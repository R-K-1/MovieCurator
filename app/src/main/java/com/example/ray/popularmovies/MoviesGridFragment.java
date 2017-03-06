package com.example.ray.popularmovies;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ray.popularmovies.Data.Movie;

import java.math.BigInteger;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by Ray on 12/29/2016.
 */

public class MoviesGridFragment extends Fragment {

    ArrayList<Movie> arrayList;
    GridView v;
    private OkHttpClient client;
    private String dbMoviesFilter = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = (GridView) inflater.inflate(R.layout.movies_gridview, container, false);
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String movieId = arrayList.get(position).getmId().toString();
                MovieDetailsFragment movieDetailsFragment =
                        (MovieDetailsFragment) getFragmentManager().findFragmentById(R.id.movie_detail_in_fragment);

                if (movieDetailsFragment != null) {
                    MovieDetailsFragment newMovieDetailsFragment = new MovieDetailsFragment();
                    newMovieDetailsFragment.setMovieIdGlobal(movieId);
                    FragmentTransaction ft = movieDetailsFragment.getActivity().getFragmentManager().beginTransaction();
                    ft.replace(R.id.layout1, newMovieDetailsFragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Intent i = new Intent(getActivity().getApplicationContext(), MovieActivity.class);
                    i.putExtra("movieId", movieId);
                    startActivity(i);
                }
            }
        });

        return v;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        arrayList = new ArrayList<>();

        client = new OkHttpClient();

        dbMoviesFilter = dbMoviesFilter == "" ? getString(R.string.db_filter_popular):dbMoviesFilter;
        new GetMoviesFromDB().execute(dbMoviesFilter);
    }

    public void setDbMoviesFilter (String filter) {
        dbMoviesFilter = filter;
    }

    public void updateGrid (String filter) {
        new GetMoviesFromDB().execute(filter);
    }

    public class GetMoviesFromDB extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(String content) {
            arrayList.clear();

            Uri x;
            String favs = getString(R.string.db_filter_favorite);
            String pops = getString(R.string.db_filter_popular);
            String tops = getString(R.string.db_filter_top_rated);

            if (content == getString(R.string.db_filter_favorite)) {
                x = MoviesProvider.FAVORITE_MOVIES_URI;
            } else if (content == getString(R.string.db_filter_top_rated)) {
                x = MoviesProvider.TOP_RATED_MOVIES_URI;
            } else {
                x = MoviesProvider.POPULAR_MOVIES_URI;
            }

            Cursor c = getActivity().getApplicationContext().getContentResolver().query(
                    x, null, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    arrayList.add(new Movie(
                            new BigInteger(c.getString(c.getColumnIndex(MoviesProvider.MOVIE_DB_ID))),
                            c.getString(c.getColumnIndex(MoviesProvider.TITLE)),
                            new String(c.getString(c.getColumnIndex(MoviesProvider.POSTER_PATH))),
                            c.getString(c.getColumnIndex(MoviesProvider.BACKDROP_PATH)),
                            c.getString(c.getColumnIndex(MoviesProvider.OVERVIEW)),
                            c.getString(c.getColumnIndex(MoviesProvider.RELEASE_DATE)),
                            Double.parseDouble(c.getString(c.getColumnIndex(MoviesProvider.POPULARITY))),
                            (c.getInt(c.getColumnIndex(MoviesProvider.IS_POPULAR))),
                            (c.getInt(c.getColumnIndex(MoviesProvider.IS_TOP_RATED))),
                            (c.getInt(c.getColumnIndex(MoviesProvider.IS_FAVORITE)))
                    ));
                } while (c.moveToNext());
            }

            MoviesGridAdapter adapter = new MoviesGridAdapter(
                    getActivity().getApplicationContext(), R.layout.movies_list_item, arrayList
            );

            v.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
