package com.example.ray.popularmovies;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ray.popularmovies.Data.Movie;
import com.facebook.stetho.okhttp3.StethoInterceptor;

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

    private MoviesGridAdapter mMoviesAdapter;

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

        arrayList = new ArrayList<>();

        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        new GetMoviesFromDB().execute("popular");
    }

    public class GetMoviesFromDB extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return "";
        }

        @Override
        protected void onPostExecute(String content) {
            arrayList.clear();

            Cursor c = getActivity().getApplicationContext().getContentResolver().query(
                    MoviesProvider.MOVIES_BASE_URI, null, null, null, null);

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

            // TODO: CursorAdaptor Implemention
            /*Cursor c = getActivity().getApplicationContext().getContentResolver().query(
                    MoviesProvider.MOVIES_BASE_URI, null, null, null, null);
            MoviesGridAdapter adapter = new MoviesGridAdapter(
                    getActivity().getApplicationContext(), c2);*/

            v.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
