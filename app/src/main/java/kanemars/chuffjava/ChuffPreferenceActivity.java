package kanemars.chuffjava;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.text.format.DateFormat;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import kanemars.KaneHuxleyJavaConsumer.Models.JourneyException;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static kanemars.KaneHuxleyJavaConsumer.StationCodes.GetCrs;
import static kanemars.chuffjava.Constants.KEY_SOURCE;
import static kanemars.chuffjava.Constants.KEY_DESTINATION;
import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_TIME;
import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_ON;

public class ChuffPreferenceActivity extends PreferenceActivity {

    static AtomicInteger notificationCounter = new AtomicInteger ();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ChuffPreferenceFragment()).commit();
    }

    public static class ChuffPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SwitchPreference switchPreference = (SwitchPreference) findPreference(KEY_NOTIFICATION_ON);
            switchPreference.setOnPreferenceChangeListener(getOnPreferenceChangeListener());
        }

        private Preference.OnPreferenceChangeListener getOnPreferenceChangeListener() {
            return new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object isNotificationOnObj) {
                    boolean isNotificationOn = (Boolean) isNotificationOnObj;
                    if (isNotificationOn) {

                        try {
                            ChuffAlarm.startAlarm(getActivity());

                            Toast.makeText(getActivity(), String.format("Notifications set up for %s to %s at %s",
                                    ChuffAlarm.journey.source, ChuffAlarm.journey.destination, ChuffAlarm.time), Toast.LENGTH_LONG).show();

                        } catch (JourneyException ex) {
                            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (ChuffAlarm.stopAlarm()) {
                        Toast.makeText(getActivity(), String.format("Cancelled notifications between %s and %s at %s",
                                ChuffAlarm.journey.source, ChuffAlarm.journey.destination, ChuffAlarm.time), Toast.LENGTH_LONG).show();

                    }
                    return true;
                }
            };
        }
    }
}
