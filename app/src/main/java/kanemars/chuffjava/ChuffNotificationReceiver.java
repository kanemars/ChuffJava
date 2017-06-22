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

import static kanemars.chuffjava.Constants.*;

public class ChuffNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);

        String daysSelected = (String) intent.getExtras().getSerializable(KEY_DAYS_OF_WEEK);

        if (daysSelected.indexOf(Integer.toString(dayOfWeek)) > 0) {
            Journey journey = (Journey) intent.getExtras().getSerializable(KEY_JOURNEY);

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

    private static void ShowChufferNotification(Context context, Journey journey, String message, int sound) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(NOTIFICATION_INTENT_FLAGS);

        Notification notification =
                new Notification.Builder(context).setContentTitle(journey.toString())
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_chuff_me)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + sound))
                        .setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                        .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(CHUFF_ME_NOTIFICATION_ID, notification);
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
