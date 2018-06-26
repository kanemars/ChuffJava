package kanemars.chuffme;

import android.app.*;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.HashSet;
import java.util.Set;

import static android.os.Build.VERSION.SDK_INT;
import static kanemars.chuffme.Constants.*;
import static kanemars.chuffme.Constants.CHUFF_ME_NOTIFICATION_ID;

public class StartAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This gets called when Android starts up
        //enableBluetooth();
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            sendHelloNotificationAndStartNotifications(context);
        }
    }

    private void sendHelloNotificationAndStartNotifications (Context context) {
        SharedPreferences chuffPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String strSource = chuffPreferences.getString(KEY_SOURCE, context.getString(R.string.default_source_station));
        String strDestination = chuffPreferences.getString(KEY_DESTINATION, context.getString(R.string.default_destination_station));
        Journey journey = new Journey(strSource, strDestination);
        String message = new NotificationTime(chuffPreferences).toString(context, journey);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Welcome to Chuff Me")
                .setSmallIcon(R.drawable.ic_chuff_me)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setTicker(message)
                .setContentIntent(launchMainActivityIfUserClicksOnWelcomeNotification(context));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (SDK_INT >= Build.VERSION_CODES.O) { // Since android Oreo notification channel is needed.
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, "Default notifications", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Boot notification for chuffme");
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(VIBRATOR);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(CHUFF_ME_NOTIFICATION_ID, builder.build());

        boolean notificationOn = chuffPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        if (notificationOn) {
            startNotificationsAtBoot(context, chuffPreferences);
        }
    }

    private PendingIntent launchMainActivityIfUserClicksOnWelcomeNotification(Context context) {
        Intent launchMainActivityIntent = new Intent(context, MainActivity.class);
        launchMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context,
                0,
                launchMainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Now don't launch the MainActivity but instead just atart the AlarmManager
    private void startNotificationsAtBoot (Context context, SharedPreferences chuffPreferences) {
        Intent bootNotificationIntent = new Intent(context, ChuffNotificationBroadcastReceiver.class);

        Set<String> daysSelected = chuffPreferences.getStringSet(KEY_DAYS_OF_WEEK, new HashSet<String>());
        String daysSelectedDelim = daysSelected.toString();
        bootNotificationIntent.putExtra(KEY_DAYS_OF_WEEK, daysSelectedDelim);
        Journey journey = Constants.getJourney(chuffPreferences, context);
        bootNotificationIntent.putExtra(KEY_SOURCE, journey.source);
        bootNotificationIntent.putExtra(KEY_DESTINATION, journey.destination);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, PENDING_INTENT_REQUEST_CODE, bootNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, new NotificationTime(chuffPreferences).getInMillis(), CHUFF_ALARM_INTERVAL, pendingIntent);
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        // Else no need to change bluetooth state
    }
}
