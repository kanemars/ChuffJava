package kanemars.chuffjava;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.Calendar;
import java.util.Date;

import static kanemars.chuffjava.ChuffPreferenceActivity.notificationCounter;
import static kanemars.chuffjava.Constants.*;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    private Intent notificationIntent;
    private SharedPreferences chuffPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        notificationIntent.putExtra(KEY_JOURNEY, getJourney());
                        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, getNotificationTime(prefs), AlarmManager.INTERVAL_DAY, pendingIntent);
                    } else {
                        alarmMgr.cancel(pendingIntent);
                    }
                }
            }
        };
        chuffPreferences.registerOnSharedPreferenceChangeListener(listener);
        showNextNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNextNotification();
    }

    private long getNotificationTime (SharedPreferences prefs) {
        long strNotificationTime = prefs.getLong(KEY_NOTIFICATION_TIME, 1234);
        Calendar timeToNotify = Calendar.getInstance();
        timeToNotify.setTimeInMillis(strNotificationTime);
        return timeToNotify.getTimeInMillis();
    }

    private Journey getJourney() {
        //SharedPreferences chuffPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        Spanned departuresHTML = ChuffNotificationReceiver.getNext2Departures(getJourney());
        Toast.makeText(getApplicationContext(), departuresHTML, Toast.LENGTH_LONG).show();
    }



    private void showNextNotification() {
        Journey journey = getJourney();
        boolean notificationOn = chuffPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        TextView textView = (TextView) findViewById(R.id.nextNotificationTextView);
        textView.setText(notificationOn ? String.format("%s to %s will be notified at %s",
                journey.source,
                journey.destination,
                DateFormat.getTimeFormat(this).format(new Date(getNotificationTime (chuffPreferences))))
                : "Notifications are turned off");

        Button checkTimesButton = (Button) findViewById(R.id.checkTimesButton);
        checkTimesButton.setText(String.format("Check times between %s and %s now", journey.source, journey.destination));

    }
}
