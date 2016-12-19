package com.example.ray.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by Ray on 12/14/2016.
 */

public class MoviesProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.ray.popularmovies";
    static final String URL = "content://" + PROVIDER_NAME + "/movies";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String MOVIE_DB_ID = "movieDBId";
    static final String TITLE = "title";
    static final String ORIGINAL_TITLE = "originalTitle";
    static final String POSTER_PATH = "posterPath";
    static final String BACKDROP_PATH = "backdropPath";
    static final String OVERVIEW = "overview";
    static final String RELEASE_DATE = "releaseDate";
    static final String POPULARITY = "popularity";

    private static HashMap<String, String> MOVIES_PROJECTION_MAP;

    static final int MOVIES = 1;
    static final int MOVIE_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "movies", MOVIES);
        uriMatcher.addURI(PROVIDER_NAME, "movies/#", MOVIE_ID);
    }

    /**
     * Database specific constant declarations
     */

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Movies.db";
    static final String MOVIES_TABLE_NAME = "movies";
    static final String TRAILERS_TABLE_NAME = "movies";
    static final String COMMENT_TABLE_NAME = "movies";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + MOVIES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " movieDBId TEXT NOT NULL, " +
                    " title TEXT NOT NULL, " +
                    " originalTitle TEXT, " +
                    " posterPath TEXT, " +
                    " backdropPath TEXT, " +
                    " overview TEXT, " +
                    " releaseDate TEXT, " +
                    " popularity TEXT);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */

    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  MOVIES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new movie record
         */
        long rowID = db.insert(	MOVIES_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MOVIES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                qb.setProjectionMap(MOVIES_PROJECTION_MAP);
                break;

            case MOVIE_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                qb.setProjectionMap(MOVIES_PROJECTION_MAP);
                break;
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            // sortOrder = POPULARITY;
            sortOrder = "";
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case MOVIES:
                count = db.delete(MOVIES_TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( MOVIES_TABLE_NAME, _ID +  " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                count = db.update(MOVIES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case MOVIE_ID:
                count = db.update(MOVIES_TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case MOVIES:
                return "vnd.android.cursor.dir/vnd.example.students";
            /**
             * Get a particular student
             */
            case MOVIE_ID:
                return "vnd.android.cursor.item/vnd.example.students";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
