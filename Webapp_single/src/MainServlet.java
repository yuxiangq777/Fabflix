import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MainServlet", urlPatterns = "/api/main")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starname = request.getParameter("starname");
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        if (!(!(title!=null && title.length()!=0) && !(year!=null && year.length()!=0) && !(director!=null && director.length()!=0) && !( starname!=null && starname.length()!=0))) {
            // Login success:

            // set this user into the session
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("title",title);
            responseJsonObject.addProperty("year",year);
            responseJsonObject.addProperty("director",director);
            responseJsonObject.addProperty("starname",starname);
        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
        }
        response.getWriter().write(responseJsonObject.toString());
        out.close();
    }
}