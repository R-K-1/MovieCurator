package com.example.ray.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ray on 12/12/2016.
 */

public class TrailersListAdapter extends BaseAdapter {
    private Context mContext;
    private int mResource;
    private ArrayList<String> mURLs;

    public TrailersListAdapter(Context context, int resource, ArrayList<String> URLs) {
        this.mContext = context;
        this.mResource = resource;
        this.mURLs = URLs;
    }

    public int getCount() { return this.mURLs.size();}

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

        File imageFile = utils.getImageFromInternalStorage(mContext, "", (mURLs.get(position) + ".jpg"));
        Picasso.with(mContext).load(imageFile).into(imageView);

        return imageView;

    }
}
