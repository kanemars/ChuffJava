package kanemars.chuffjava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;
import kanemars.KaneHuxleyJavaConsumer.Models.TrainService;
import kanemars.KaneHuxleyJavaConsumer.RestfulAsynchTasks;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        Button findTimes = (Button) findViewById(R.id.checkTimesButton);
        findTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String source = sharedPreferences.getString("source_station", "TAP");
                String destination = sharedPreferences.getString("destination_station", "RDG");

                String msg;
                try {
                    AsyncTask<String, Integer, Departures> departuresAsyncTask = new RestfulAsynchTasks().execute(source, destination, "2");
                    Departures departures = departuresAsyncTask.get();
                    TrainService first = departures.trainServices.get(0);
                    TrainService second = departures.trainServices.get(1);
                    msg = String.format("%s %s; %s %s", first.std, second.etd, second.std, second.etd);
                } catch (Exception e) {
                    msg = e.toString();
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
