package kanemars.chuffjava;

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

import static kanemars.chuffjava.Constants.KEY_DESTINATION;
import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_ON;
import static kanemars.chuffjava.Constants.KEY_SOURCE;
import static kanemars.chuffjava.MainActivity.getNotificationTime;

public class StartAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This gets called when Android starts up
        enableBluetooth();
        sendHelloNotification(context);
    }

    private void sendHelloNotification(Context context) {
        SharedPreferences chuffPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String strSource = chuffPreferences.getString(KEY_SOURCE, "Taplow - TAP");
        String strDestination = chuffPreferences.getString(KEY_DESTINATION, "Reading - RDG");
        Journey journey = new Journey(strSource, strDestination);
        boolean notificationOn = chuffPreferences.getBoolean(KEY_NOTIFICATION_ON, false);
        String message = !notificationOn ? "Notifications are turned off" : String.format("%s will be notified at %s",
                journey,
                DateFormat.getTimeFormat(context).format(new Date(getNotificationTime(chuffPreferences))));

        Notification notification =
                new Notification.Builder(context).setContentTitle("Chuff Me started")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_chuff_me)
                        .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        // Else no need to change bluetooth state
    }
}
