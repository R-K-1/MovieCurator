package com.example.ray.popularmovies;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static com.example.ray.popularmovies.MoviesProvider.MOVIES_TABLE_NAME;

/**
 * Created by Ray on 12/21/2016.
 */

public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    AccountManager mAccountManager;
    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 3;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    /**
     * Set up the sync adapter
     */
    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mAccountManager= AccountManager.get(context);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public MoviesSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /*
 * Specify the code you want to run in the sync adapter. The entire
 * sync adapter runs in a background thread, so you don't have to set
 * up your own background processing.
 */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        // Get the auth token for the current account
/*        String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);
        ParseComServerAccessor parseComService = new ParseComServerAccessor();*/
    /*
     * Put the data transfer code here.
     */
        String responsePopular = new String();
        String responseTopRated = new String();
        try {
            OkHttpClient  client = new OkHttpClient();
            responsePopular = ApiCall.GET(client, getContext().getResources().getString(R.string.moviedb_api_filter_by_popularity));
            responseTopRated = ApiCall.GET(client, getContext().getResources().getString(R.string.moviedb_api_filter_by_ratings));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Movie> arrayList = new ArrayList<>();

        JSONArray jsonArrayPopular;

        JSONArray jsonArrayTopRated;

        try {
            JSONObject jsonObjectPopular = new JSONObject(responsePopular);
            jsonArrayPopular =  jsonObjectPopular.getJSONArray("results");

            JSONObject jsonObjectTopRated = new JSONObject(responseTopRated);
            jsonArrayTopRated =  jsonObjectTopRated.getJSONArray("results");

            arrayList.clear();

            MoviesProvider.DatabaseHelper y = new MoviesProvider.DatabaseHelper(getContext());
            SQLiteDatabase db = y.getWritableDatabase();

            db.execSQL("DROP TABLE IF EXISTS " +  MOVIES_TABLE_NAME);
            db.execSQL(MoviesProvider.CREATE_DB_TABLE);


            ContentValues values = new ContentValues();
            // Bulk insert movies into database
            for (int i =0;i<jsonArrayPopular.length(); i++) {
                JSONObject m = jsonArrayPopular.getJSONObject(i);
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
                values.put(MoviesProvider.IS_POPULAR, 1);
                values.put(MoviesProvider.IS_TOP_RATED, 0);
                values.put(MoviesProvider.IS_FAVORITE, 0);

                getContext().getContentResolver().insert(MoviesProvider.CONTENT_URI, values);
            }

            // Bulk insert movies into database
            for (int i =0;i<jsonArrayTopRated.length(); i++) {
                JSONObject m = jsonArrayTopRated.getJSONObject(i);
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

                getContext().getContentResolver().insert(MoviesProvider.CONTENT_URI, values);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
/*        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);*/
        ContentResolver.requestSync(getSyncAccount(context),
                "com.example.ray.popularmovies", bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
/*        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));*/

        Account newAccount = new Account(
                context.getString(R.string.app_name), "com.example.ray.popularmovies.datasync");

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        // ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }


/*    private ArrayList<Movie> addMoviesToPersistenceArrayList (JSONArray moviesJSON,
                                                              ArrayList<Movie> moviesArrayList,
                                                              boolean isPopular, boolean isTopRated) {
        try {

            for(int i =0;i<moviesJSON.length(); i++) {
                JSONObject movie = moviesJSON.getJSONObject(i);
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("https")
                        .authority("image.tmdb.org")
                        .appendPath("t")
                        .appendPath("p")
                        .appendPath("w185");

                if (isPopular) {
                    moviesArrayList.add(new Movie(
                            new BigInteger(movie.getString("id")),
                            movie.getString("title"),
                            new String(uri.build().toString() + movie.getString("poster_path")),
                            movie.getString("backdrop_path"),
                            movie.getString("overview"),
                            movie.getString("release_date"),
                            Double.parseDouble(movie.getString("popularity")),
                            true,
                            false,
                            false
                    ));
                } else if (isTopRated) {
                    moviesArrayList.add(new Movie(
                            new BigInteger(movie.getString("id")),
                            movie.getString("title"),
                            new String(uri.build().toString() + movie.getString("poster_path")),
                            movie.getString("backdrop_path"),
                            movie.getString("overview"),
                            movie.getString("release_date"),
                            Double.parseDouble(movie.getString("popularity")),
                            false,
                            true,
                            false
                    ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moviesArrayList;
    }*/

}
