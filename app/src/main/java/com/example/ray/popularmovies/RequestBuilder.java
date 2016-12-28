package com.example.ray.popularmovies;

import android.net.Uri;

/**
 * Created by Ray on 12/6/2016.
 */

public class RequestBuilder {

    public static Uri.Builder buildGetMoviesURI(String filter) {

        String x = filter;
        return new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(x)
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                .appendQueryParameter("language", "en-US");

    }

    public static Uri.Builder buildGetTrailersURI(String movieId) {

        String x = movieId;
        return new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(x)
                .appendPath("videos")
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                .appendQueryParameter("language", "en-US");

    }

    public static Uri.Builder buildGetReviewsURI(String movieId) {

        String x = movieId;
        return new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(x)
                .appendPath("reviews")
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                .appendQueryParameter("language", "en-US");

    }

}
