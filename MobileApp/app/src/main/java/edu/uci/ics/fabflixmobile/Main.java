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

import org.json.JSONObject;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

public class Main extends ActionBarActivity {

    private EditText mtitle;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.search);
        mtitle = findViewById(R.id.mtitle);
        searchButton = findViewById(R.id.search);
        /**
         * In Android, localhost is the address of the device or the emulator.
         * To connect to your machine, you need to use the below IP address
         * **/

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    public void search() {
        Intent listPage = new Intent(Main.this, ListViewActivity.class);
        //without starting the activity/page, nothing would happen
        Bundle b = new Bundle();
        b.putString("title",mtitle.getText().toString());
        b.putInt("page",1);
        listPage.putExtras(b);
        startActivity(listPage);
        finish();
    }
}