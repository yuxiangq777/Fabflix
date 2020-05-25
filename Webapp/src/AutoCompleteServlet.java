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
import java.util.ArrayList;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "AutoCompleteServlet", urlPatterns = "/api/autocomplete")
public class AutoCompleteServlet extends HttpServlet {
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
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            JsonArray jsonArray = new JsonArray();
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }
            String db_query = "select distinct mo.id as id, title " +
                    "from movies as mo ";
            String[] splited = query.trim().split("\\s+");
            String to_match="";
            for(int i=0;i<splited.length;i++){
                System.out.println(splited[i]);
                to_match+= "+"+splited[i]+"*";
                if(i!=splited.length-1) to_match+=" ";
            }
            db_query+=" where match (title) against ('"+to_match+"' in boolean mode) ";
            db_query+=" LIMIT 10 ";
            PreparedStatement statement = dbcon.prepareStatement(db_query);
            ResultSet rs = statement.executeQuery();

            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("id");
                String title=rs.getString("title");
                jsonArray.add(generateJsonObject(id, title));
            }
            out.write(jsonArray.toString());
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
    private static JsonObject generateJsonObject(String ID, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("id", ID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}
