package kanemars.chuffjava;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import static kanemars.chuffjava.Constants.*;
import static kanemars.chuffjava.Constants.CHUFF_ME_NOTIFICATION_ID;

public class StartAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This gets called when Android starts up
//        enableBluetooth();
        sendHelloNotificationAndStartNotifications (context);
    }

    private void sendHelloNotificationAndStartNotifications (Context context) {
        SharedPreferences chuffPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String strSource = chuffPreferences.getString(KEY_SOURCE, "Taplow - TAP");
        String strDestination = chuffPreferences.getString(KEY_DESTINATION, "Maidenhead - MAI");
        Journey journey = new Journey(strSource, strDestination);
        String message = new NotificationTime(chuffPreferences).toString(context, journey);

        Notification notification =
                new Notification.Builder(context).setContentTitle("Welcome to Chuff Me")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_chuff_me)
                        .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(CHUFF_ME_NOTIFICATION_ID, notification);

        boolean notificationOn = chuffPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        if (notificationOn) {
            startNotificationsAtBoot(context);
        }
    }

    private void startNotificationsAtBoot (Context context) {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        // Else no need to change bluetooth state
    }
}
