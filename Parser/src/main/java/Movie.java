import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class Movie {

    private String id;
    private String title;
    private int year;
    private String director;
    private List<String> genre_list;
    public Movie(){
        id="";
        title="";
        year=0;
        director="";
        genre_list= new ArrayList<String>();
    }

    public Movie(String id, String title, int year, String director) {
        this.id = id;
        this.title= title;
        this.year= year;
        this.director= director;
    }
    public int getYear() {
        return year;
    }

    public void setYear(int Year) {
        this.year = Year;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }
    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }
    public List<String> getGenre_list() {
        return genre_list;
    }
    public void add_genre(String genre) {
        genre_list.add(genre);
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Name:" + getName());
        sb.append(", Year:"+getYear());
        sb.append(", ID: "+getID());
        sb.append(", director:"+getDirector());
        sb.append(" Genres: ");
        for(int i=0;i<genre_list.size();i++){
            sb.append(genre_list.get(i)+" ");
        }
        return sb.toString();
    }
}
