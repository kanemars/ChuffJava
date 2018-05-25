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
import static java.security.AccessController.getContext;
import static kanemars.chuffme.Constants.*;

public class ChuffNotificationBroadcastReceiver extends BroadcastReceiver {

    //private NotificationManager notificationManager;

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
                    NextTwoDepartures departures = getNext2Departures(journey);
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
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(NOTIFICATION_INTENT_FLAGS);

        String id = "chuffmeid";
        long [] vibrator = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
            .setContentTitle(message)
                .setSmallIcon(R.drawable.ic_chuff_me)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, 0))
                .setTicker(message)
                .setVibrate(vibrator);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (SDK_INT >= VERSION_CODES.O) { // Since android Oreo notification channel is needed.
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(id);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(id, "chuffme1", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Notification for chuffme");
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(vibrator);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(CHUFF_ME_NOTIFICATION_ID, builder.build());
    }

    static NextTwoDepartures getNext2Departures(Journey journey) throws Exception {
        AsyncTask<String, Integer, Departures> departuresAsyncTask;
        departuresAsyncTask = new GetDeparturesAsyncTask().execute(journey.crsSource, journey.crsDestination, "2");
        Departures departures = departuresAsyncTask.get();
        if (departures == null) {
            throw new Exception("Problem connecting to internet, try turning off WIFI");
        }

        return new NextTwoDepartures (departures.trainServices.get(0), departures.trainServices.get(1));
    }
}
