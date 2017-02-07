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

            Spanned msg = ChuffNotificationReceiver.getNext2Departures(journey);

            ChuffNotificationReceiver.ShowChufferNotification(context, journey.toString(), msg.toString(), notificationCounter.getAndIncrement());
        }
    }

    private static void ShowChufferNotification(Context context, String title, String message, int uniqueId) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification notification =
                new Notification.Builder(context).setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_stat_train)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                        .build();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(uniqueId, notification);
    }

    static Spanned getNext2Departures(Journey journey) {
        AsyncTask<String, Integer, Departures> departuresAsyncTask;
        try {
            departuresAsyncTask = new GetDeparturesAsyncTask().execute(journey.crsSource, journey.crsDestination, "2");
            Departures departures = departuresAsyncTask.get();
            if (departures == null) {
                return fromHtml("Problem connecting to internet");
            }
            TrainService first = departures.trainServices.get(0);
            TrainService second = departures.trainServices.get(1);
            return fromHtml(String.format("<b>%s</b> %s; <b>%s</b> %s", first.std, first.etd, second.std, second.etd));
        } catch (InterruptedException | ExecutionException e) {
            return fromHtml(e.toString());
        }
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
