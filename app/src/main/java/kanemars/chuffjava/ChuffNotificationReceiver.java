package kanemars.chuffjava;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;
import kanemars.KaneHuxleyJavaConsumer.Models.TrainService;
import kanemars.KaneHuxleyJavaConsumer.RestfulAsynchTasks;

import java.util.concurrent.ExecutionException;

public class ChuffNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Journey journey = new Journey(intent.getStringExtra("source"), intent.getStringExtra("destination"));
    }

    static void ShowChufferNotification(Context context, String title, String message, int uniqueId) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification notification =
                new Notification.Builder(context).setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                        .build();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(uniqueId, notification);
    }

    static Spanned getNext2Departures(Journey journey) {
        try {
            AsyncTask<String, Integer, Departures> departuresAsyncTask = new RestfulAsynchTasks().execute(journey.source, journey.destination, "2");
            Departures departures = departuresAsyncTask.get();
            TrainService first = departures.trainServices.get(0);
            TrainService second = departures.trainServices.get(1);
            return fromHtml(String.format("<b>%s</b> %s; <b>%s</b> %s", first.std, first.etd, second.std, second.etd));
        } catch (InterruptedException | ExecutionException e) {
            return fromHtml(e.toString());
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
