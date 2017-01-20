package kanemars.chuffjava;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.view.View;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import kanemars.KaneHuxleyJavaConsumer.Models.JourneyException;

import static kanemars.KaneHuxleyJavaConsumer.StationCodes.STATION_CRS_CODES;

public class MainActivity extends AppCompatActivity {

    static AtomicInteger notificationCounter = new AtomicInteger ();
    private AutoCompleteTextView source;
    private AutoCompleteTextView destination;

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
            Toast.makeText(getApplicationContext(), ChuffNotificationReceiver.getNext2Departures(getJourney()), Toast.LENGTH_SHORT).show();
        } catch (JourneyException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpRepeatingNotifaction (Journey journey, Calendar firstTimeToNotify) {
        Intent notificationIntent = new Intent(getBaseContext(), ChuffNotificationReceiver.class);
        notificationIntent.putExtra("journey", journey);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToNotify.getTimeInMillis(), pendingIntent);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, firstTimeToNotify.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }

    private Journey getJourney() throws JourneyException {
        return new Journey(source.getText().toString(), destination.getText().toString());
    }
}
