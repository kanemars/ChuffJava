package kanemars.chuffme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.Set;

import static kanemars.chuffme.Constants.*;

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

            findPreference(KEY_NOTIFICATION_ON).setOnPreferenceChangeListener(getNotificationOnChangeListener());
            findPreference(KEY_DAYS_OF_WEEK).setOnPreferenceChangeListener(getDaysChangeListener());

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean notificationOn = sharedPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
            setPreferences(notificationOn);
        }

        void setPreferences(boolean isNotificationChecked) {
            findPreference(KEY_SOURCE).setEnabled(!isNotificationChecked);
            findPreference(KEY_DESTINATION).setEnabled(!isNotificationChecked);
            findPreference(KEY_NOTIFICATION_TIME).setEnabled(!isNotificationChecked);
            findPreference(KEY_DAYS_OF_WEEK).setEnabled(!isNotificationChecked);
        }

        private Preference.OnPreferenceChangeListener getDaysChangeListener() {
            return new Preference.OnPreferenceChangeListener() {

                @Override
                @SuppressWarnings("unchecked")
                public boolean onPreferenceChange(Preference preference, Object days) {
                    Set<String> selectedDays = (Set<String>) days;

                    findPreference(Constants.KEY_NOTIFICATION_ON).setEnabled(!selectedDays.isEmpty());
                    findPreference(KEY_DAYS_OF_WEEK).setSummary(getShortDays (selectedDays));
                    return true;
                }
            };
        }

        private static String getShortDays (Set<String> numbers) {

            if (numbers.isEmpty()) {
                // Disable notificationOn button
                return "No days selected";
            }
            StringBuilder days = new StringBuilder();
            DateFormatSymbols symbols = new DateFormatSymbols();
            for (String number : numbers) {
                Integer dayOfWeek = Integer.parseInt(number);
                days.append(symbols.getShortWeekdays()[dayOfWeek]).append(", ");
            }
            return days.toString().substring(0, days.length() - 2);
        }

        private Preference.OnPreferenceChangeListener getNotificationOnChangeListener() {
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
