package kanemars.chuffjava;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.widget.ProgressBar;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import kanemars.KaneHuxleyJavaConsumer.Models.TrainService;
import kanemars.KaneHuxleyJavaConsumer.GetDeparturesAsyncTask;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static kanemars.chuffjava.ChuffPreferenceActivity.notificationCounter;
import static kanemars.chuffjava.Constants.KEY_JOURNEY;

public class ChuffNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {

            Journey journey = (Journey) intent.getExtras().getSerializable(KEY_JOURNEY);

            try {
                NextTwoDepartures departures = ChuffNotificationReceiver.getNext2Departures(journey);
                if (departures.areTrainsOnTime()) {
                    ChuffNotificationReceiver.ShowChufferNotification(context, journey.toString(), departures.toString(), notificationCounter.getAndIncrement(), R.raw.thomas_whistle);
                } else {
                    ChuffNotificationReceiver.ShowChufferNotification(context, journey.toString(), departures.toString(), notificationCounter.getAndIncrement(), R.raw.exhale);
                }
            } catch (Exception e) {
                ChuffNotificationReceiver.ShowChufferNotification(context, journey.toString(), e.getMessage(), notificationCounter.getAndIncrement(), R.raw.exhale);
            }
        }
    }

    private static void ShowChufferNotification(Context context, String journey, String message, int uniqueId, int sound) {
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
