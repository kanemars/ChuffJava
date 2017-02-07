package kanemars.chuffjava;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import kanemars.KaneHuxleyJavaConsumer.Models.JourneyException;

import java.util.Calendar;
import java.util.Date;

import static kanemars.chuffjava.Constants.KEY_SOURCE;
import static kanemars.chuffjava.Constants.KEY_DESTINATION;
import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_TIME;
import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_ON;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.chuffToolbar);
        setSupportActionBar(myToolbar);
        ChuffPreferences chuffPreferences = new ChuffPreferences(this);
        showNextNotification(chuffPreferences);
        try {
            ChuffAlarm.startAlarmIfNotificationOn(this, chuffPreferences);
        } catch (JourneyException ex) {

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        showNextNotification(new ChuffPreferences(this));
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
        try {
            Spanned departuresHTML = ChuffNotificationReceiver.getNext2Departures(getJourney());
            Toast.makeText(getApplicationContext(), departuresHTML, Toast.LENGTH_LONG).show();
        } catch (JourneyException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Journey getJourney() throws JourneyException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strSource = sharedPreferences.getString(KEY_SOURCE, "Taplow - TAP");
        String strDestination = sharedPreferences.getString(KEY_DESTINATION, "Reading - RDG");

        return new Journey(strSource, strDestination);
    }

    private void showNextNotification(ChuffPreferences preferences) {
        TextView textView = (TextView) findViewById(R.id.nextNotificationTextView);
        textView.setText(preferences.notificationOn ? String.format("%s to %s will be notified at %s",
                preferences.source,
                preferences.destination,
                preferences.notificationTime) : "Notifications are turned off");

        Button checkTimesButton = (Button) findViewById(R.id.checkTimesButton);
        checkTimesButton.setText(String.format("Check times between %s and %s now", preferences.source, preferences.destination));

    }
}
