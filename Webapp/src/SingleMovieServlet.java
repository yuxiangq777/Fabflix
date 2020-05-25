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
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
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
        String id = request.getParameter("id");
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "select distinct m.id as movieId, title, year, director, rating " +
                    "from movies as m, ratings as r " +
                    "where m.id=r.movieId and m.id = ?";

            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String rating = rs.getString("rating");
                //String genreName = rs.getString("genreName");
                //String starId = rs.getString("starId");
                //String starName = rs.getString("starName");
                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);
                //jsonObject.addProperty("genre_name", genreName);
                //jsonObject.addProperty("star_id", starId);
                //jsonObject.addProperty("star_name", starName);
                jsonArray.add(jsonObject);
            }
            String query1 = "select distinct m.id as movieId, g.name as genreName " +
                    "from movies as m, genres_in_movies as gim, genres as g "+
                    "where g.id= gim.genreId and m.id=gim.movieId and m.id = ? order by genreName";

            PreparedStatement statement1 = dbcon.prepareStatement(query1);
            statement1.setString(1, id);

            // Perform the query
            ResultSet rs1 = statement1.executeQuery();
            JsonObject jsonObject1 = new JsonObject();
            JsonArray genre_list = new JsonArray();
            // Iterate through each row of rs
            while (rs1.next()) {
                String genreName = rs1.getString("genreName");
                genre_list.add(genreName);
                // Create a JsonObject based on the data we retrieve from rs


            }
            jsonObject1.add("genre_list", genre_list);
            jsonArray.add(jsonObject1);

            String query2 = "select temp.starNamea as starName, temp.starIds as starId, count(*) as count_m from (select distinct s.name as starNamea, starId as starIds " +
                    "from movies as m, stars_in_movies as sim, stars as s "+
                    "where m.id = sim.movieId and sim.starId = s.id and m.id = ?) as temp, movies as ma, stars_in_movies as sima "+
                    "where ma.id = sima.movieId and sima.starId = temp.starIds " +
                    "group by temp.starIds " +
                    "order by count(*) desc, starName";
            PreparedStatement statement2 = dbcon.prepareStatement(query2);
            statement2.setString(1, id);

            // Perform the query
            ResultSet rs2 = statement2.executeQuery();
            JsonObject jsonObject2 = new JsonObject();
            JsonArray star_list = new JsonArray();
            JsonArray starid_list = new JsonArray();
            JsonArray count_ = new JsonArray();
            while (rs2.next()) {
                String starName = rs2.getString("starName");
                String _starid = rs2.getString("starId");
                String count__ = rs2.getString("count_m");
                star_list.add(starName);
                starid_list.add(_starid);
                count_.add(count__);
                // Create a JsonObject based on the data we retrieve from rs
            }
            jsonObject2.add("star_list", star_list);
            jsonObject2.add("starid_list", starid_list);
            jsonObject2.add("count_list", count_);
            jsonArray.add(jsonObject2);
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            rs1.close();
            statement1.close();
            rs2.close();
            statement2.close();
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
