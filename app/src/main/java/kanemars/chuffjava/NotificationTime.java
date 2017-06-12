package kanemars.chuffjava;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.Calendar;
import java.util.Date;

import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_ON;
import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_TIME;

public class NotificationTime {
    private long notificationTimeInMillis;
    private boolean toStartOnSameDay = true;
    private boolean notificationOn = false;


    NotificationTime(SharedPreferences prefs) {
        notificationOn = prefs.getBoolean(KEY_NOTIFICATION_ON, false);
        long strNotificationTime = prefs.getLong(KEY_NOTIFICATION_TIME, 1234);

        Calendar timeToNotifyCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        timeToNotifyCal.setTimeInMillis(strNotificationTime);

        long intendedTime = timeToNotifyCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if (intendedTime >= currentTime) {
            notificationTimeInMillis = intendedTime;
        } else {
            timeToNotifyCal.add(Calendar.DAY_OF_MONTH, 1); // Start tomorrow
            notificationTimeInMillis = timeToNotifyCal.getTimeInMillis();
            toStartOnSameDay = false;
        }
    }

    long getInMillis() {
        return notificationTimeInMillis;
    }

    private String hhMM(Context context) {
        return DateFormat.getTimeFormat(context).format(new Date(notificationTimeInMillis));
    }

    String toString(Context context, Journey journey) {
        return !notificationOn ? "Notifications are turned off" :
                String.format("%s will be notified at %s", journey, hhMM(context));
    }

    public boolean isToStartOnSameDay () {
        return toStartOnSameDay;
    }
}
