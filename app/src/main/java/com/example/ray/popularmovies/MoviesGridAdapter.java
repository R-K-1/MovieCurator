package com.example.ray.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.ray.popularmovies.Data.Movie;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ray on 11/1/2016.
 */


public class MoviesGridAdapter extends BaseAdapter {
// public class MoviesGridAdapter extends CursorAdapter {
    private Context mContext;
    private int mResource;
    private ArrayList<Movie> mMovies;
    private Cursor mCursor;

    public MoviesGridAdapter(Context context, int resource, ArrayList<Movie> movies) {
        this.mContext = context;
        this.mResource = resource;
        this.mMovies = movies;
    }

    // TODO: CursorAdapter implementation
    /*public MoviesGridAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;
        this.mCursor = cursor;
    }*/

    public int getCount() { return this.mMovies.size();}
    // public int getCount() { return this.mCursor.getCount();}

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // TODO: CursorAdapter implementation
    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    /*@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movies_list_item, parent, false);
    }*/

    // TODO: CursorAdapter implementation
    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    /*@Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView;
        // if it's not recycled, initialize some attributes
        imageView = new ImageView(mContext);
        imageView.setLayoutParams(new GridView.LayoutParams(300, 360));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(0, 0, 0, 0);


        Utils utils = new Utils();
        File imageFile = utils.getImageFromInternalStorage(
                mContext, "", cursor.getString(cursor.getColumnIndex(MoviesProvider.POSTER_PATH)));
        Picasso.with(mContext).load(imageFile).into(imageView);
    }*/

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        Utils utils = new Utils();

        ImageView imageView;
        if (convertView == null){
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 360));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);

        } else {
            imageView = (ImageView) convertView;
        }

        Movie movie = mMovies.get(position);
        File imageFile = utils.getImageFromInternalStorage(mContext, "", movie.getmPosterPath());
        Picasso.with(mContext).load(imageFile).into(imageView);

        return imageView;
    }
}
