import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String from= request.getParameter("from");
        String real_email="";
        String real_password="";
        String ID= "";
        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        boolean admin=true;
        // Verify reCAPTCHA
        if(!(from!=null && from.equals("android"))) {
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "reCAPTHA verification DOES NOT PASS");
                response.getWriter().write(responseJsonObject.toString());
                out.close();
                return;
            }
        }
        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/slavedb");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
            //Connection dbcon = dataSource.getConnection();

            // Declare our statement
            String query2 = "select email, password " +
                    "from employees as e " +
                    "where email= '" + email+"'";
            PreparedStatement statement2 = dbcon.prepareStatement(query2);
            ResultSet rs2 = statement2.executeQuery();
            // Iterate through each row of rs
            while (rs2.next()) {
                real_email = rs2.getString("email");
                real_password = rs2.getString("password");
            }
            rs2.close();
            statement2.close();
            if ((!email.equals(real_email)) || real_email.length()==0) {
                admin=false;
                String query = "select c.id as Id, email, password " +
                        "from customers as c " +
                        "where c.email= '" + email + "'";

                // Perform the query
                PreparedStatement statement = dbcon.prepareStatement(query);
                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs
                while (rs.next()) {
                    real_email = rs.getString("email");
                    real_password = rs.getString("password");
                    ID = rs.getString("Id");
                }
                rs.close();
                statement.close();
            }
            response.setStatus(200);
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        boolean success=false;
        if(real_password.length()!=0) success= new StrongPasswordEncryptor().checkPassword(password, real_password);
        if (email.equals(real_email) && success && (!(real_email.length()==0))) {
            // Login success:

            // set this user into the session
            request.getSession().setAttribute("user", new User(email));
            request.getSession().setAttribute("user_id",ID);
            if(admin) responseJsonObject.addProperty("status", "admin_success");
            else responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");

            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            if ((!email.equals(real_email)) || real_email.length()==0) {
                responseJsonObject.addProperty("message", "user email " + email + " doesn't exist");
            } else {
                responseJsonObject.addProperty("message", "incorrect password");
            }
        }
        response.getWriter().write(responseJsonObject.toString());
        out.close();
    }
}
