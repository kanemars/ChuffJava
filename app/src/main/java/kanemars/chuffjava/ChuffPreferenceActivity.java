package kanemars.chuffjava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

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

            SwitchPreference switchPreference = (SwitchPreference) findPreference("notification_preference");
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference arg0, Object isNotificationOnObj) {
                    boolean isNotificationOn = (Boolean) isNotificationOnObj;
                    if (isNotificationOn) {

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String strSource = sharedPreferences.getString("edit_text_source", "Taplow - TAP");
                        String strDestination = sharedPreferences.getString("edit_text_destination", "Reading - RDG");

                        long strNotificationTime = sharedPreferences.getLong( "notification_time", 1232);
                        Calendar timeToNotify = Calendar.getInstance();
                        timeToNotify.setTimeInMillis(strNotificationTime);

                        String time = DateFormat.getTimeFormat(getContext()).format(new Date(timeToNotify.getTimeInMillis()));

                        Toast.makeText(getActivity(), String.format("Notifications set up for %s to %s at %s",
                                strSource, strDestination, time), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
    }
}
