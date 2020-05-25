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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addmovie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        String Year = request.getParameter("Year");
        String director = request.getParameter("director");
        String star_name = request.getParameter("star_name");
        String genre_name = request.getParameter("genre_name");
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        if(title==null || title.length()==0||Year==null || Year.length()==0||director==null || director.length()==0||star_name==null || star_name.length()==0||genre_name==null || genre_name.length()==0){
            JsonObject jobject = new JsonObject();
            jobject.addProperty("message","Failed! Please fullfill all fields!" );
            out.write(jobject.toString());
            out.close();
            return;
        }
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            String query= "call add_movie('"+title+"', "+Year+", '"+director+"', '"+star_name+"', '"+genre_name+"')";
            PreparedStatement statement = dbcon.prepareStatement(query);
            // Perform the query
            ResultSet rs = statement.executeQuery();
            String message="";
            while(rs.next()){
                message= rs.getString("answer");
            }
            JsonObject nj= new JsonObject();
            nj.addProperty("message",message);
            // Iterate through each row of rs
            // write JSON string to output
            out.write(nj.toString());
            // set response status to 200 (OK)
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
        out.close();

    }

}