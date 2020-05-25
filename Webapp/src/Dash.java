import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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
import org.jasypt.util.password.StrongPasswordEncryptor;
@WebServlet(name = "Dash", urlPatterns = "/api/dash")
public class Dash extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
        // Retrieve parameter id from url request.
        PrintWriter out = response.getWriter();
        JsonObject jsonObject=new JsonObject();
        HttpSession session = request.getSession();
        User result= (User) session.getAttribute("user");
        if (result == null) jsonObject.addProperty("status","fail");
        else if(result.email.equals("classta@email.edu")) jsonObject.addProperty("status","success");
        else jsonObject.addProperty("status","fail");
        out.write(jsonObject.toString());
        out.close();
    }
}