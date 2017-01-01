package com.example.ray.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
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
 * A placeholder fragment containing a simple view.
 */
// public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
public class MainActivityFragment extends Fragment {


    ArrayList<Movie> arrayList;
    GridView gridView;
    private OkHttpClient client;

    private MoviesGridAdapter mMoviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        arrayList = new ArrayList<>();

        client = new OkHttpClient();

        arrayList = new ArrayList<>();

        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        gridView = (GridView) getView().findViewById(R.id.movies_Grid);

        new GetMoviesFromDB().execute("popular");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Send intent to SingleViewActivity
                Intent i = new Intent(getActivity().getApplicationContext(), MovieActivity.class);
                // Pass image index
                i.putExtra("SelectedMovie",(Parcelable) arrayList.get(position));
                startActivity(i);
            }
        });
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
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}