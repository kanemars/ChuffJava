package kanemars.chuffjava;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.view.View;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import static kanemars.KaneHuxleyJavaConsumer.StationCodes.STATION_CRS_CODES;

public class MainActivity extends AppCompatActivity {

    static AtomicInteger notificationCounter = new AtomicInteger ();
    AutoCompleteTextView source;
    AutoCompleteTextView destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, STATION_CRS_CODES);
        source = (AutoCompleteTextView) findViewById(R.id.autoCompleteSource);
        destination = (AutoCompleteTextView) findViewById(R.id.autoCompleteDestination);
        source.setAdapter(adapter);
        destination.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChuffSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onStartServiceButtonClick(View view) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.notificationTimePicker);
        Toast.makeText(getApplicationContext(), "Starting notification at " + timePicker.getHour() + ':' + timePicker.getMinute(), Toast.LENGTH_SHORT).show();

        Calendar timeToNotify = Calendar.getInstance();
        timeToNotify.setTimeInMillis(System.currentTimeMillis());
        timeToNotify.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        timeToNotify.set(Calendar.MINUTE, timePicker.getMinute());

        setUpRepeatingNotifaction(getDefaultJourney(), timeToNotify);
    }

    public void immediatelyShowNext2Trains(View view) {
        Spanned msg = ChuffNotificationReceiver.getNext2Departures(getDefaultJourney());

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setUpRepeatingNotifaction (Journey journey, Calendar timeToNotify) {
        Intent notificationIntent = new Intent(getBaseContext(), ChuffNotificationReceiver.class);
        notificationIntent.putExtra("source", journey.source);
        notificationIntent.putExtra("destination", journey.destination);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToNotify.getTimeInMillis(), pendingIntent);
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, timeToNotify.getTimeInMillis(), 1000 * 60 * minutes, pendingIntent);
    }

    private Journey getDefaultJourney () {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        String source = sharedPreferences.getString("source_station", "TAP");
//        String destination = sharedPreferences.getString("destination_station", "RDG");
        return new Journey(source.getText().toString(), destination.getText().toString());
    }
}
