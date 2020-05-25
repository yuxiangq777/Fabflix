import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
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
        JsonArray shopping_cart= (JsonArray) session.getAttribute("shopping_cart");
        String type= request.getParameter("type");
        String  title="";
        String id =request.getParameter("id");
        String  quan= request.getParameter("quan");
        String  price= request.getParameter("price");
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "SELECT title from movies where id=?";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                title = rs.getString("title");
            }
            rs.close();
            statement.close();
            dbcon.close();
        }catch (Exception e) {

                // write error message JSON object to output
                JsonObject jsonObject_ = new JsonObject();
                jsonObject_.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject_.toString());

                // set reponse status to 500 (Internal Server Error)
                response.setStatus(500);
            }
        int i=0;
        if(shopping_cart!=null) {
            for (; i < shopping_cart.size(); i++) {
                if (shopping_cart.get(i).getAsJsonObject().get("id").getAsString().equals(id)) {

                    JsonObject updated = shopping_cart.get(i).getAsJsonObject();
                    if(type.equals("update")||type.equals("add")) {
                        JsonElement quan_ = updated.remove("quan");
                        int new_quan =0;
                        if(type.equals("add")) new_quan=Integer.parseInt(quan_.getAsString()) + Integer.parseInt(quan);
                        else new_quan= Integer.parseInt(quan);
                        updated.addProperty("quan", Integer.toString(new_quan));
                        shopping_cart.set(i, updated);
                        break;
                    }
                    else{
                        shopping_cart.remove(i);
                        break;
                    }
                }
            }
            if (i == shopping_cart.size() && type.equals("add")) {
                JsonObject movie = new JsonObject();
                movie.addProperty("id", id);
                movie.addProperty("title", title);
                movie.addProperty("quan", quan);
                movie.addProperty("price", price);
                shopping_cart.add(movie);
            }
            session.setAttribute("shopping_cart", shopping_cart);
        }
        else{
            JsonObject movie_ = new JsonObject();
            movie_.addProperty("id", id);
            movie_.addProperty("title", title);
            movie_.addProperty("quan", quan);
            movie_.addProperty("price", price);
            JsonArray shopping_cart_ = new JsonArray();
            shopping_cart_.add(movie_);
            session.setAttribute("shopping_cart",shopping_cart_);
        }
        jsonObject.addProperty("status","success");
        jsonObject.addProperty("message","successfully "+type+" the movie ");
        out.write(jsonObject.toString());
        out.close();
    }
}
