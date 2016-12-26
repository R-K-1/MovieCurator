package com.example.ray.popularmovies;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;

import static com.example.ray.popularmovies.MoviesProvider.MOVIES_TABLE_NAME;

/**
 * Created by Ray on 12/23/2016.
 */

public class MovieDBAlarmService extends IntentService {
    private Context context;
    public MovieDBAlarmService() {
        super("MovieDBAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i("MovieDBAlarmService", "Service running");

        context = getApplicationContext();

        OkHttpClient x = new OkHttpClient();
        String responsePopular = new String();
        String responseTopRated = new String();
        try {
            OkHttpClient  client = new OkHttpClient();
            responsePopular = ApiCall.GET(x,
                    RequestBuilder.buildGetMoviesURI(getResources().getString(R.string.moviedb_api_filter_by_popularity)).toString());
            responseTopRated += ApiCall.GET(x,
                    RequestBuilder.buildGetMoviesURI(getResources().getString(R.string.moviedb_api_filter_by_ratings)).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject popularMoviesObject = new JSONObject(responsePopular);
            JSONArray popularMoviesArray = popularMoviesObject.getJSONArray("results");
            JSONObject topRatedMoviesObject = new JSONObject(responseTopRated);
            JSONArray topRatedMoviesArray = topRatedMoviesObject.getJSONArray("results");

            MoviesProvider.DatabaseHelper y = new MoviesProvider.DatabaseHelper(context);
            SQLiteDatabase db = y.getWritableDatabase();

            db.execSQL("DROP TABLE IF EXISTS " +  MOVIES_TABLE_NAME);
            db.execSQL(MoviesProvider.CREATE_DB_TABLE);


            ContentValues values = new ContentValues();
            // Bulk insert movies into database
            for (int i = 0; i < popularMoviesArray.length(); i++) {
                JSONObject m = popularMoviesArray.getJSONObject(i);
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("https")
                        .authority("image.tmdb.org")
                        .appendPath("t")
                        .appendPath("p")
                        .appendPath("w185");


                String imgURL = uri.build().toString() + m.getString("poster_path");

                PackageManager pm = getPackageManager();
                String s = getPackageName();
                String imgDir = "";
                try {
                    PackageInfo p = pm.getPackageInfo(s, 0);
                    imgDir = p.applicationInfo.dataDir;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w("popularMovies", "Error Package name not found", e);
                }

                // String imgDir = Environment.getex
                Utils z = new Utils();
                /*Picasso.with(getApplicationContext()).load(imgURL).into(new
                        Utils().picassoImageTarget(getApplicationContext(), imgDir, m.getString("poster_path")));*/

                Bitmap b;

                try {
                    b = z.getBitmapFromCloud(context, imgURL);
                    z.saveBitmapToInternalStorage(context, b, m.getString("poster_path") );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                values.clear();
                values.put(MoviesProvider.MOVIE_DB_ID, m.getString("id"));
                values.put(MoviesProvider.TITLE, m.getString("title"));
                values.put(MoviesProvider.ORIGINAL_TITLE, m.getString("original_title"));
                // values.put(MoviesProvider.POSTER_PATH, uri.build().toString() + m.getString("poster_path"));
                values.put(MoviesProvider.POSTER_PATH, m.getString("poster_path"));
                values.put(MoviesProvider.BACKDROP_PATH, m.getString("backdrop_path"));
                values.put(MoviesProvider.OVERVIEW, m.getString("overview"));
                values.put(MoviesProvider.RELEASE_DATE, m.getString("release_date"));
                values.put(MoviesProvider.POPULARITY, m.getString("popularity"));
                values.put(MoviesProvider.IS_POPULAR, 1);
                values.put(MoviesProvider.IS_TOP_RATED, 0);
                values.put(MoviesProvider.IS_FAVORITE, 0);

                getContentResolver().insert(MoviesProvider.CONTENT_URI, values);
            }

            // Bulk insert movies into database
            /*for (int i = 0; i < topRatedMoviesArray.length(); i++) {
                JSONObject m = topRatedMoviesArray.getJSONObject(i);
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("https")
                        .authority("image.tmdb.org")
                        .appendPath("t")
                        .appendPath("p")
                        .appendPath("w185");

                values.clear();
                values.put(MoviesProvider.MOVIE_DB_ID, m.getString("id"));
                values.put(MoviesProvider.TITLE, m.getString("title"));
                values.put(MoviesProvider.ORIGINAL_TITLE, m.getString("original_title"));
                values.put(MoviesProvider.POSTER_PATH, uri.build().toString() + m.getString("poster_path"));
                values.put(MoviesProvider.BACKDROP_PATH, m.getString("backdrop_path"));
                values.put(MoviesProvider.OVERVIEW, m.getString("overview"));
                values.put(MoviesProvider.RELEASE_DATE, m.getString("release_date"));
                values.put(MoviesProvider.POPULARITY, m.getString("popularity"));
                values.put(MoviesProvider.IS_POPULAR, 0);
                values.put(MoviesProvider.IS_TOP_RATED, 1);
                values.put(MoviesProvider.IS_FAVORITE, 0);

                getContentResolver().insert(MoviesProvider.CONTENT_URI, values);
            }*/


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public Bitmap getBitmapFromCloud(String url) throws IOException {
        Bitmap mBitmap;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();

            }
        });

        return mBitmap = builder.build().with(context).load(url).get();
    }

}
