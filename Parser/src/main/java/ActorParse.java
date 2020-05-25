
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;  // Import the File class
import java.io.IOException;
import java.io.FileWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ActorParse extends DefaultHandler {

    List<Actor> myEmpls;
    private String tempVal;

    //to maintain context
    private Actor tempEmp;

    public ActorParse() {
        myEmpls = new ArrayList<Actor>();
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
            sp.parse("actors63.xml", this);

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
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempEmp = new Actor();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal=tempVal.trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException{

        if (qName.equalsIgnoreCase("actor")) {
            myEmpls.add(tempEmp);
        } else if (qName.equalsIgnoreCase("stagename")) {
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            try {
                tempVal.replaceAll("\\s+", "");
                tempEmp.setYear(Integer.parseInt(tempVal));
            } catch (NumberFormatException e) {
                if (!(tempVal == null || tempVal.length() == 0 || tempVal.equals(" ") || tempVal.equals("n.a."))) {
                    try {
                        FileWriter myWriter = new FileWriter("Inconsistency.txt",true);
                        myWriter.write("Bad Actor input with birthYear: " + tempVal+"\n");
                        myWriter.close();
                    } catch (IOException ea) {
                        System.out.println("An error occurred.");
                        ea.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        File inconsist= new File("Inconsistency.txt");
        System.out.println("Start parsing actors63.xml!");
        inconsist.createNewFile();
        Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        String id_query = "select max(id) as Id from stars";
        PreparedStatement statement_ = dbcon.prepareStatement(id_query);
        // Perform the query
        ResultSet rs_ = statement_.executeQuery();
        int max_id=0;
        String s_part="";
        while(rs_.next()){
            max_id= Integer.parseInt(rs_.getString("Id").replaceAll("[^0-9]", ""));
            s_part= rs_.getString("Id").substring(0,2);
        }
        ActorParse spe = new ActorParse();
        spe.runExample();
        Iterator<Actor> it = spe.myEmpls.iterator();
        String query = "INSERT INTO stars values(?,?,?)";
        PreparedStatement statement = dbcon.prepareStatement(query);
        HashMap<String,String> star_map= new HashMap<>();
        String query_all= "select id, name from stars";
        PreparedStatement statement3 = dbcon.prepareStatement(query_all);
        ResultSet rs_all = statement3.executeQuery();
        while(rs_all.next()){
            star_map.put(rs_all.getString("id"),rs_all.getString("name"));
        }
        while (it.hasNext()) {
            Actor temp=it.next();
            if(!temp.getName().equals("")) {
                if(!star_map.containsValue(temp.getName())) {
                    max_id += 1;
                    statement.setString(1, s_part + Integer.toString(max_id));
                    statement.setString(2, temp.getName());
                    int y = temp.getYear();
                    if (y == 0) statement.setInt(3, java.sql.Types.INTEGER);
                    else statement.setInt(3, y);
                    statement.addBatch();
                    star_map.put(s_part + Integer.toString(max_id),temp.getName());
                    //statement.executeUpdate();
                }
            }
            else{
                try {
                    FileWriter myWriter = new FileWriter("Inconsistency.txt",true);
                    myWriter.write("Bad Actor input with no name "+"\n");
                    myWriter.close();
                } catch (IOException ea) {
                    System.out.println("An error occurred.");
                    ea.printStackTrace();
                }
            }
        }
        statement.executeBatch();
        System.out.println("Finish parsing actors63.xml!");
        statement.close();
        statement_.close();
        rs_.close();
        dbcon.close();

    }

}
