package kanemars.chuffjava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;
import kanemars.KaneHuxleyJavaConsumer.Models.JourneyException;

import java.util.concurrent.atomic.AtomicInteger;

import static kanemars.chuffjava.Constants.*;

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

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean notificationOn = sharedPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
            setPreferencesEnabled(notificationOn);
        }

        private void setPreferencesEnabled(boolean isNotificationChecked) {
            AutoCompletePreference source = (AutoCompletePreference) findPreference(KEY_SOURCE);
            AutoCompletePreference destination = (AutoCompletePreference) findPreference(KEY_DESTINATION);
            TimePreference timePreference = (TimePreference) findPreference(KEY_NOTIFICATION_TIME);
            source.setEnabled(!isNotificationChecked);
            destination.setEnabled(!isNotificationChecked);
            timePreference.setEnabled(!isNotificationChecked);
        }

        private Preference.OnPreferenceChangeListener getOnPreferenceChangeListener() {
            return new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object isNotificationOnObj) {
                    boolean isNotificationOn = (Boolean) isNotificationOnObj;

                    setPreferencesEnabled(isNotificationOn);

                    if (isNotificationOn) {

                        try {
                            ChuffAlarm.startAlarmIfNotificationOn(getActivity());

                            Toast.makeText(getActivity(), String.format("Notifications set up for %s to %s at %s",
                                    ChuffAlarm.journey.source, ChuffAlarm.journey.destination, ChuffAlarm.time), Toast.LENGTH_LONG).show();

                        } catch (JourneyException ex) {
                            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (ChuffAlarm.stopAlarmIfRunning()) {
                        Toast.makeText(getActivity(), String.format("Cancelled notifications between %s and %s at %s",
                                ChuffAlarm.journey.source, ChuffAlarm.journey.destination, ChuffAlarm.time), Toast.LENGTH_LONG).show();

                    }
                    return true;
                }
            };
        }
    }
}
