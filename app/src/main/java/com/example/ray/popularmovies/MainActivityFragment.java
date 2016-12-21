package com.example.ray.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
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

        /*client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();*/

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
            String response = new String();
            try {
                response = ApiCall.GET(client, RequestBuilder.buildGetMoviesURI(params[0]).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String content) {
            // try {

            arrayList.clear();

            Uri movies = MoviesProvider.CONTENT_URI;
            Cursor c = getActivity().getApplicationContext().getContentResolver().query(
                    MoviesProvider.CONTENT_URI, null, null, null, null);

            while (c.moveToNext()) {
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("https")
                        .authority("image.tmdb.org")
                        .appendPath("t")
                        .appendPath("p")
                        .appendPath("w185");

                arrayList.add(new Movie(
                        new BigInteger(c.getString(c.getColumnIndex(MoviesProvider.MOVIE_DB_ID))),
                        c.getString(c.getColumnIndex(MoviesProvider.TITLE)),
                        new String(c.getString(c.getColumnIndex(MoviesProvider.POSTER_PATH))),
                        c.getString(c.getColumnIndex(MoviesProvider.BACKDROP_PATH)),
                        c.getString(c.getColumnIndex(MoviesProvider.OVERVIEW)),
                        c.getString(c.getColumnIndex(MoviesProvider.RELEASE_DATE)),
                        Double.parseDouble(c.getString(c.getColumnIndex(MoviesProvider.POPULARITY)))
                ));

            }

            MoviesGridAdapter adapter = new MoviesGridAdapter(
                   getActivity().getApplicationContext(), R.layout.movies_list_item, arrayList
            );
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            /* JSONObject jsonObject = new JSONObject(content);
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
            }*/

            /*MoviesProvider.DatabaseHelper y = new MoviesProvider.DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = y.getWritableDatabase();

            db.execSQL("DROP TABLE IF EXISTS " +  MOVIES_TABLE_NAME);
            db.execSQL(MoviesProvider.CREATE_DB_TABLE);

            // db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                // Bulk insert movies into database
                for (Movie m : arrayList) {
                    // add new value
                    values.clear();
                    values.put(MoviesProvider.MOVIE_DB_ID, m.getmId().toString());
                    values.put(MoviesProvider.TITLE, m.getTitle());
                    values.put(MoviesProvider.ORIGINAL_TITLE, m.getmOriginalTitle());
                    values.put(MoviesProvider.POSTER_PATH, m.getmPosterPath());
                    values.put(MoviesProvider.BACKDROP_PATH, m.getmBackdropPath());
                    values.put(MoviesProvider.OVERVIEW, m.getmOverview());
                    values.put(MoviesProvider.RELEASE_DATE, m.getmReleaseDate());
                    values.put(MoviesProvider.POPULARITY, m.getmPopularity());

                    Uri uri = getContentResolver().insert(MoviesProvider.CONTENT_URI, values);

                    // uri.toString();

                }
            } finally {
                // db.endTransaction();
            }*/

        }
    }
}