package kanemars.chuffjava;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import kanemars.KaneHuxleyJavaConsumer.GetDeparturesAsyncTask;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

import java.util.Calendar;

import static kanemars.chuffjava.ChuffPreferenceActivity.notificationCounter;
import static kanemars.chuffjava.Constants.KEY_JOURNEY;

public class ChuffNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //MainActivity.log ("onReceive called with context " + context.getPackageCodePath() + "; " + context.getPackageName());

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {

            Journey journey = (Journey) intent.getExtras().getSerializable(KEY_JOURNEY);

            try {
                NextTwoDepartures departures = getNext2Departures(journey);
                if (departures.areTrainsOnTime()) {
                    ShowChufferNotification(context, journey.toString(), departures.toString(), notificationCounter.getAndIncrement(), R.raw.thomas_whistle);
                } else {
                    ShowChufferNotification(context, journey.toString(), departures.toString(), notificationCounter.getAndIncrement(), R.raw.exhale);
                }
            } catch (Exception e) {
                ShowChufferNotification(context, journey.toString(), e.getMessage(), notificationCounter.getAndIncrement(), R.raw.exhale);
            }
        }
    }

    protected static void ShowChufferNotification(Context context, String journey, String message, int uniqueId, int sound) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification notification =
                new Notification.Builder(context).setContentTitle(journey)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_chuff_me)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + sound))
                        .setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                        .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        //MainActivity.log("Notification " + uniqueId + " called by " + stackTraceElements[2]);

        notificationManager.notify(uniqueId, notification);
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
