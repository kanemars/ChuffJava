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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import kanemars.KaneHuxleyJavaConsumer.Models.JourneyException;

import static kanemars.KaneHuxleyJavaConsumer.StationCodes.GetCrs;
import static kanemars.KaneHuxleyJavaConsumer.StationCodes.STATION_CODES;

public class MainActivity extends AppCompatActivity {

    static AtomicInteger notificationCounter = new AtomicInteger ();
    private AutoCompleteTextView source;
    private AutoCompleteTextView destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.chuffToolbar);
        setSupportActionBar(myToolbar);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, STATION_CODES);
        source = (AutoCompleteTextView) findViewById(R.id.autoCompleteSource);
        destination = (AutoCompleteTextView) findViewById(R.id.autoCompleteDestination);
        source.setAdapter(adapter);
        destination.setAdapter(adapter);
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

    public void onStartServiceButtonClick(View view) {
        try {
            TimePicker timePicker = (TimePicker) findViewById(R.id.notificationTimePicker);

            Calendar timeToNotify = Calendar.getInstance();
            timeToNotify.setTimeInMillis(System.currentTimeMillis());
            timeToNotify.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            timeToNotify.set(Calendar.MINUTE, timePicker.getMinute());

            setUpRepeatingNotifaction(getJourney(), timeToNotify);
            Toast.makeText(getApplicationContext(), "Starting notification at " + timePicker.getHour() + ':' + timePicker.getMinute(), Toast.LENGTH_SHORT).show();
        } catch (JourneyException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void immediatelyShowNext2Trains(View view) {
        try {
            Toast.makeText(getApplicationContext(), ChuffNotificationReceiver.getNext2Departures(getJourney()), Toast.LENGTH_LONG).show();
        } catch (JourneyException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setUpRepeatingNotifaction (Journey journey, Calendar firstTimeToNotify) {
        Intent notificationIntent = new Intent(getBaseContext(), ChuffNotificationReceiver.class);
        notificationIntent.putExtra("journey", journey);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToNotify.getTimeInMillis(), pendingIntent);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, firstTimeToNotify.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private Journey getJourney() throws JourneyException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strSource = sharedPreferences.getString("edit_text_source", "TAP");
        String strDestination = sharedPreferences.getString("edit_text_destination", "RDG");

        return new Journey(GetCrs (strSource), GetCrs (strDestination));
    }
}
