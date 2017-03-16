package kanemars.chuffjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartMainActivityAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This gets called when Android starts up
        startMainActivity(context);
    }

    private void startMainActivity(Context context){
        Intent i = new Intent();
        i.setClassName("kanemars.chuffjava", "kanemars.chuffjava.MainActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
