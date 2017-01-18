package kanemars.chuffjava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AtomicInteger notificationCounter = new AtomicInteger ();

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String source = sharedPreferences.getString("source_station", "TAP");
        String destination = sharedPreferences.getString("destination_station", "RDG");
        String msg;
        String title = "Trains from " + source + " to " + destination;
        try {
            msg = ChuffNotificationReceiver.getNext2Departures(source, destination);
        } catch (InterruptedException|ExecutionException e) {
            msg = e.toString();
        }

        ChuffNotificationReceiver.ShowChufferNotification (this, title, msg, notificationCounter.getAndIncrement());
    }

    public void immediatelyShowNext2Trains(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String source = sharedPreferences.getString("source_station", "TAP");
        String destination = sharedPreferences.getString("destination_station", "RDG");

        String msg;
        try {
            msg = ChuffNotificationReceiver.getNext2Departures(source, destination);
        } catch (InterruptedException|ExecutionException e) {
            msg = e.toString();
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


}
