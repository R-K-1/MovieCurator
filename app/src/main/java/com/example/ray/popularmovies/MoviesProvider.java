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

    static final String _ID = "_id";
    static final String MOVIE_DB_ID = "movieDBId";
    static final String TITLE = "title";
    static final String ORIGINAL_TITLE = "originalTitle";
    static final String POSTER_PATH = "posterPath";
    static final String BACKDROP_PATH = "backdropPath";
    static final String OVERVIEW = "overview";
    static final String RELEASE_DATE = "releaseDate";
    static final String POPULARITY = "popularity";
    static final String IS_POPULAR = "isPopular";
    static final String IS_TOP_RATED = "isTopRated";
    static final String IS_FAVORITE = "isFavorite";

    static final String FK_MOVIE_MOVIE_DB_ID = "FKMovieMovieDBId";
    static final String TRAILER_MOVIE_DB_ID = "TrailerMovieDBId";
    static final String KEY = "Key";
    static final String NAME = "Name";
    static final String SITE = "Site";

    static final String REVIEW_MOVIE_DB_ID = "ReviewMovieDBId";
    static final String AUTHOR = "Author";
    static final String CONTENT = "Content";

    private static HashMap<String, String> MOVIES_PROJECTION_MAP;
    private static HashMap<String, String> TRAILERS_PROJECTION_MAP;
    private static HashMap<String, String> REVIEWS_PROJECTION_MAP;

    static final int MOVIES = 1;
    static final int MOVIE_ID = 2;
    static final int TRAILERS = 3;
    static final int REVIEWS = 4;
    static final int MOVIE_TRAILERS = 5;
    static final int MOVIE_REVIEWS = 6;
    static final int FAVORITE_MOVIES_ID = 7;
    static final int POPULAR_MOVIES_ID = 8;
    static final int TOP_RATED_MOVIES_ID = 9;

    static final String BASE = "content://" + PROVIDER_NAME;
    static final Uri BASE_URI = Uri.parse(BASE);
    static final String MOVIES_BASE = "content://" + PROVIDER_NAME + "/movies";
    static final Uri MOVIES_BASE_URI = Uri.parse(MOVIES_BASE);
    static final String MOVIE_URI = MOVIES_BASE + "/";
    static final Uri FAVORITE_MOVIES_URI = Uri.parse(MOVIES_BASE + "/favorites");
    static final Uri INSERT_TRAILERS_URI = Uri.parse(BASE + "/trailers");
    static final String GET_TRAILERS_URI = BASE + "/trailers/";
    static final Uri INSERT_REVIEWS_URI = Uri.parse(BASE + "/reviews");
    static final String GET_REVIEWS_URI = BASE + "/reviews/";



    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "movies", MOVIES);
        uriMatcher.addURI(PROVIDER_NAME, "movies/#", MOVIE_ID);
        uriMatcher.addURI(PROVIDER_NAME, "movies/favorites", FAVORITE_MOVIES_ID);
        uriMatcher.addURI(PROVIDER_NAME, "movies/popular", POPULAR_MOVIES_ID);
        uriMatcher.addURI(PROVIDER_NAME, "movies/toprated", TOP_RATED_MOVIES_ID);
        uriMatcher.addURI(PROVIDER_NAME, "trailers", TRAILERS);
        uriMatcher.addURI(PROVIDER_NAME, "reviews", REVIEWS);
        uriMatcher.addURI(PROVIDER_NAME, "trailers/#", MOVIE_TRAILERS);
        uriMatcher.addURI(PROVIDER_NAME, "reviews/#", MOVIE_REVIEWS);
    }

    /**
     * Database specific constant declarations
     */

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Movies.db";
    static final String MOVIES_TABLE_NAME = "movies";
    static final int DATABASE_VERSION = 3;
    static final String CREATE_MOVIES_DB_TABLE =
            " CREATE TABLE " + MOVIES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " movieDBId TEXT NOT NULL, " +
                    " title TEXT NOT NULL, " +
                    " originalTitle TEXT, " +
                    " posterPath TEXT, " +
                    " backdropPath TEXT, " +
                    " overview TEXT, " +
                    " releaseDate TEXT, " +
                    " popularity TEXT, " +
                    " isPopular INTEGER, " +
                    " isTopRated INTEGER, " +
                    " isFavorite INTEGER);";

    static final String TRAILERS_TABLE_NAME = "trailers";
    static final String CREATE_TRAILERS_DB_TABLE =
            " CREATE TABLE " + TRAILERS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " TrailerMovieDBId TEXT NOT NULL, " +
                    " Key TEXT NOT NULL, " +
                    " Name TEXT, " +
                    " Site TEXT, " +
                    " FKMovieMovieDBId TEXT);";

    static final String REVIEWS_TABLE_NAME = "reviews";
    static final String CREATE_REVIEWS_DB_TABLE =
            " CREATE TABLE " + REVIEWS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " ReviewMovieDBId TEXT NOT NULL, " +
                    " Author TEXT, " +
                    " Content TEXT NOT NULL, " +
                    " FKMovieMovieDBId TEXT);";

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
            db.execSQL(CREATE_MOVIES_DB_TABLE);
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
        // long rowID = db.insert(	MOVIES_TABLE_NAME, "", values);
        long rowID = 0;

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                rowID = db.insert(	MOVIES_TABLE_NAME, "", values);
                break;

            case TRAILERS:
                rowID = db.insert(	TRAILERS_TABLE_NAME, "", values);
                break;

            case REVIEWS:
                rowID = db.insert(	REVIEWS_TABLE_NAME, "", values);
                break;
        }
        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(MOVIES_BASE_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                qb.setTables(MOVIES_TABLE_NAME);
                qb.setProjectionMap(MOVIES_PROJECTION_MAP);
                break;

            case MOVIE_ID:
                qb.setTables(MOVIES_TABLE_NAME);
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            case MOVIE_TRAILERS:
                qb.setTables(TRAILERS_TABLE_NAME);
                qb.appendWhere( FK_MOVIE_MOVIE_DB_ID + "=" + uri.getPathSegments().get(1));
                // qb.setProjectionMap(TRAILERS_PROJECTION_MAP);
                break;

            case MOVIE_REVIEWS:
                qb.setTables(REVIEWS_TABLE_NAME);
                qb.appendWhere( FK_MOVIE_MOVIE_DB_ID + "=" + uri.getPathSegments().get(1));
                // qb.setProjectionMap(REVIEWS_PROJECTION_MAP);
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
                    MOVIE_DB_ID + " = " + uri.getPathSegments().get(1), selectionArgs);
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
