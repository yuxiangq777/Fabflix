
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;  // Import the File class
import java.io.IOException;
import java.io.FileWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastParse extends DefaultHandler {

    List<Cast> myEmpls;
    private String tempVal;

    //to maintain context
    private Cast tempEmp;

    public CastParse() {
        myEmpls = new ArrayList<Cast>();
    }

    public void runExample() {
        parseDocument();
        insertData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void insertData() {
        System.out.println("No of Employees '" + myEmpls.size() + "'.");

    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            //create a new instance of employee
            tempEmp = new Cast();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal=tempVal.trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException{

        if (qName.equalsIgnoreCase("m")) {
            myEmpls.add(tempEmp);
        } else if (qName.equalsIgnoreCase("a")) {
            tempEmp.setStar(tempVal);
        } else if (qName.equalsIgnoreCase("f")) {
            tempEmp.setMovie(tempVal);
        }

    }

    public static void main(String[] args) throws Exception{
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        System.out.println("Start parsing casts124.xml!");
        String query = "INSERT INTO stars values(?,?,?)";
        PreparedStatement statement = dbcon.prepareStatement(query);
        //String query2= "select id from stars where name=?";
        //PreparedStatement statement2 = dbcon.prepareStatement(query2);
        String id_query = "select max(id) as Id from stars";
        PreparedStatement statement_ = dbcon.prepareStatement(id_query);
        ResultSet rs_ = statement_.executeQuery();
        //String query3= "select * from stars_in_movies where starId=? and movieId=?";
        //PreparedStatement statement3 = dbcon.prepareStatement(query3);
        String query4= "INSERT INTO stars_in_movies values(?,?)";
        PreparedStatement statement4 = dbcon.prepareStatement(query4);
        //String query5= "select * from movies where id=?";
        //PreparedStatement statement5 = dbcon.prepareStatement(query5);
        HashMap<String,String> movie_map= new HashMap<>();
        String query_all= "select id, title from movies";
        PreparedStatement statement_all = dbcon.prepareStatement(query_all);
        ResultSet rs_all = statement_all.executeQuery();
        while(rs_all.next()){
            movie_map.put(rs_all.getString("id"),rs_all.getString("title"));
        }
        statement_all.close();
        rs_all.close();
        HashMap<String,String> star_map= new HashMap<>();
        String query_all2= "select id, name from stars";
        PreparedStatement statement_all2 = dbcon.prepareStatement(query_all2);
        ResultSet rs_all2 = statement_all2.executeQuery();
        while(rs_all2.next()){
            star_map.put(rs_all2.getString("id"),rs_all2.getString("name"));
        }
        statement_all2.close();
        rs_all2.close();
        HashMap<String,String> star_movie_map= new HashMap<>();
        String query_all3= "select * from stars_in_movies";
        PreparedStatement statement_all3 = dbcon.prepareStatement(query_all3);
        ResultSet rs_all3 = statement_all3.executeQuery();
        while(rs_all3.next()){
            star_movie_map.put(rs_all3.getString("starId"),rs_all3.getString("movieId"));
        }
        statement_all3.close();
        rs_all3.close();
        FileWriter myWriter = new FileWriter("Inconsistency.txt",true);
        int max_id=0;
        String s_part="";
        while(rs_.next()){
            max_id= Integer.parseInt(rs_.getString("Id").replaceAll("[^0-9]", ""));
            s_part= rs_.getString("Id").substring(0,2);
        }
        rs_.close();
        CastParse spe = new CastParse();
        spe.runExample();
        Iterator<Cast> it = spe.myEmpls.iterator();
        while (it.hasNext()) {
            Cast temp=it.next();
            String star_name= temp.getStar();
            String movieId= temp.getMovie();
            //statement5.setString(1,movieId);
            //ResultSet rs11=statement5.executeQuery();
            if(movie_map.containsKey(movieId)) {
                String starId = "";
                for(Map.Entry e: star_map.entrySet()){
                    if(e.getValue().toString().equals(star_name)) starId= e.getKey().toString();
                }
                //statement2.setString(1, star_name);
                //ResultSet rs1 = statement2.executeQuery();
                //if (rs1.next()) starId = rs1.getString("id");
                if (starId == "") {
                    max_id += 1;
                    starId = s_part + Integer.toString(max_id);
                    statement.setString(1, starId);
                    statement.setString(2, star_name);
                    statement.setInt(3, java.sql.Types.INTEGER);
                    //statement.executeUpdate();
                    statement.addBatch();
                    star_map.put(starId,star_name);
                }
                //rs1.close();
                //statement3.setString(1, starId);
                //statement3.setString(2, movieId);
                //ResultSet rs2 = statement3.executeQuery();
                if (star_movie_map.containsKey(starId) && star_movie_map.get(starId).equals(movieId)) {
                    try {
                        myWriter.write("Star-Movie pair already exist: starId=" + starId + "; movieId=" + movieId + "\n");
                    } catch (IOException ea) {
                        System.out.println("An error occurred.");
                        ea.printStackTrace();
                    }
                } else {
                    statement4.setString(1, starId);
                    statement4.setString(2, movieId);
                    //statement4.executeUpdate();
                    statement4.addBatch();
                    star_movie_map.put(starId,movieId);
                }
                //rs2.close();
            }
            //else{
            //    try {
            //        myWriter.write("Movie ID: "+movieId+" doesn't exist. Can't satisfy foreign key constraint\n");
            //    } catch (IOException ea) {
            //        System.out.println("An error occurred.");
            //        ea.printStackTrace();
            //    }
            //}
        }
        statement.executeBatch();
        statement4.executeBatch();
        myWriter.close();
        statement.close();
        //statement2.close();
        //statement3.close();
        statement4.close();
        statement_.close();
        System.out.println("Finish parsing casts124.xml!");
        dbcon.close();
    }

}
