package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleMovieActivity extends ActionBarActivity {

    private TextView title_text;
    private TextView year_text;
    private TextView director_text;
    private TextView rating_text;
    private TextView genre_text;
    private TextView star_text;
    private String url;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.singlemovie);
        title_text= findViewById(R.id.smovie_title);
        year_text = findViewById(R.id.smovie_year);
        director_text = findViewById(R.id.smovie_director);
        genre_text = findViewById(R.id.smovie_genres);
        star_text = findViewById(R.id.smovie_stars);
        rating_text=findViewById(R.id.smovie_rating);
        url= url = "https://ec2-34-238-241-109.compute-1.amazonaws.com:8443/project_4/api/";
        final ArrayList<Movie> movies = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        // or other values
        if(b != null) {
            id=b.getString("id");
        }
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest singlemovieRequest = new StringRequest(Request.Method.GET, url + "single-movie"+"?id="+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                try {
                    JSONArray resultdata = new JSONArray(response);
                    JSONObject info_1= resultdata.getJSONObject(0);
                    title_text.setText("Movie title: "+ info_1.getString("movie_title"));
                    year_text.setText("Year: "+ info_1.getString("year"));
                    director_text.setText("Director: "+ info_1.getString("director"));
                    rating_text.setText("Rating:" +info_1.getString("rating"));
                    JSONArray genre_list= resultdata.getJSONObject(1).getJSONArray("genre_list");
                    String genres="";
                    for(int j=0;j<genre_list.length();j++){
                        genres+=genre_list.getString(j);
                        if(j!=genre_list.length()-1) genres+=",";
                    }
                    genre_text.setText("All genres: "+ genres);
                    JSONArray star_list= resultdata.getJSONObject(2).getJSONArray("star_list");
                    String stars="";
                    for(int j=0;j<star_list.length();j++){
                        stars+=star_list.getString(j);
                        if(j!=star_list.length()-1) stars+=",";
                    }
                    star_text.setText("All stars: "+ stars);
                }catch (JSONException err){
                    Log.d("Error", err.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("singlemovie.error", error.toString());
                    }
                });

        // !important: queue.add is where the login request is actually sent
        queue.add(singlemovieRequest);
    }

}