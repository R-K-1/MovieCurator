package com.example.ray.popularmovies.Sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.ray.popularmovies.MovieDBAlarmService;

/**
 * Created by Ray on 12/24/2016.
 */

public class MovieDBAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.example.ray.popularmovies.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MovieDBAlarmService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
