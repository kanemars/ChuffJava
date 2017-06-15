package kanemars.chuffjava;

import android.content.Intent;
import static android.app.AlarmManager.*;

final class Constants {
    private Constants() {
        // restrict instantiation
    }

    static final String KEY_SOURCE = "source";
    static final String KEY_DESTINATION = "destination";
    static final String KEY_NOTIFICATION_TIME = "notification_time";
    static final String KEY_NOTIFICATION_ON = "notification_preference";
    static final String KEY_JOURNEY = "journey";

    //private static final long MINUTE =  60 * 1000;
    static final long CHUFF_ALARM_INTERVAL = INTERVAL_DAY;// 20 seconds //;
    static final int NOTIFICATION_INTENT_FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP;

    // Using the same notificationId will ensure that Chuff Me will only have at most one notification listed
    // Existing Chuff Me notifications will be replaced with the latest one
    // The times of existing trains will become redundant after a while anyway
    static final int CHUFF_ME_NOTIFICATION_ID = 1;
}
