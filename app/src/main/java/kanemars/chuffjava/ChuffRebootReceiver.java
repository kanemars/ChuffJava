package kanemars.chuffjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import kanemars.KaneHuxleyJavaConsumer.Models.JourneyException;

public class ChuffRebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ChuffAlarm.startAlarmIfNotificationOn(context);
        } catch (JourneyException e) {
            e.printStackTrace(); // Ignore exception
        }
    }

}
