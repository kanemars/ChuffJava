package kanemars.chuffjava;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import kanemars.KaneHuxleyJavaConsumer.GetDeparturesAsyncTask;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.Calendar;

import static kanemars.chuffjava.ChuffPreferenceActivity.notificationCounter;
import static kanemars.chuffjava.Constants.KEY_JOURNEY;

public class ChuffNotificationReceiverFromBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Journey journey = (Journey) intent.getExtras().getSerializable(KEY_JOURNEY);

        Notification notification =
                new Notification.Builder(context).setContentTitle("Welcome to Chuff Me")
                        .setContentText(String.format("%s from reboot", journey))
                        .setSmallIcon(R.drawable.ic_chuff_me)
                        .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
