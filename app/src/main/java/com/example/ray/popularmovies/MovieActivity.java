package com.example.ray.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ray.popularmovies.Data.Movie;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by Ray on 11/10/2016.
 */

public class MovieActivity extends Activity {

    private OkHttpClient mClient;
    private ArrayList<String> mTrailersURLs;
    private ListView mTrailers;
    private ArrayList<String> mReviewsList;
    private ListView mReviews;
    Activity a;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = this;
        setContentView(R.layout.movie_detail);

        Utils utils = new Utils();

        // Get intent data
        Intent i = getIntent();

        Bundle extras = getIntent().getExtras();
        final Movie movie = utils.getMovieFromDB(extras.get("movieId").toString(), this);


        ImageView imageView = (ImageView) findViewById(R.id.movie_detail_poster);
        File imageFile = utils.getImageFromInternalStorage(getApplicationContext(), "", movie.getmPosterPath());
        Picasso.with(getApplicationContext()).load(imageFile).into(imageView);

        final ToggleButton isFavoriteButton = (ToggleButton) findViewById(R.id.isFavorite);

        int currentState = movie.ismInFavorites();
        int x = movie.ismInFavorites() == 1? R.drawable.star_on: R.drawable.star_off;
        isFavoriteButton.setBackgroundResource(x);
        isFavoriteButton.setVisibility(View.VISIBLE);
        isFavoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MoviesProvider.DatabaseHelper y = new MoviesProvider.DatabaseHelper(getApplicationContext());
                SQLiteDatabase db = y.getWritableDatabase();
                ContentValues values = new ContentValues();
                String uri = MoviesProvider.MOVIE_URI + movie.getmId();
                if (isChecked) {
                    isFavoriteButton.setBackgroundResource(R.drawable.star_on);
                    values.put(MoviesProvider.IS_FAVORITE, 1);
                    getContentResolver().update(Uri.parse(uri), values, null, null);
                } else {
                    isFavoriteButton.setBackgroundResource(R.drawable.star_off);
                    values.put(MoviesProvider.IS_FAVORITE, 0);
                    getContentResolver().update(Uri.parse(uri), values, null, null);
                }
            }
        });

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

        mReviewsList = new ArrayList<String>();
        new GetReviewsFromDB().execute(movie.getmId().toString());
        mReviews = (ListView) findViewById(R.id.movie_detail_reviews_list);
    }

    class GetTrailersJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            mTrailersURLs.clear();
            Uri trailers = Uri.parse(MoviesProvider.GET_TRAILERS_URI + params[0]);
            Cursor c = getApplicationContext().getContentResolver().query(
                    trailers, null, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    mTrailersURLs.add(c.getString(c.getColumnIndex(MoviesProvider.KEY)));
                } while (c.moveToNext());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String content) {
            TrailersListAdapter adapter = new TrailersListAdapter(
                    getApplicationContext(), R.id.movie_detail_trailers_list, mTrailersURLs
            );
            mTrailers.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    class GetReviewsFromDB extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(String content) {
            mReviewsList.clear();
            Uri reviews = Uri.parse(MoviesProvider.GET_REVIEWS_URI + content);
            Cursor c = getApplicationContext().getContentResolver().query(
                    reviews, null, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    mReviewsList.add(c.getString(c.getColumnIndex(MoviesProvider.CONTENT)) + "\n \n"
                        + c.getString(c.getColumnIndex(MoviesProvider.AUTHOR)));
                } while (c.moveToNext());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(a,
                    R.layout.reviews_list_item, mReviewsList);
            mReviews.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
