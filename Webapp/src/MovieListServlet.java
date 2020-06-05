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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
        String  by= request.getParameter("by");
        String title= request.getParameter("title");
        String year= request.getParameter("year");
        String director= request.getParameter("director");
        String starname= request.getParameter("starname");
        String genre= request.getParameter("genre");
        String order= request.getParameter("order");
        String page= request.getParameter("page");
        String ipp= request.getParameter("ipp");
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

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
            String subq= "movies as m";
            if(by.equals("browse")){
                if (genre != null && genre.length()!=0){
                    subq = "(select distinct mo.id as id, title, year, director " +
                            "from movies as mo, genres_in_movies as gim, genres as g "+
                            "where g.id= gim.genreId and mo.id=gim.movieId and g.name='"+genre+"') as m";
                }
                else if(title!=null && title.length()!=0){
                    if(title.equals("*")){
                        subq = "(select distinct mo.id as id, title, year, director " +
                                "from movies as mo "+
                                "where title regexp '^[^a-z0-9]') as m";
                    }
                    else {
                        subq = "(select distinct mo.id as id, title, year, director " +
                                "from movies as mo " +
                                "where lower(title) like lower('" + title + "%')) as m";
                    }
                }
            }
            else if(by.equals("search")){
                subq = "(select distinct mo.id as id, title, year, director " +
                        "from movies as mo, stars_in_movies as sim, stars as s "+
                        "where mo.id = sim.movieId and sim.starId = s.id ";
                if(starname!=null && starname.length()!=0){
                    subq+=" and lower(s.name) like lower('%"+starname+"%')";
                }
                if(title!=null && title.length()!=0){
                    subq+=" and lower(mo.title) like lower('%"+title+"%')";
                }
                if(year!=null && year.length()!=0){
                    subq+=" and mo.year="+year;
                }
                if(director!=null && director.length()!=0){
                    subq+=" and lower(mo.director) like lower('%"+director+"%')";
                }
                subq += ") as m";
            }
            else if(by.equals("main")){
                subq = "(select distinct mo.id as id, title, year, director " +
                        "from movies as mo, stars_in_movies as sim, stars as s "+
                        "where mo.id = sim.movieId and sim.starId = s.id ";
                if(title!=null && title.length()!=0){
                    String[] splited = title.trim().split("\\s+");
                    String to_match="";
                    for(int i=0;i<splited.length;i++){
                        System.out.println(splited[i]);
                        to_match+= "+"+splited[i]+"*";
                        if(i!=splited.length-1) to_match+=" ";
                    }
                    subq+=" and match (title) against ('"+to_match+"' in boolean mode)";
                }
                subq += ") as m";
            }
            // Declare our statement

            int pa = Integer.parseInt(page);
            int ipp_i= Integer.parseInt(ipp);
            int offset= (pa-1)*ipp_i;
            String order_q= "";
            if(order!=null && order.length()!=0){
                if (order.equals("t_asc")){
                    order_q= "order by title asc ";
                }
                else if (order.equals("t_dsc")){
                    order_q= "order by title desc ";
                }
                else if (order.equals("r_asc")){
                    order_q= "order by rating asc ";
                }
                else if (order.equals("r_dsc")){
                    order_q= "order by rating desc ";
                }
                else if (order.equals("t_asc_r_asc")){
                    order_q= "order by title asc, rating asc ";
                }
                else if (order.equals("t_asc_r_dsc")){
                    order_q= "order by title asc, rating desc ";
                }
                else if (order.equals("t_dsc_r_asc")){
                    order_q= "order by title desc, rating asc ";
                }
                else if (order.equals("t_dsc_r_dsc")){
                    order_q= "order by title desc, rating desc ";
                }
                else if (order.equals("r_asc_t_asc")){
                    order_q= "order by rating asc, title asc ";
                }
                else if (order.equals("r_asc_t_dsc")){
                    order_q= "order by rating asc, title desc ";
                }
                else if (order.equals("r_dsc_t_asc")){
                    order_q= "order by rating desc, title asc ";
                }
                else if (order.equals("r_dsc_t_dsc")){
                    order_q= "order by rating desc, title desc ";
                }
            }
            String query = "select distinct m.id as movieId, title, year, director, rating " +
                    "from "+subq+", ratings as r " +
                    "where m.id=r.movieId " +
                    order_q +
                    "LIMIT "+ipp+" "+
                    "OFFSET "+Integer.toString(offset);
            // Perform the query
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String year_ = rs.getString("year");
                String director_ = rs.getString("director");
                String rating = rs.getString("rating");
                JsonArray genre_list = new JsonArray();
                JsonObject jsonObject = new JsonObject();
                JsonArray star_list = new JsonArray();
                JsonArray starid_list = new JsonArray();
                String query1 = "select distinct m.id as movieId, g.name as genreName " +
                        "from movies as m, genres_in_movies as gim, genres as g "+
                        "where g.id= gim.genreId and m.id=gim.movieId and m.id = '"+movieId+"' order by g.name LIMIT 3";
                PreparedStatement statement1 = dbcon.prepareStatement(query1);
                ResultSet rs1 = statement1.executeQuery();
                while (rs1.next()){
                    String genreName = rs1.getString("genreName");
                    genre_list.add(genreName);
                }

                String query2 = "select temp.starNamea as starName, temp.starIds as starId from (select distinct s.name as starNamea, starId as starIds " +
                        "from movies as m, stars_in_movies as sim, stars as s "+
                        "where m.id = sim.movieId and sim.starId = s.id and m.id = '"+movieId+"') as temp, movies as ma, stars_in_movies as sima "+
                        "where ma.id = sima.movieId and sima.starId = temp.starIds " +
                        "group by temp.starIds " +
                        "order by count(*) desc, starName limit 3";
                PreparedStatement statement2 = dbcon.prepareStatement(query2);
                ResultSet rs2 = statement2.executeQuery();
                while (rs2.next()) {
                    String starName = rs2.getString("starName");
                    String _starid = rs2.getString("starId");
                    star_list.add(starName);
                    starid_list.add(_starid);
                }
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("year", year_);
                jsonObject.addProperty("director", director_);
                jsonObject.addProperty("rating", rating);
                jsonObject.add("genre_list",genre_list);
                jsonObject.add("star_list",star_list);
                jsonObject.add("starid_list",starid_list);
                jsonArray.add(jsonObject);
                rs1.close();
                statement1.close();
                rs2.close();
                statement2.close();
            }
            if(jsonArray.size()!=0){
                request.getSession().setAttribute("search_result","by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+order+"&page="+page+"&ipp="+ipp);
            }
            // write JSON string to output
            out.write(jsonArray.toString());
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
