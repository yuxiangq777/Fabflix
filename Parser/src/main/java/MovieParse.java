
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

public class MovieParse extends DefaultHandler {

    List<Movie> myEmpls;
    private String tempVal;
    private String tempdirector;
    //to maintain context
    private Movie tempEmp;
    private List<String> tempgenre=new ArrayList<String>();

    public MovieParse() {
        myEmpls = new ArrayList<Movie>();
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
            sp.parse("mains243.xml", this);

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
        System.out.println("No of Movies '" + myEmpls.size() + "'.");

    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("dirname")) {
            //create a new instance of employee
            tempdirector="";
        }else if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempEmp = new Movie();
            tempEmp.setDirector(tempdirector);
        }else if (qName.equalsIgnoreCase("cats")) {
            //create a new instance of employee
            tempgenre.clear();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal=tempVal.trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException{

        if (qName.equalsIgnoreCase("film")) {

            myEmpls.add(tempEmp);
        } else if (qName.equalsIgnoreCase("dirname")||qName.equalsIgnoreCase("dirn")) {
            tempdirector=tempVal;
        }else if (qName.equalsIgnoreCase("fid")) {
            tempEmp.setID(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            try {
                tempVal.replaceAll("\\s+","");
                tempEmp.setYear(Integer.parseInt(tempVal));
            } catch (NumberFormatException e){
                if(!(tempVal==null || tempVal.length()==0 || tempVal.equals(" ")||tempVal.equals("n.a."))){
                    try {
                        FileWriter myWriter = new FileWriter("Inconsistency.txt",true);
                        myWriter.write("Bad Movie: ID="+tempEmp.getID()+"input with birthYear: "+tempVal+"\n");
                        myWriter.close();
                    } catch (IOException ea) {
                        System.out.println("An error occurred.");
                        ea.printStackTrace();
                    }
                }
            }
        }else if (qName.equalsIgnoreCase("cat")) {
            tempVal=tempVal.trim().toLowerCase();
            if(tempVal.equals("actn")) tempEmp.add_genre("Violence");
            else if(tempVal.equals("advt")) tempEmp.add_genre("Adventure");
            else if(tempVal.equals("avga")||tempVal.equals("Avant Garde")) tempEmp.add_genre("Avant Garde");
            else if(tempVal.equals("camp")) tempEmp.add_genre("Now - camp");
            else if(tempVal.equals("cart")) tempEmp.add_genre("Cartoon");
            else if(tempVal.equals("comd")|| tempVal.equals("Comd ")) tempEmp.add_genre("Comedy");
            else if(tempVal.equals("disa")) tempEmp.add_genre("Disaster");
            else if(tempVal.equals("docu")) tempEmp.add_genre("Documentary");
            else if(tempVal.equals("dram")) tempEmp.add_genre("Drama");
            else if(tempVal.equals("epic")) tempEmp.add_genre("Epic");
            else if(tempVal.equals("faml")) tempEmp.add_genre("Family");
            else if(tempVal.equals("fant")) tempEmp.add_genre("Fantasy");
            else if(tempVal.equals("hist")) tempEmp.add_genre("History");
            else if(tempVal.equals("horr")) tempEmp.add_genre("Horror");
            else if(tempVal.equals("musc")) tempEmp.add_genre("Musical");
            else if(tempVal.equals("myst")) tempEmp.add_genre("Mystery");
            else if(tempVal.equals("noir")) tempEmp.add_genre("Black");
            else if(tempVal.equals("porn")) tempEmp.add_genre("Pornography");
            else if(tempVal.equals("romt")) tempEmp.add_genre("Romantic");
            else if(tempVal.toLowerCase().equals("scfi")||tempVal.equals("s.f.")||tempVal.toLowerCase().equals("scif")) tempEmp.add_genre("Science Fiction");
            else if(tempVal.equals("surl")||tempVal.equals("surr")) tempEmp.add_genre("Surreal");
            else if(tempVal.toLowerCase().equals("susp")) tempEmp.add_genre("Thriller");
            else if(tempVal.equals("west")) tempEmp.add_genre("Western");
            else if(tempVal.toLowerCase().equals("biop")) tempEmp.add_genre("Biographical Picture");
            else if(tempVal.equals("tv")) tempEmp.add_genre("TV show");
            else if(tempVal.equals("scat")||tempVal.equals("muscl")||tempVal.equals("sati")) tempEmp.add_genre(tempVal);
            else if(tempVal.equals("tvs")) tempEmp.add_genre("TV series");
            else if(tempVal.equals("tvm")) tempEmp.add_genre("TV miniseries");
            else if(tempVal.toLowerCase().equals("cnrb")||tempVal.toLowerCase().equals("cnr")) tempEmp.add_genre("Cops and robbers");
            else if(!(tempVal.equals("")||tempVal.equals("ctxx")||tempVal.equals(" "))){
                try {
                    FileWriter myWriter = new FileWriter("Inconsistency.txt",true);
                    myWriter.write("bad genre: "+tempVal+"\n");
                    myWriter.close();
                } catch (IOException ea) {
                    System.out.println("An error occurred.");
                    ea.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) throws Exception{
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        System.out.println("Start parsing mains243.xml!");
        MovieParse spe = new MovieParse();
        spe.runExample();
        Iterator<Movie> it = spe.myEmpls.iterator();
        HashMap<String,String> movie_map= new HashMap<>();
        String query_all= "select id, title from movies";
        PreparedStatement statement_all = dbcon.prepareStatement(query_all);
        ResultSet rs_all = statement_all.executeQuery();
        while(rs_all.next()){
            movie_map.put(rs_all.getString("id"),rs_all.getString("title"));
        }
        statement_all.close();
        rs_all.close();
        HashMap<String,String> genre_map= new HashMap<>();
        String query_all2= "select id, name from genres";
        PreparedStatement statement_all2 = dbcon.prepareStatement(query_all2);
        ResultSet rs_all2 = statement_all2.executeQuery();
        while(rs_all2.next()){
            genre_map.put(rs_all2.getString("id"),rs_all2.getString("name"));
        }
        statement_all2.close();
        rs_all2.close();
        HashMap<String,String> genre_movie_map= new HashMap<>();
        String query_all3= "select * from genres_in_movies";
        PreparedStatement statement_all3 = dbcon.prepareStatement(query_all3);
        ResultSet rs_all3 = statement_all3.executeQuery();
        while(rs_all3.next()){
            genre_movie_map.put(rs_all3.getString("genreId"),rs_all3.getString("movieId"));
        }
        statement_all3.close();
        rs_all3.close();
        String query = "INSERT INTO movies values(?,?,?,?)";
        PreparedStatement statement = dbcon.prepareStatement(query);
        //String query2= "select id from movies where id=?";
        //PreparedStatement statement2 = dbcon.prepareStatement(query2);
        //String query3= "select id from genres where name=?";
        //PreparedStatement statement3 = dbcon.prepareStatement(query3);
        String query4= "INSERT INTO genres(name) values(?)";
        PreparedStatement statement4 = dbcon.prepareStatement(query4);
        String query5= "INSERT INTO genres_in_movies values(?,?)";
        PreparedStatement statement5 = dbcon.prepareStatement(query5);
        String query6= "select max(id) as id from genres";
        PreparedStatement statement6 = dbcon.prepareStatement(query6);
        ResultSet rs10 = statement6.executeQuery();
        int max_genre_id=0;
        if(rs10.next())  max_genre_id= rs10.getInt("id");
        rs10.close();
        //String query7= "select * from genres_in_movies where genreId=? and movieId=?";
        //PreparedStatement statement7 = dbcon.prepareStatement(query7);
        FileWriter myWriter = new FileWriter("Inconsistency.txt",true);
        String query8= "INSERT INTO ratings values(?,?,?)";
        PreparedStatement statement8 = dbcon.prepareStatement(query8);
        while (it.hasNext()) {
            Movie temp=it.next();
            String ID= temp.getID();
            String title= temp.getName();
            int year=temp.getYear();
            String director=temp.getDirector();
            List<String> genre_list=temp.getGenre_list();
            if(ID.equals("")|| title.equals("") || year==0 || director.equals("")){
                try {
                    if(title.equals(""))  myWriter.write("bad movie: ID= "+ID+" lack of title\n");
                    else if(year==0)  myWriter.write("bad movie: ID= "+ID+" lack of year\n");
                    else if(director.equals(""))  myWriter.write("bad movie: ID= "+ID+" lack of director\n");
                } catch (IOException ea) {
                    System.out.println("An error occurred.");
                    ea.printStackTrace();
                }
            }
            else{
                if(movie_map.containsKey(ID)){
                    try {
                        myWriter.write("bad movie: ID= "+ID+" already in the database\n");
                    } catch (IOException ea) {
                        System.out.println("An error occurred.");
                        ea.printStackTrace();
                    }
                }
                else{
                    statement.setString(1,ID);
                    statement.setString(2,title);
                    statement.setInt(3,year);
                    statement.setString(4,director);
                    statement.addBatch();
                    movie_map.put(ID,title);
                    //statement.executeUpdate();
                    statement8.setString(1,ID);
                    statement8.setFloat(2,1.0f);
                    statement8.setInt(3,1);
                    statement8.addBatch();
                    //statement8.executeUpdate();
                    for(int i=0;i<genre_list.size();i++){
                        //statement3.setString(1,genre_list.get(i));
                        //ResultSet rs2=statement3.executeQuery();
                        String genre_id="";
                        for(Map.Entry e: genre_map.entrySet()){
                            if(e.getValue().toString().equals(genre_list.get(i))) genre_id= e.getKey().toString();
                        }
                        //while(rs2.next()) genre_id=Integer.parseInt(rs2.getString("id"));
                        //rs2.close();
                        if(genre_id.equals("")){
                            statement4.setString(1,genre_list.get(i));
                            statement4.executeUpdate();
                            max_genre_id++;
                            genre_id= Integer.toString(max_genre_id);
                            genre_map.put(genre_id,genre_list.get(i));
                            //ResultSet rs3=statement6.executeQuery();
                            //while(rs3.next()) genre_id= Integer.parseInt(rs3.getString("id"));
                        }
                        //statement7.setInt(1,genre_id);
                        //statement7.setString(2,ID);
                        //ResultSet rs3=statement7.executeQuery();
                        if(genre_movie_map.containsKey(genre_id) && genre_movie_map.get(genre_id).equals(ID)){
                            try {
                                myWriter.write("Genre-Movie pair already exist: genreId="+genre_id+"; movieId="+ID+"\n");
                            } catch (IOException ea) {
                                System.out.println("An error occurred.");
                                ea.printStackTrace();
                            }
                        }
                        else {
                            statement5.setInt(1, Integer.parseInt(genre_id));
                            statement5.setString(2, ID);
                            //statement5.executeUpdate();
                            statement5.addBatch();
                            genre_movie_map.put(genre_id,ID);
                        }
                        //rs3.close();
                    }
                }
                //rs1.close();
            }
        }
        statement.executeBatch();
        statement8.executeBatch();
        statement5.executeBatch();
        System.out.println("Finish parsing mains243.xml!");
        myWriter.close();
        statement.close();
        //statement2.close();
        //statement3.close();
        statement4.close();
        statement5.close();
        statement6.close();
        //statement7.close();
        statement8.close();
        dbcon.close();
    }

}
