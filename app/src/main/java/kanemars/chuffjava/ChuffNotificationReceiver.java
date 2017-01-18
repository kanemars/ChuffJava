package kanemars.chuffjava;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;
import kanemars.KaneHuxleyJavaConsumer.Models.TrainService;
import kanemars.KaneHuxleyJavaConsumer.RestfulAsynchTasks;

import java.util.concurrent.ExecutionException;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ChuffNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String source = intent.getStringExtra("source");
        String destination = intent.getStringExtra("destination");
        String title = "Trains from " + source + " to " + destination;
        try {
            String message = getNext2Departures(source, destination);

            //ShowChufferNotification(context, title, message);
        } catch (InterruptedException | ExecutionException e) {
            //msg = e.toString();
        }
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

    static String getNext2Departures(String source, String destination) throws InterruptedException, ExecutionException {
        String msg;
        AsyncTask<String, Integer, Departures> departuresAsyncTask = new RestfulAsynchTasks().execute(source, destination, "2");
        Departures departures = departuresAsyncTask.get();
        TrainService first = departures.trainServices.get(0);
        TrainService second = departures.trainServices.get(1);
        msg = String.format("%s %s; %s %s", first.std, first.etd, second.std, second.etd);
        return msg;
    }
}
