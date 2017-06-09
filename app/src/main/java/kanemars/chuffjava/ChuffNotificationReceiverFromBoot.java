package kanemars.chuffjava;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.Calendar;

import static kanemars.chuffjava.ChuffNotificationReceiver.chuffMeNotificationId;
import static kanemars.chuffjava.ChuffNotificationReceiver.getNext2Departures;
import static kanemars.chuffjava.Constants.KEY_JOURNEY;

public class ChuffNotificationReceiverFromBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {

            Journey journey = (Journey) intent.getExtras().getSerializable(KEY_JOURNEY);

            try {
                NextTwoDepartures departures = getNext2Departures(journey);
                if (departures.areTrainsOnTime()) {
                    ShowChufferNotificationFromReboot(context, journey, departures.toString(), R.raw.thomas_whistle);
                } else {
                    ShowChufferNotificationFromReboot(context, journey, departures.toString(), R.raw.exhale);
                }
            } catch (Exception e) {
                ShowChufferNotificationFromReboot(context, journey, e.getMessage(), R.raw.exhale);
            }
        }
    }

    private static void ShowChufferNotificationFromReboot(Context context, Journey journey, String message, int sound) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification notification =
                new Notification.Builder(context).setContentTitle(journey.toString())
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_chuff_me)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + sound))
                        .setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                        .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(chuffMeNotificationId, notification);
    }
}
