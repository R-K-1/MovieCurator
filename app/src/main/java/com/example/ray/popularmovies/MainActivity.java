package com.example.ray.popularmovies;

import android.accounts.Account;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ray.popularmovies.Sync.MovieDBAlarmReceiver;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
// public class MainActivity extends FragmentActivity {

    private OkHttpClient client;

    // Constants
    // Content provider authority
    public static final String AUTHORITY = MoviesProvider.PROVIDER_NAME;
    /*"com.example.android.datasync.provider"*/
    // Account type
    // public static final String ACCOUNT_TYPE = "com.example.android.datasync";
    public static final String ACCOUNT_TYPE = "com.example.ray.popularmovies.datasync";
    // Account
    public static final String ACCOUNT = "default_account";
    // Instance fields
    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

        scheduleAlarm();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        // Pass the settings flags by inserting them in a bundle
/*        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.order_by_popularity) {
            MoviesGridFragment moviesGridFragment =
                    (MoviesGridFragment) getFragmentManager().findFragmentById(R.id.movies_grid_in_fragment);
            moviesGridFragment.updateGrid(getString(R.string.db_filter_popular));
            return true;
        }

        if (id == R.id.order_by_ratings) {
            MoviesGridFragment moviesGridFragment =
                    (MoviesGridFragment) getFragmentManager().findFragmentById(R.id.movies_grid_in_fragment);
            moviesGridFragment.updateGrid(getString(R.string.db_filter_top_rated));
            return true;
        }

        if (id == R.id.order_by_favorites) {
            MoviesGridFragment moviesGridFragment =
                    (MoviesGridFragment) getFragmentManager().findFragmentById(R.id.movies_grid_in_fragment);
            moviesGridFragment.updateGrid(getString(R.string.db_filter_favorite));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MovieDBAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MovieDBAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_DAY, pIntent);
    }

}
