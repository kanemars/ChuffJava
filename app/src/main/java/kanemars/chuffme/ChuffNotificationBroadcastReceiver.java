package kanemars.chuffme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import kanemars.KaneHuxleyJavaConsumer.GetDeparturesAsyncTask;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import java.util.Calendar;

import static android.os.Build.*;
import static android.os.Build.VERSION.*;
import static kanemars.chuffme.Constants.*;

public class ChuffNotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Calendar today = Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());
            int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);

            String daysSelected = (String) bundle.getSerializable(KEY_DAYS_OF_WEEK);

            if (daysSelected == null || daysSelected.indexOf(Integer.toString(dayOfWeek)) > 0) {
                Journey journey = new Journey(bundle.getString(KEY_SOURCE), bundle.getString(KEY_DESTINATION));

                try {
                    NextTwoDepartures departures = getNext2Departures(journey, context.getResources().getString(R.string.nointernet));
                    String message = departures.toString();
                    if (departures.areTrainsOnTime()) {
                        ShowChufferNotification(context, journey, message, R.raw.thomas_whistle);
                    } else {
                        ShowChufferNotification(context, journey, message, R.raw.exhale);
                    }
                } catch (Exception e) {
                    ShowChufferNotification(context, journey, e.getMessage(), R.raw.exhale);
                }
            }
        }
    }
    private static void ShowChufferNotification(Context context, Journey journey, String message, int sound) {
        // Below mention the Activity which needs to be
        // triggered when user clicks on notification(MainActivity.class in this case)

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + sound);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(journey.toString())
                .setSmallIcon(R.drawable.ic_chuff_me)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(message)
                .setVibrate(VIBRATOR)
                .setSound(soundUri);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (SDK_INT >= VERSION_CODES.O) { // Since android Oreo notification channel is needed.
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, "Default notifications", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Notification for chuffme");
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(VIBRATOR);
                notificationChannel.setSound(soundUri, AUDIO_ATTRIBUTES);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(CHUFF_ME_NOTIFICATION_ID, builder.build());
    }

    protected static NextTwoDepartures getNext2Departures(Journey journey, String noInternetMessage) throws Exception {
        AsyncTask<String, Integer, Departures> departuresAsyncTask;
        departuresAsyncTask = new GetDeparturesAsyncTask().execute(journey.crsSource, journey.crsDestination, "2");
        Departures departures = departuresAsyncTask.get();
        if (departures == null) {
            throw new Exception(noInternetMessage);
        }

        return new NextTwoDepartures (departures.trainServices.get(0), departures.trainServices.get(1));
    }
}
