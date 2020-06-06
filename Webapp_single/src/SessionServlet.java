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
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "SessionServlet", urlPatterns = "/api/session")
public class SessionServlet extends HttpServlet {
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
        String type= request.getParameter("type");
        String result= (String) session.getAttribute("search_result");
        if (result!=null) jsonObject.addProperty("search_result",result);
        else jsonObject.addProperty("search_result","");
        JsonArray shopping_cart= (JsonArray) session.getAttribute("shopping_cart");
        if (shopping_cart!=null) jsonObject.add("shopping_cart",shopping_cart);
        else jsonObject.addProperty("shopping_cart","");
        if (shopping_cart!=null) {
            int total=0;
            for (int i = 0; i < shopping_cart.size(); i++) {
                int p = Integer.parseInt(shopping_cart.get(i).getAsJsonObject().get("price").getAsString());
                int q = Integer.parseInt(shopping_cart.get(i).getAsJsonObject().get("quan").getAsString());
                total+=p*q;
            }
            session.setAttribute("total_price",total);
            jsonObject.addProperty("total_price",Integer.toString(total));
        }
        JsonArray sale_list= (JsonArray) session.getAttribute("sale_list");
        if(sale_list!=null) jsonObject.add("sale_list",sale_list);
        else jsonObject.addProperty("sale_list","");

        out.write(jsonObject.toString());
        out.close();
        if(type!=null && type.equals("clear")){
            session.setAttribute("sale_list",null);
            session.setAttribute("shopping_cart",null);
            session.setAttribute("total_price",null);
        }
    }
}
