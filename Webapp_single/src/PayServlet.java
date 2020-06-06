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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
@WebServlet(name = "PayServlet", urlPatterns = "/api/pay")
public class PayServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String cnumber = request.getParameter("cnumber");
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/masterdb");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
            //Connection dbcon = dataSource.getConnection();

            // Declare our statement


            String query = "select * " +
                    "from creditcards " +
                    "where id= '" +cnumber +"' and firstName='"+fname+"' and lastName='"+lname+"' and expiration='"+year+"/"+month+"/"+day+"' ";
            // Perform the query
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            responseJsonObject.addProperty("status","");
            // Iterate through each row of rs
            while (rs.next()) {
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "Payment successful");
                jsonArray.add(responseJsonObject);
                HttpSession session = request.getSession();
                JsonArray shopping_cart= (JsonArray) session.getAttribute("shopping_cart");
                String user_id=(String) session.getAttribute("user_id");
                for (int i = 0; i < shopping_cart.size(); i++){
                    String movieid=shopping_cart.get(i).getAsJsonObject().get("id").getAsString();
                    String quan= shopping_cart.get(i).getAsJsonObject().get("quan").getAsString();
                    Date date = new Date();
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                    String time= df.format(date);
                    String query2 = "INSERT INTO sales (customerId,movieId,saleDate,copies) VALUES("+user_id+",'"+movieid+"', '"+time+"', '"+quan+"')";
                    PreparedStatement statement1 = dbcon.prepareStatement(query2);
                    statement1.executeUpdate();
                    String query3= "select id from sales order by id desc limit 1 ";
                    PreparedStatement statement2 = dbcon.prepareStatement(query3);
                    ResultSet rs2 = statement2.executeQuery();
                    while (rs2.next()){
                        String a= rs2.getString("id");
                        if(session.getAttribute("sale_list")==null){
                            JsonArray sale_list=new JsonArray();
                            sale_list.add(a);
                            session.setAttribute("sale_list",sale_list);
                        }
                        else{
                            JsonArray sale_list= (JsonArray) session.getAttribute("sale_list");
                            sale_list.add(a);
                            session.setAttribute("sale_list",sale_list);
                        }
                    }
                    statement1.close();
                    statement2.close();
                    rs2.close();
                }
                break;
            }
            if(responseJsonObject.get("status").getAsString().equals("")){
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Wrong information");
            }
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }


        response.getWriter().write(responseJsonObject.toString());
        out.close();
    }
}
