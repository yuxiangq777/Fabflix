package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListViewActivity extends Activity {
    private String url;
    private Button prevButton;
    private Button nextButton;
    private String title;
    private int page;
    private TextView page_info;
    private MovieListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        prevButton=findViewById(R.id.prev);
        nextButton=findViewById(R.id.next);
        page_info=findViewById(R.id.page);
        url = "https://ec2-34-238-241-109.compute-1.amazonaws.com:8443/project_4/api/";
        //this should be retrieved from the database and the backend server
        final ArrayList<Movie> movies = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        // or other values
        if(b != null) {
            title = b.getString("title");
            page = b.getInt("page");
        }
        page_info.setText("Page "+Integer.toString(page));
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest ViewRequest = new StringRequest(Request.Method.GET, url + "movie-list"+"?by=main&title="+title+"&year=&director=&starname=&genre=&order=&page="+page+"&ipp=20", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                try {
                    JSONArray resultdata = new JSONArray(response);
                    for(int i=0;i<resultdata.length();i++){
                        JSONObject jobject= resultdata.getJSONObject(i);
                        String movie_title=jobject.getString("movie_title");
                        String movie_id=jobject.getString("movie_id");
                        String year= jobject.getString("year");
                        String director= jobject.getString("director");
                        JSONArray genre_list= jobject.getJSONArray("genre_list");
                        String genres="";
                        for(int j=0;j<genre_list.length();j++){
                            genres+=genre_list.getString(j);
                            if(j!=genre_list.length()-1) genres+=",";
                        }
                        JSONArray star_list= jobject.getJSONArray("star_list");
                        String stars="";
                        for(int j=0;j<star_list.length();j++){
                            stars+=star_list.getString(j);
                            if(j!=star_list.length()-1) stars+=",";
                        }
                        String rating= jobject.getString("rating");
                        movies.add(new Movie(movie_id,movie_title,year,director,genres,stars,rating));
                    }
                    Log.d("listview.success", response);
                    adapter.notifyDataSetChanged();
                    //initialize the activity(page)/destination

                }catch (JSONException err){
                    Log.d("Error", err.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("listview.error", error.toString());
                    }
                });

        // !important: queue.add is where the login request is actually sent
        queue.add(ViewRequest);
        adapter = new MovieListViewAdapter(movies, this);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                Intent listPage = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                //without starting the activity/page, nothing would happen
                Bundle b = new Bundle();
                b.putString("id",movie.getId());
                listPage.putExtras(b);
                startActivity(listPage);
                finish();
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page>1){
                    Intent listPage = new Intent(ListViewActivity.this, ListViewActivity.class);
                    //without starting the activity/page, nothing would happen
                    Bundle b = new Bundle();
                    b.putString("title",title);
                    b.putInt("page",page-1);
                    listPage.putExtras(b);
                    startActivity(listPage);
                    finish();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listPage = new Intent(ListViewActivity.this, ListViewActivity.class);
                //without starting the activity/page, nothing would happen
                Bundle b = new Bundle();
                b.putString("title",title);
                b.putInt("page",page+1);
                listPage.putExtras(b);
                startActivity(listPage);
                finish();

            }
        });
    }
}