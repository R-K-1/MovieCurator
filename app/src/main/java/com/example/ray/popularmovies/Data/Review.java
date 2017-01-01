package com.example.ray.popularmovies.Data;

/**
 * Created by Ray on 11/6/2016.
 */

public class Review {
    private String mId;
    private String mAuthor;
    private String mContent;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }


    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public Review(String id, String author, String content) {
        this.mId = id;
        this.mAuthor = author;
        this.mContent = content;
    }

}