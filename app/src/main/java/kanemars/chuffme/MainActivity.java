package kanemars.chuffme;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.HashSet;
import java.util.Set;

import static kanemars.chuffme.Constants.*;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    private SharedPreferences chuffPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.chuffToolbar);
        setSupportActionBar(myToolbar);

        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        chuffPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        chuffPreferences.registerOnSharedPreferenceChangeListener(getNotificationButtonListener());

        showNotificationStatus();

        // Now this MainActivity may have been called from StartAtBootReceiver
        boolean notificationOn = chuffPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        if (notificationOn) {
            startNotifications(chuffPreferences);
            moveTaskToBack(true);
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener getNotificationButtonListener() {
        // http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently
        return new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                switch (key) {
                    case KEY_NOTIFICATION_ON:
                        boolean notificationOn = prefs.getBoolean(KEY_NOTIFICATION_ON, false);
                        if (notificationOn) {
                            startNotifications (prefs);
                        } else {
                            stopNotifications ();
                        }
                        break;
                }
            }
        };
    }

    private void startNotifications (SharedPreferences prefs) {
        Intent notificationIntent = new Intent(this, ChuffNotificationBroadcastReceiver.class);

        Set<String> daysSelected = prefs.getStringSet(KEY_DAYS_OF_WEEK, new HashSet<String>());
        String daysSelectedDelim = daysSelected.toString();
        notificationIntent.putExtra(KEY_DAYS_OF_WEEK, daysSelectedDelim);
        Journey journey = getJourney();
        notificationIntent.putExtra(KEY_SOURCE, journey.source);
        notificationIntent.putExtra(KEY_DESTINATION, journey.destination);

        //pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, new NotificationTime(prefs).getInMillis(), CHUFF_ALARM_INTERVAL, pendingIntent);
    }

    private void stopNotifications () {
        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager!=null)
        notificationManager.cancel(CHUFF_ME_NOTIFICATION_ID);
        if (pendingIntent != null) {
            alarmMgr.cancel(pendingIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNotificationStatus();
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
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.thomas_whistle);

        mediaPlayer.start();

        TextView textView = findViewById(R.id.trainTimesTextView);

        try {
            NextTwoDepartures departures = ChuffNotificationBroadcastReceiver.getNext2Departures(getJourney());
            textView.setText(departures.toSpanned());
        } catch (Exception e) {
            textView.setText(e.getMessage());
        }
    }

    private void showNotificationStatus() {
        Journey journey = getJourney();

        TextView textView = findViewById(R.id.nextNotificationTextView);
        textView.setText(new NotificationTime(chuffPreferences).toString(this, journey));

        Button checkTimesButton = findViewById(R.id.checkTimesButton);
        checkTimesButton.setText(String.format("Check times from %s now", journey));
    }
}
