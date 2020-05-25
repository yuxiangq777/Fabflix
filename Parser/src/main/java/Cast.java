

public class Cast {

    private String star_name;
    private String movie_id;
    public Cast(){
        star_name="";
        movie_id="";
    }

    public Cast(String name, String movie_id) {
        this.star_name = name;
        this.movie_id= movie_id;
    }
    public String getStar() {
        return star_name;
    }

    public void setStar(String name) {
        this.star_name = name;
    }
    public String getMovie() {
        return movie_id;
    }

    public void setMovie(String movie_id) {
        this.movie_id = movie_id;
    }




    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Cast Details - ");
        sb.append("star_name:" + getStar());
        sb.append(", Movie:"+getMovie());
        return sb.toString();
    }
}