package kanemars.chuffjava;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static kanemars.chuffjava.Constants.*;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    private Intent notificationIntent;
    private SharedPreferences chuffPreferences;
    private TextView logTextView;
    static long CHUFF_ALARM_INTERVAL = AlarmManager.INTERVAL_DAY; // 1 * 60 * 1000; //;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logTextView = (TextView) findViewById(R.id.logTextView);

        log("MainActivity creating!");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.chuffToolbar);
        setSupportActionBar(myToolbar);

        notificationIntent = new Intent(this, ChuffNotificationReceiver.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently
        chuffPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
    private static SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm");

    private void log(String message) {
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        logTextView.append(logDateFormat.format(Calendar.getInstance().getTime()) + ": " + message + System.getProperty("line.separator"));
    }

    private void startNotifications (SharedPreferences prefs) {
        notificationIntent.putExtra(KEY_JOURNEY, getJourney());
        //pendingIntent = PendingIntent.getBroadcast(getBaseContext(), notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        log("Called from MainActivity Intention is to start PendingIntent " + pendingIntent.toString() + " at " + DateFormat.getTimeFormat(this).format(new Date(getNotificationTime(chuffPreferences))));
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, getNotificationTime(prefs), CHUFF_ALARM_INTERVAL, pendingIntent);
    }

    private void stopNotifications () {
        alarmMgr.cancel(pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNotificationStatus();
    }

    static long getNotificationTime(SharedPreferences prefs) {
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
}
