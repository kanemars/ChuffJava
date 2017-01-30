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

            SwitchPreference switchPreference = (SwitchPreference) findPreference("notification_preference");
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object isNotificationOnObj) {
                    boolean isNotificationOn = (Boolean) isNotificationOnObj;
                    if (isNotificationOn) {

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String strSource = sharedPreferences.getString("edit_text_source", "Taplow - TAP");
                        String strDestination = sharedPreferences.getString("edit_text_destination", "Reading - RDG");

                        long strNotificationTime = sharedPreferences.getLong( "notification_time", 1232);
                        Calendar timeToNotify = Calendar.getInstance();
                        timeToNotify.setTimeInMillis(strNotificationTime);

                        String time = DateFormat.getTimeFormat(getContext()).format(new Date(timeToNotify.getTimeInMillis()));

                        try {
                            Journey journey = new Journey(GetCrs(strSource), GetCrs(strDestination));

                            Intent notificationIntent = new Intent(getActivity(), ChuffNotificationReceiver.class);
                            notificationIntent.putExtra("journey", journey);

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            AlarmManager alarmMgr = (AlarmManager) preference.getContext().getSystemService(Context.ALARM_SERVICE);

                            //alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToNotify.getTimeInMillis(), pendingIntent);
                            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, timeToNotify.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                            Toast.makeText(getActivity(), String.format("Notifications set up for %s to %s at %s",
                                    strSource, strDestination, time), Toast.LENGTH_LONG).show();

                        } catch (JourneyException ex) {
                            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
            });
        }
    }
}
