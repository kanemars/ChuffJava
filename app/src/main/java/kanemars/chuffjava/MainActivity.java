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

import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

public class MainActivity extends AppCompatActivity {

    static AtomicInteger notificationCounter = new AtomicInteger ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        Calendar timeToNotify = Calendar.getInstance();
        timeToNotify.setTimeInMillis(System.currentTimeMillis());
        timeToNotify.add(Calendar.SECOND, 10);

        setUpRepeatingNotifaction(getDefaultJourney(), timeToNotify);

        //Toast.makeText(getApplicationContext(), "Setting up notifications", Toast.LENGTH_SHORT).show();
    }

    public void immediatelyShowNext2Trains(View view) {
        Journey journey = getDefaultJourney();
        Spanned msg = ChuffNotificationReceiver.getNext2Departures(journey);

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setUpRepeatingNotifaction (Journey journey, Calendar timeToNotify) {
        Intent notificationIntent = new Intent(getBaseContext(), ChuffNotificationReceiver.class);
        notificationIntent.putExtra("source", journey.source);
        notificationIntent.putExtra("destination", journey.destination);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int minutes = 1;
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToNotify.getTimeInMillis(), pendingIntent);
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, timeToNotify.getTimeInMillis(), 1000 * 60 * minutes, pendingIntent);
    }

    private Journey getDefaultJourney () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String source = sharedPreferences.getString("source_station", "TAP");
        String destination = sharedPreferences.getString("destination_station", "RDG");
        return new Journey(source, destination);
    }
}
