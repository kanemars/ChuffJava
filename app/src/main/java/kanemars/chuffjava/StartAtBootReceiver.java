package kanemars.chuffjava;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TextView;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.Date;

import static kanemars.chuffjava.Constants.*;
import static kanemars.chuffjava.MainActivity.getNotificationTime;
import static kanemars.chuffjava.MainActivity.log;

public class StartAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This gets called when Android starts up
        enableBluetooth();
        sendHelloNotificationAndStartNotifications (context);
    }

    private void sendHelloNotificationAndStartNotifications (Context context) {
        SharedPreferences chuffPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String strSource = chuffPreferences.getString(KEY_SOURCE, "Taplow - TAP");
        String strDestination = chuffPreferences.getString(KEY_DESTINATION, "Reading - RDG");
        Journey journey = new Journey(strSource, strDestination);
        boolean notificationOn = chuffPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        long notificationTime = getNotificationTime(chuffPreferences);
        String message = !notificationOn ? "Notifications are turned off" : String.format("%s will be notified at %s",
                journey,
                DateFormat.getTimeFormat(context).format(new Date(notificationTime)));

        Notification notification =
                new Notification.Builder(context).setContentTitle("Welcome to Chuff Me")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_chuff_me)
                        .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        if (notificationOn) {
            startNotificationsAtBoot(context, journey, notificationTime);
        }
    }

    private void startNotificationsAtBoot (Context context, Journey journey, long notificationTime) {

        Intent notificationIntent = new Intent(context, ChuffNotificationReceiverFromBoot.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notificationIntent.putExtra(KEY_JOURNEY, journey);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        // Else no need to change bluetooth state
    }
}
