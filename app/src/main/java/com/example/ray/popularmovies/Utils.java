package com.example.ray.popularmovies;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ray on 12/24/2016.
 */

public class Utils {
    public Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        // final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        final File directory = cw.getDir("My Movie Curator", Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                // new Thread(new Runnable() {
                    /*@Override
                    public void run() {*/
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

                    // }
                // }).start();
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
}
