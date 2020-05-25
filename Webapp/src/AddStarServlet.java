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
@WebServlet(name = "AddStarServlet", urlPatterns = "/api/addstar")
public class AddStarServlet extends HttpServlet {
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
        String name = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        if(name==null || name.length()==0){
            JsonObject jobject = new JsonObject();
            jobject.addProperty("message","Failed! Name is required!" );
            out.write(jobject.toString());
            out.close();
            return;
        }
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            String id_query= "select max(id) as Id from stars";
            PreparedStatement statement_ = dbcon.prepareStatement(id_query);
            // Perform the query
            ResultSet rs_ = statement_.executeQuery();
            int max_id=0;
            String s_part="";
            while(rs_.next()){
                max_id= Integer.parseInt(rs_.getString("Id").replaceAll("[^0-9]", ""))+1;
                s_part= rs_.getString("Id").substring(0,2);
            }
            String id= s_part+Integer.toString(max_id);
            String query="";
            if(birthYear!=null && birthYear.length()!=0){
                query= "insert into stars(id,name,birthYear) VALUES('"+id+"', '"+name+"', "+birthYear+")";
            }
            else{
                query= "insert into stars(id,name) VALUES('"+id+"', '"+name+"')";
            }
            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);
            // Perform the query
            statement.executeUpdate();
            JsonObject nj= new JsonObject();
            nj.addProperty("message","Success! New star ID is: "+id );
            // Iterate through each row of rs
            // write JSON string to output
            out.write(nj.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            rs_.close();
            statement_.close();
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