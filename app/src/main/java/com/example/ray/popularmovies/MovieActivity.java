package com.example.ray.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by Ray on 11/10/2016.
 */

public class MovieActivity extends Activity {

    private OkHttpClient mClient;
    private ArrayList<String> mTrailersURLs;
    private ListView mTrailers;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        // Get intent data
        Intent i = getIntent();

        Movie movie = (Movie) i.getParcelableExtra("SelectedMovie");

        ImageView imageView = (ImageView) findViewById(R.id.movie_detail_poster);
        Picasso.with(getApplicationContext()).load(movie.getmPosterPath()).into(imageView);

        TextView titleView = (TextView) findViewById(R.id.movie_detail_title);
        titleView.setText(movie.getTitle());

        TextView releaseDateView = (TextView) findViewById(R.id.movie_detail_release_date);
        releaseDateView.setText(movie.getmReleaseDate());

        TextView voteView = (TextView) findViewById(R.id.movie_detail_vote_average);
        voteView.setText(Double.toString(movie.getmPopularity()));

        TextView synopsisView = (TextView) findViewById(R.id.movie_detail_plot_synopsis);
        synopsisView.setText(movie.getmOverview());

        mTrailersURLs = new ArrayList<>();
        mClient = new OkHttpClient();
        new GetTrailersJSON().execute(movie.getmId().toString());

        mTrailers = (ListView) findViewById(R.id.movie_detail_trailers_list);

        mTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String movieId = mTrailersURLs.get(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + movieId)));
            }
        });

    }

    class GetTrailersJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String response = new String();
            try {
                response = ApiCall.GET(mClient, RequestBuilder.buildGetTrailersURL(params[0]).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String content) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray jsonArray =  jsonObject.getJSONArray("results");

                mTrailersURLs.clear();
                for(int i =0;i<jsonArray.length(); i++){
                    JSONObject trailer = jsonArray.getJSONObject(i);

                    mTrailersURLs.add(trailer.getString("key"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TrailersListAdapter adapter = new TrailersListAdapter(
                    getApplicationContext(), R.id.movie_detail_trailers_list, mTrailersURLs
            );
            mTrailers.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
