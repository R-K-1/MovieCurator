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

/**
 * An activity representing a list of Movies. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link MovieActivity} representing item details. On tablets, this activity
 * and the movie activity are side by side. Still touching an item launches the fragment containing
 * the details
 * */
public class MainActivity extends AppCompatActivity {

    private OkHttpClient client;

    public static final String AUTHORITY = MoviesProvider.PROVIDER_NAME;
    public static final String ACCOUNT_TYPE = "com.example.ray.popularmovies.datasync";
    // Account
    public static final String ACCOUNT = "default_account";
    // Instance fields
    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing debugging tools to query the database and analyze web traffic
        Stetho.initializeWithDefaults(this);

        // Calling the function launching the daily job downloading server update
        scheduleAlarm();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing an http client object with debugging feature
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

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

        // Menu option to order movies by popularity
        if (id == R.id.order_by_popularity) {
            MoviesGridFragment moviesGridFragment =
                    (MoviesGridFragment) getFragmentManager().findFragmentById(R.id.movies_grid_in_fragment);
            moviesGridFragment.updateGrid(getString(R.string.db_filter_popular));
            return true;
        }

        // Menu option to order movies by ratings
        if (id == R.id.order_by_ratings) {
            MoviesGridFragment moviesGridFragment =
                    (MoviesGridFragment) getFragmentManager().findFragmentById(R.id.movies_grid_in_fragment);
            moviesGridFragment.updateGrid(getString(R.string.db_filter_top_rated));
            return true;
        }

        // Menu option to display only users'favorite movies
        if (id == R.id.order_by_favorites) {
            MoviesGridFragment moviesGridFragment =
                    (MoviesGridFragment) getFragmentManager().findFragmentById(R.id.movies_grid_in_fragment);
            moviesGridFragment.updateGrid(getString(R.string.db_filter_favorite));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // function called to schedule periodic job fetching updates from server
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
