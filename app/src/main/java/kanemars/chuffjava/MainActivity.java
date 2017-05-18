package kanemars.chuffjava;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.Calendar;
import java.util.Date;

import static kanemars.chuffjava.Constants.*;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    private Intent notificationIntent;
    private SharedPreferences chuffPreferences;
    private static TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logTextView = (TextView) findViewById(R.id.logTextView);

        log("Chuff creating!");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.chuffToolbar);
        setSupportActionBar(myToolbar);

        notificationIntent = new Intent(this, ChuffNotificationReceiver.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently
        chuffPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Start notifications if this has been saved
        boolean notificationOn = chuffPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        if (notificationOn) {
            startNotifications(chuffPreferences);
        }

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(KEY_NOTIFICATION_ON)) {
                    boolean notificationOn = prefs.getBoolean(KEY_NOTIFICATION_ON, false);
                    if (notificationOn) {
                        startNotifications (prefs);
                    } else {
                        stopNotifications ();
                    }
                }
            }
        };
        chuffPreferences.registerOnSharedPreferenceChangeListener(listener);

        showNotificationStatus();
    }

    static void log(String message) {
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        logTextView.append(message + System.getProperty("line.separator"));
    }

    private void startNotifications (SharedPreferences prefs) {
        notificationIntent.putExtra(KEY_JOURNEY, getJourney());
        //pendingIntent = PendingIntent.getBroadcast(getBaseContext(), notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, getNotificationTime(prefs), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void stopNotifications () {
        alarmMgr.cancel(pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNotificationStatus();
    }

    private long getNotificationTime (SharedPreferences prefs) {
        long strNotificationTime = prefs.getLong(KEY_NOTIFICATION_TIME, 1234);
        Calendar timeToNotify = Calendar.getInstance();
        timeToNotify.setTimeInMillis(strNotificationTime);
        return timeToNotify.getTimeInMillis();
    }

    private Journey getJourney() {
        String strSource = chuffPreferences.getString(KEY_SOURCE, "Taplow - TAP");
        String strDestination = chuffPreferences.getString(KEY_DESTINATION, "Reading - RDG");

        return new Journey(strSource, strDestination);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.journey_settings:
                try {
                    startActivity(new Intent(this, ChuffPreferenceActivity.class));
                } catch (Exception ex) {
                    return false;
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void immediatelyShowNext2Trains(View view) {
        log("Button pressed");

        TextView textView = (TextView) findViewById(R.id.trainTimesTextView);

        try {
            NextTwoDepartures departures = ChuffNotificationReceiver.getNext2Departures(getJourney());
            textView.setText(departures.toSpanned());
        } catch (Exception e) {
            textView.setText(e.getMessage());
        }
    }

    private void showNotificationStatus() {
        Journey journey = getJourney();
        boolean notificationOn = chuffPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        TextView textView = (TextView) findViewById(R.id.nextNotificationTextView);
        textView.setText(notificationOn ? String.format("%s will be notified at %s",
                journey,
                DateFormat.getTimeFormat(this).format(new Date(getNotificationTime (chuffPreferences))))
                : "Notifications are turned off");

        Button checkTimesButton = (Button) findViewById(R.id.checkTimesButton);
        checkTimesButton.setText(String.format("Check times from %s now", journey));
    }

    public boolean hasNetworkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
