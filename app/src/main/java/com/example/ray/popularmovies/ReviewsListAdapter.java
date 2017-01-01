package com.example.ray.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.ray.popularmovies.Data.Review;

import java.util.ArrayList;

/**
 * Created by Ray on 12/12/2016.
 */

public class ReviewsListAdapter extends BaseAdapter {
    private Context mContext;
    private int mResource;
    private ArrayList<Review> mReviews;

    public ReviewsListAdapter(Context context, int resource, ArrayList<Review> reviews) {
        // super(context, resource, products);
        this.mContext = context;
        this.mResource = resource;
        this.mReviews = reviews;
    }

    public int getCount() { return this.mReviews.size();}

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        Utils utils = new Utils();

        ImageView imageView;
        if (convertView == null){
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 180));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);

        } else {
            imageView = (ImageView) convertView;
        }

        /*File imageFile = utils.getImageFromInternalStorage(mContext, "", (mURLs.get(position) + ".jpg"));
        Picasso.with(mContext).load(imageFile).into(imageView);*/

        return imageView;

    }
}
