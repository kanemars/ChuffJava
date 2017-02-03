package kanemars.chuffjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import static kanemars.chuffjava.Constants.KEY_SOURCE;
import static kanemars.chuffjava.Constants.KEY_DESTINATION;
import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_TIME;
import static kanemars.chuffjava.Constants.KEY_NOTIFICATION_ON;

public class ChuffRebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ChuffPreferences preferences = new ChuffPreferences(context);
    }

}
