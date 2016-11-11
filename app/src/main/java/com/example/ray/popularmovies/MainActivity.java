package com.example.ray.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Movie> arrayList;
    GridView gridView   ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        arrayList = new ArrayList<>();

        gridView = (GridView) findViewById(R.id.movies_Grid);

        // runOnUiThread(new Runnable() {
            // @Override
            // public void run() {
                // new ReadJSON().execute("http://quocnguyen.16mb.com/products.json");
                new GetMoviesJSON().execute("https://api.themoviedb.org/3/discover/movie?api_key=" + BuildConfig.MOVIE_DB_API_KEY + "&sort_by=popularity.desc");
            // }
        // });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                    // Send intent to SingleViewActivity
                    Intent i = new Intent(getApplicationContext(), MovieActivity.class);
                    // Pass image index
                    i.putExtra("SelectedMovie",(Serializable) arrayList.get(position));
                    startActivity(i);
            }
        });
    }

    class GetMoviesJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray jsonArray =  jsonObject.getJSONArray("results");

                for(int i =0;i<jsonArray.length(); i++){
                    JSONObject movieObject = jsonArray.getJSONObject(i);
                    arrayList.add(new Movie(
                            new BigDecimal(movieObject.getString("id")),
                            movieObject.getString("title"),
                            new String("https://image.tmdb.org/t/p/w185" + movieObject.getString("poster_path")),
                            movieObject.getString("backdrop_path"),
                            movieObject.getString("overview"),
                            movieObject.getString("release_date"),
                            new BigDecimal(movieObject.getString("popularity"))
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MoviesGridAdapter adapter = new MoviesGridAdapter(
                    getApplicationContext(), R.layout.movies_list_item, arrayList
            );
            gridView.setAdapter(adapter);
        }
    }


    private static String readURL(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            // create a url object
            URL url = new URL(theUrl);
            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();
            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
