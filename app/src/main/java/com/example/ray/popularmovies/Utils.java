package com.example.ray.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.example.ray.popularmovies.Data.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by Ray on 12/24/2016.
 */

public class Utils {
    public Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir("My Movie Curator", Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                final File myImageFile = new File(directory, imageName); // Create image file
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(myImageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    public Bitmap getBitmapFromCloud(Context context, String url) throws IOException {
        Bitmap mBitmap;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        return mBitmap = builder.build().with(context).load(url).get();
    }

    public void saveBitmapToInternalStorage (Context context, Bitmap bmp, String imageName) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("My Movie Curator", Context.MODE_PRIVATE);
        File myImageFile = new File(directory, imageName); // Create image file

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myImageFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());
    }

    public File getImageFromInternalStorage (Context context, String imageDir, String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("My Movie Curator", Context.MODE_PRIVATE);
        File myImageFile = new File(directory, imageName);
        return myImageFile;

    }

    public void deleteImageFromInternalStorage (Context context, String imageDir, String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("My Movie Curator", Context.MODE_PRIVATE);
        File myImageFile = new File(directory, imageName);
        if (myImageFile.delete()) {
            Log.i("image", "deleted " + imageName);
        } else {
            Log.i("image", "could not delete " + imageName);
        }
    }

    public Movie getMovieFromDB (String movieId, Activity activity) {
        MoviesProvider.DatabaseHelper y = new MoviesProvider.DatabaseHelper(activity.getApplicationContext());
        SQLiteDatabase db = y.getReadableDatabase();
        Uri uri = Uri.parse(MoviesProvider.MOVIE_URI + movieId);
        Cursor c = activity.getApplicationContext().getContentResolver().query(
                uri, null, null, null, null);

        Movie m = new Movie(
                new BigInteger("1"),
                activity.getString(R.string.stub_movie_title),
                "",
                "",
                "",
                "",
                1.0,
                0,
                0,
                0
        );

        if (c != null && c.getCount() ==1) {
            c.moveToFirst();
            m = new Movie(
                    new BigInteger(c.getString(c.getColumnIndex(MoviesProvider.MOVIE_DB_ID))),
                    c.getString(c.getColumnIndex(MoviesProvider.TITLE)),
                    new String(c.getString(c.getColumnIndex(MoviesProvider.POSTER_PATH))),
                    c.getString(c.getColumnIndex(MoviesProvider.BACKDROP_PATH)),
                    c.getString(c.getColumnIndex(MoviesProvider.OVERVIEW)),
                    c.getString(c.getColumnIndex(MoviesProvider.RELEASE_DATE)),
                    Double.parseDouble(c.getString(c.getColumnIndex(MoviesProvider.POPULARITY))),
                    (c.getInt(c.getColumnIndex(MoviesProvider.IS_POPULAR))),
                    (c.getInt(c.getColumnIndex(MoviesProvider.IS_TOP_RATED))),
                    (c.getInt(c.getColumnIndex(MoviesProvider.IS_FAVORITE)))
            );
        }

        return m;
    }
}
