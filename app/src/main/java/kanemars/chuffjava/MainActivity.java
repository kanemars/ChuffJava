package kanemars.chuffjava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import kanemars.KaneHuxleyJavaConsumer.Models.JourneyException;

import static kanemars.KaneHuxleyJavaConsumer.StationCodes.GetCrs;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.chuffToolbar);
        setSupportActionBar(myToolbar);
        showNextNotification();

    }

    @Override
    protected void onResume() {
        super.onResume();
        showNextNotification();
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
            Toast.makeText(getApplicationContext(), ChuffNotificationReceiver.getNext2Departures(getJourney()), Toast.LENGTH_LONG).show();
        } catch (JourneyException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Journey getJourney() throws JourneyException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strSource = sharedPreferences.getString("edit_text_source", "Taplow - TAP");
        String strDestination = sharedPreferences.getString("edit_text_destination", "Reading - RDG");

        return new Journey(strSource, strDestination);
    }

    private void showNextNotification() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strSource = sharedPreferences.getString("edit_text_source", "Taplow - TAP");
        String strDestination = sharedPreferences.getString("edit_text_destination", "Reading - RDG");
        TextView textView = (TextView) findViewById(R.id.nextNotificationTextView);
        textView.setText(String.format("%s to %s will be notified at ", strSource, strDestination));

    }
}
