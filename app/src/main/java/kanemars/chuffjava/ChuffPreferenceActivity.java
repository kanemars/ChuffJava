package kanemars.chuffjava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

import static kanemars.chuffjava.Constants.*;

public class ChuffPreferenceActivity extends PreferenceActivity {

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

//            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            boolean notificationOn = sharedPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
            setPreferences(notificationOn);
        }

        void setPreferences(boolean isNotificationChecked) {
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
                    setPreferences(isNotificationOn);
                    String message = "Notifications are now turned " + (isNotificationOn ? "on" : "off");
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    return true;
                }
            };
        }
    }
}
