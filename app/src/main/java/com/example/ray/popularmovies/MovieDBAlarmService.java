package com.example.ray.popularmovies;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.example.ray.popularmovies.Tools.ApiCall;
import com.example.ray.popularmovies.Tools.RequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

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

            PackageManager pm = getPackageManager();
            String s = getPackageName();
            String imgDir = "";
            try {
                PackageInfo p = pm.getPackageInfo(s, 0);
                imgDir = p.applicationInfo.dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("popularMovies", "Error Package name not found", e);
            }
            Utils z = new Utils();

            Uri.Builder uri = new Uri.Builder();
            uri.scheme("https")
                    .authority("image.tmdb.org")
                    .appendPath("t")
                    .appendPath("p")
                    .appendPath("w185");

            ContentValues values = new ContentValues();

            MoviesProvider.DatabaseHelper y = new MoviesProvider.DatabaseHelper(context);
            SQLiteDatabase db = y.getWritableDatabase();

            db.execSQL(MoviesProvider.CREATE_MOVIES_DB_TABLE);
            db.execSQL(MoviesProvider.CREATE_TRAILERS_DB_TABLE);
            db.execSQL(MoviesProvider.CREATE_REVIEWS_DB_TABLE);

            db.execSQL(MoviesProvider.DELETE_NONFAVORITE_REVIEWS);

            Cursor cdt = db.rawQuery(MoviesProvider.SELECT_FILENAME_NONFAVORITE_TRAILERS, null);
            if (cdt != null && cdt.moveToFirst()) {
                do {
                    String fileName = cdt.getString(cdt.getColumnIndex(MoviesProvider.KEY)) + ".jpg";
                    z.deleteImageFromInternalStorage(context, imgDir, fileName);
                } while (cdt.moveToNext());
            }
            db.execSQL(MoviesProvider.DELETE_NONFAVORITE_TRAILERS);

            Cursor cdm = db.rawQuery(MoviesProvider.SELECT_FILENAME_NONFAVORITE_POSTERS, null);
            if (cdm != null && cdm.moveToFirst()) {
                 do {
                    String fileName = cdm.getString(cdm.getColumnIndex(MoviesProvider.POSTER_PATH));
                    z.deleteImageFromInternalStorage(context, imgDir, fileName);
                } while (cdm.moveToNext());
            }
            db.execSQL(MoviesProvider.DELETE_NONFAVORITE_MOVIES);

            ArrayList<String> favoriteMoviesId = new ArrayList<String>();
            Cursor cmIds = db.rawQuery(MoviesProvider.SELECT_FAVORITE_MOVIES_ID, null);
            if (cmIds != null && cmIds.moveToFirst()) {
                 do {
                     favoriteMoviesId.add(cmIds.getString(cmIds.getColumnIndex(MoviesProvider.MOVIE_DB_ID)));
                } while (cmIds.moveToNext());
            }

            ArrayList<String> moviesInDBIds = new ArrayList<String>();

            for (int i = 0; i < popularMoviesArray.length(); i++) {
                JSONObject m = popularMoviesArray.getJSONObject(i);
                String mId = m.getString("id");

                if (!favoriteMoviesId.contains(mId)) {


                    String responseTrailers = new String();
                    String responseReviews = new String();
                    try {
                        responseTrailers = ApiCall.GET(x,
                                RequestBuilder.buildGetTrailersURI(mId).toString());
                        responseReviews += ApiCall.GET(x,
                                RequestBuilder.buildGetReviewsURI(mId).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    JSONObject movieTrailers = new JSONObject(responseTrailers);
                    JSONArray movieTrailersArray = movieTrailers.getJSONArray("results");
                    JSONObject movieReviews = new JSONObject(responseReviews);
                    JSONArray movieReviewsArray = movieReviews.getJSONArray("results");

                    String imgURL = uri.build().toString() + m.getString("poster_path");
                    Bitmap b;
                    try {
                        b = z.getBitmapFromCloud(context, imgURL);
                        z.saveBitmapToInternalStorage(context, b, m.getString("poster_path"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    values.clear();
                    values.put(MoviesProvider.MOVIE_DB_ID, mId);
                    values.put(MoviesProvider.TITLE, m.getString("title"));
                    values.put(MoviesProvider.ORIGINAL_TITLE, m.getString("original_title"));
                    values.put(MoviesProvider.POSTER_PATH, m.getString("poster_path"));
                    values.put(MoviesProvider.BACKDROP_PATH, m.getString("backdrop_path"));
                    values.put(MoviesProvider.OVERVIEW, m.getString("overview"));
                    values.put(MoviesProvider.RELEASE_DATE, m.getString("release_date"));
                    values.put(MoviesProvider.POPULARITY, m.getString("popularity"));
                    values.put(MoviesProvider.IS_POPULAR, 1);
                    values.put(MoviesProvider.IS_TOP_RATED, 0);
                    values.put(MoviesProvider.IS_FAVORITE, 0);

                    getContentResolver().insert(MoviesProvider.MOVIES_BASE_URI, values);
                    ContentValues values2 = new ContentValues();

                    for (int j = 0; j < movieTrailersArray.length(); j++) {
                        try {
                            JSONObject t = movieTrailersArray.getJSONObject(j);

                            String key = t.get("key").toString();

                            Uri.Builder posterThumbnail = new Uri.Builder();
                            posterThumbnail.scheme("https")
                                    .authority("img.youtube.com")
                                    .appendPath("vi")
                                    .appendPath(key)
                                    .appendPath("0.jpg");


                            String trailerImgURL = posterThumbnail.build().toString() + m.getString("poster_path");
                            Bitmap bm;
                            try {
                                bm = z.getBitmapFromCloud(context, posterThumbnail.build().toString());
                                z.saveBitmapToInternalStorage(context, bm, (key + ".jpg"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            values2.clear();
                            values2.put(MoviesProvider.FK_MOVIE_MOVIE_DB_ID, mId);
                            values2.put(MoviesProvider.TRAILER_MOVIE_DB_ID, t.get("id").toString());
                            values2.put(MoviesProvider.KEY, key);
                            values2.put(MoviesProvider.NAME, t.get("name").toString());
                            values2.put(MoviesProvider.SITE, t.get("site").toString());

                            getContentResolver().insert(MoviesProvider.INSERT_TRAILERS_URI, values2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    for (int k = 0; k < movieReviewsArray.length(); k++) {
                        values2.clear();
                        try {
                            JSONObject r = movieReviewsArray.getJSONObject(k);

                            values2.clear();
                            values2.put(MoviesProvider.FK_MOVIE_MOVIE_DB_ID, mId);
                            values2.put(MoviesProvider.REVIEW_MOVIE_DB_ID, r.get("id").toString());
                            values2.put(MoviesProvider.AUTHOR, r.get("author").toString());
                            values2.put(MoviesProvider.CONTENT, r.get("content").toString());

                            getContentResolver().insert(MoviesProvider.INSERT_REVIEWS_URI, values2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    moviesInDBIds.add(mId);
                }

            }

            for (int i = 0; i < topRatedMoviesArray.length(); i++) {
                JSONObject m = topRatedMoviesArray.getJSONObject(i);
                String mId = m.getString("id");

                if (!favoriteMoviesId.contains(mId)) {

                    if (!moviesInDBIds.contains(mId)) {


                        String responseTrailers = new String();
                        String responseReviews = new String();
                        try {
                            responseTrailers = ApiCall.GET(x,
                                    RequestBuilder.buildGetTrailersURI(mId).toString());
                            responseReviews += ApiCall.GET(x,
                                    RequestBuilder.buildGetReviewsURI(mId).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        JSONObject movieTrailers = new JSONObject(responseTrailers);
                        JSONArray movieTrailersArray = movieTrailers.getJSONArray("results");
                        JSONObject movieReviews = new JSONObject(responseReviews);
                        JSONArray movieReviewsArray = movieReviews.getJSONArray("results");

                        String imgURL = uri.build().toString() + m.getString("poster_path");
                        Bitmap b;
                        try {
                            b = z.getBitmapFromCloud(context, imgURL);
                            z.saveBitmapToInternalStorage(context, b, m.getString("poster_path"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        values.clear();
                        values.put(MoviesProvider.MOVIE_DB_ID, mId);
                        values.put(MoviesProvider.TITLE, m.getString("title"));
                        values.put(MoviesProvider.ORIGINAL_TITLE, m.getString("original_title"));
                        values.put(MoviesProvider.POSTER_PATH, m.getString("poster_path"));
                        values.put(MoviesProvider.BACKDROP_PATH, m.getString("backdrop_path"));
                        values.put(MoviesProvider.OVERVIEW, m.getString("overview"));
                        values.put(MoviesProvider.RELEASE_DATE, m.getString("release_date"));
                        values.put(MoviesProvider.POPULARITY, m.getString("popularity"));
                        values.put(MoviesProvider.IS_POPULAR, 0);
                        values.put(MoviesProvider.IS_TOP_RATED, 1);
                        values.put(MoviesProvider.IS_FAVORITE, 0);

                        getContentResolver().insert(MoviesProvider.MOVIES_BASE_URI, values);
                        ContentValues values2 = new ContentValues();

                        for (int j = 0; j < movieTrailersArray.length(); j++) {
                            try {
                                JSONObject t = movieTrailersArray.getJSONObject(j);

                                String key = t.get("key").toString();

                                Uri.Builder posterThumbnail = new Uri.Builder();
                                posterThumbnail.scheme("https")
                                        .authority("img.youtube.com")
                                        .appendPath("vi")
                                        .appendPath(key)
                                        .appendPath("0.jpg");


                                String trailerImgURL = posterThumbnail.build().toString() + m.getString("poster_path");
                                Bitmap bm;
                                try {
                                    bm = z.getBitmapFromCloud(context, posterThumbnail.build().toString());
                                    z.saveBitmapToInternalStorage(context, bm, (key + ".jpg"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                values2.clear();
                                values2.put(MoviesProvider.FK_MOVIE_MOVIE_DB_ID, mId);
                                values2.put(MoviesProvider.TRAILER_MOVIE_DB_ID, t.get("id").toString());
                                values2.put(MoviesProvider.KEY, key);
                                values2.put(MoviesProvider.NAME, t.get("name").toString());
                                values2.put(MoviesProvider.SITE, t.get("site").toString());

                                getContentResolver().insert(MoviesProvider.INSERT_TRAILERS_URI, values2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        for (int k = 0; k < movieReviewsArray.length(); k++) {
                            values2.clear();
                            try {
                                JSONObject r = movieReviewsArray.getJSONObject(k);

                                values2.clear();
                                values2.put(MoviesProvider.FK_MOVIE_MOVIE_DB_ID, mId);
                                values2.put(MoviesProvider.REVIEW_MOVIE_DB_ID, r.get("id").toString());
                                values2.put(MoviesProvider.AUTHOR, r.get("author").toString());
                                values2.put(MoviesProvider.CONTENT, r.get("content").toString());

                                getContentResolver().insert(MoviesProvider.INSERT_REVIEWS_URI, values2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        values.clear();
                        String updateMovieURI = MoviesProvider.MOVIE_URI + mId;
                        values.put(MoviesProvider.IS_TOP_RATED, 1);
                        getContentResolver().update(Uri.parse(updateMovieURI), values, null, null);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
