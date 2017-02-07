package kanemars.chuffjava;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import kanemars.KaneHuxleyJavaConsumer.Models.JourneyException;

import java.util.Date;

import static kanemars.KaneHuxleyJavaConsumer.StationCodes.GetCrs;
import static kanemars.chuffjava.ChuffPreferenceActivity.notificationCounter;
import static kanemars.chuffjava.Constants.KEY_JOURNEY;

class ChuffAlarm {

    static AlarmManager alarmMgr;
    static PendingIntent pendingIntent;
    static Intent notificationIntent;
    static Journey journey;
    static String time;

    static void startAlarmIfNotificationOn(Context context) throws JourneyException {
        startAlarmIfNotificationOn (context, new ChuffPreferences(context));
    }


    static void startAlarmIfNotificationOn(Context context, ChuffPreferences preferences) throws JourneyException {
        journey = new Journey(GetCrs(preferences.source), GetCrs(preferences.destination));

        notificationIntent = new Intent(context, ChuffNotificationReceiver.class);
        notificationIntent.putExtra(KEY_JOURNEY, journey);

        pendingIntent = PendingIntent.getBroadcast(context, notificationCounter.getAndIncrement(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, preferences.timeToNotify.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        time =  DateFormat.getTimeFormat(context).format(new Date(preferences.timeToNotify.getTimeInMillis()));
    }

    static boolean stopAlarmIfRunning() {
        if (alarmMgr != null) {
            alarmMgr.cancel(pendingIntent);
            return true;
        }
        return false;
    }
}
