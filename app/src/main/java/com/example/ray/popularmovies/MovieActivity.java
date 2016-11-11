package com.example.ray.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Ray on 11/10/2016.
 */

public class MovieActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        // Get intent data
        Intent i = getIntent();

        // Selected image id
        int position = i.getExtras().getInt("id");

        Movie movie = (Movie)getIntent().getSerializableExtra("SelectedMovie");

        // ImageAdapter imageAdapter = new ImageAdapter(this);
        ImageView imageView = (ImageView) findViewById(R.id.SingleView);
        Picasso.with(getApplicationContext()).load(movie.getPosterPath()).into(imageView);

        TextView titleView = (TextView) findViewById(R.id.movie_detail_title);
        titleView.setText(movie.getTitle());

        TextView releaseDateView = (TextView) findViewById(R.id.movie_detail_release_date);
        releaseDateView.setText(movie.getReleaseDate());

        TextView voteView = (TextView) findViewById(R.id.movie_detail_vote_average);
        voteView.setText(movie.getPopularity().toString());

        TextView synopsisView = (TextView) findViewById(R.id.movie_detail_plot_synopsis);
        synopsisView.setText(movie.getOverview());
    }
}
