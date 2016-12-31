package com.example.ray.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
    private communicate cm;

    private MoviesGridAdapter mMoviesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // return inflater.inflate(R.layout.movies_gridview, container, false);
        // View v = inflater.inflate(R.layout.movies_gridview, container, false);
        // GridView v = (GridView) inflater.inflate(R.layout.movies_gridview, container, false);
        v = (GridView) inflater.inflate(R.layout.movies_gridview, container, false);
        // GridView moviesGrid = (GridView)v.findViewById(R.id.movies_Gridview);
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Send intent to SingleViewActivity
                Intent i = new Intent(getActivity().getApplicationContext(), MovieActivity.class);
                // Pass image index
                i.putExtra("SelectedMovie",(Parcelable) arrayList.get(position));
                // cm.sendData();
                // startActivity(i);
                MovieDetailsFragment movieDetailsFragment =
                        (MovieDetailsFragment) getFragmentManager().findFragmentById(R.id.movie_detail_in_fragment);

                if (movieDetailsFragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("movieId", arrayList.get(position).getmId().toString());
                    movieDetailsFragment.setArguments(bundle);
                    movieDetailsFragment.updateMovieDetails(arrayList.get(position).getmId().toString());
                } else {
                    // i.putExtra("SelectedMovie",(Parcelable) arrayList.get(position));
                    i.putExtra("movieId", arrayList.get(position).getmId());
                    // cm.sendData();
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

        // gridView = (GridView) getView().findViewById(R.id.movies_Grid);
        // gridView = (GridView) getView().findViewById(R.id.movies_gridview_inside_movies_gridview);
        new GetMoviesFromDB().execute("popular");

        /*if (gridView == null) {
            gridView = getActivity().getLayoutInflater().inflate(R.layout.)
        }*/
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

            if (c != null) {
                while (c.moveToNext()) {
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
                }
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
