package kanemars.chuffjava;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

import static kanemars.chuffjava.Constants.*;

public class ChuffPreferences {
    public String source;
    String destination;
    public String notificationTime;
    public boolean notificationOn;
    public Calendar timeToNotify;

    public ChuffPreferences (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        notificationOn = sharedPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        source = sharedPreferences.getString(KEY_SOURCE, "Taplow - TAP");
        destination = sharedPreferences.getString(KEY_DESTINATION, "Reading - RDG");
        long strNotificationTime = sharedPreferences.getLong(KEY_NOTIFICATION_TIME, 1234);
        timeToNotify = Calendar.getInstance();
        timeToNotify.setTimeInMillis(strNotificationTime);
        notificationTime = DateFormat.getTimeFormat(context).format(new Date(timeToNotify.getTimeInMillis()));
    }
}
