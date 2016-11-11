package com.example.ray.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ray on 11/1/2016.
 */

public class MoviesGridAdapter extends BaseAdapter {
    private Context mContext;
    private int mResource;
    private ArrayList<Movie> mMovies;

    public MoviesGridAdapter(Context context, int resource, ArrayList<Movie> movies) {
        // super(context, resource, products);
        this.mContext = context;
        this.mResource = resource;
        this.mMovies = movies;
    }

    public int getCount() { return this.mMovies.size();}

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null){
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            // imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setLayoutParams(new GridView.LayoutParams(150, 180));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // imageView.setPadding(8, 8, 8, 8);
            imageView.setPadding(0, 0, 0, 0);

        } else {
            imageView = (ImageView) convertView;
        }
        // Commenting out line below momemtarily because I get out of bound index exception
        // Movie movie = (Movie) movies[position];
        Movie movie = mMovies.get(position);

        Picasso.with(mContext).load(movie.getPosterPath()).into(imageView);


        return imageView;

    }
}
