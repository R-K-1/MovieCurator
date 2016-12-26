package com.example.ray.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigInteger;

/**
 * Created by Ray on 11/6/2016.
 */

public class Movie implements Parcelable {
    private BigInteger mId;
    private String mTitle;
    private String mOriginalTitle;
    private String mPosterPath;
    private String mBackdropPath;
    private String mOverview;
    private String mReleaseDate;
    private double mPopularity;
    private boolean mInPopular;
    private boolean mInTopRated;
    private boolean mInFavorites;

    public Movie(BigInteger id, String title, String posterPath, String backdropPath, String overview,
                 String releaseDate, double popularity) {
        this.mId = id;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
        this.mPopularity = popularity;
    }

    public Movie(BigInteger id, String title, String posterPath, String backdropPath, String overview,
                 String releaseDate, double popularity, boolean isPopular, boolean isTopRated,
                 boolean isFavorite) {
        this.mId = id;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
        this.mPopularity = popularity;
        this.mInPopular = isPopular;
        this.mInTopRated = isTopRated;
        this.mInFavorites = isFavorite;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public BigInteger getmId() {
        return mId;
    }

    public void setmId(BigInteger mId) {
        this.mId = mId;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String originalTitle) {
        this.mTitle = mTitle;
    }

    public String getmBackdropPath() {
        return mBackdropPath;
    }

    public void setmBackdropPath(String mBackdropPath) {
        this.mBackdropPath = mBackdropPath;
    }

    public double getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(double mPopularity) {
        this.mPopularity = mPopularity;
    }

    public boolean ismInPopular() { return mInPopular; }

    public void setmInPopular(boolean mInPopular) { this.mInPopular = mInPopular; }

    public boolean ismInTopRated() { return mInTopRated; }

    public void setmInTopRated(boolean mInTopRated) { this.mInTopRated = mInTopRated; }

    public boolean ismInFavorites() { return mInFavorites; }

    public void setmInFavorites(boolean mInFavorites) { this.mInFavorites = mInFavorites; }

    protected Movie(Parcel in) {
        mId = (BigInteger) in.readValue(BigInteger.class.getClassLoader());
        mTitle = in.readString();
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mPopularity = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeString(mTitle);
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mPopularity);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}