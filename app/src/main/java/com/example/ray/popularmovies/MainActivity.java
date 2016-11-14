package com.example.ray.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.math.BigInteger;
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

        arrayList = new ArrayList<>();

        gridView = (GridView) findViewById(R.id.movies_Grid);

        runOnUiThread(new Runnable() {
            @Override
             public void run() {
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("popular")
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                        .appendQueryParameter("language", "en-US");
                new GetMoviesJSON().execute(uri.build().toString());
            }
        });

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
        if (id == R.id.order_by_popularity) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Uri.Builder uri = new Uri.Builder();
                    uri.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath("popular")
                            .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                            .appendQueryParameter("language", "en-US");
                    new GetMoviesJSON().execute(uri.build().toString());
                }
            });
            return true;
        }

        if (id == R.id.order_by_ratings) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Uri.Builder uri = new Uri.Builder();
                    uri.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath("top_rated")
                            .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                            .appendQueryParameter("language", "en-US");
                    new GetMoviesJSON().execute(uri.build().toString());
                }
            });
            return true;

        }
        return super.onOptionsItemSelected(item);
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

                arrayList.clear();
                for(int i =0;i<jsonArray.length(); i++){
                    JSONObject movie = jsonArray.getJSONObject(i);
                    Uri.Builder uri = new Uri.Builder();
                    uri.scheme("https")
                            .authority("image.tmdb.org")
                            .appendPath("t")
                            .appendPath("p")
                            .appendPath("w185");

                    arrayList.add(new Movie(
                            new BigInteger(movie.getString("id")),
                            movie.getString("title"),
                            new String(uri.build().toString() + movie.getString("poster_path")),
                            movie.getString("backdrop_path"),
                            movie.getString("overview"),
                            movie.getString("release_date"),
                            Double.parseDouble(movie.getString("popularity"))
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MoviesGridAdapter adapter = new MoviesGridAdapter(
                    getApplicationContext(), R.layout.movies_list_item, arrayList
            );
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
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
}
