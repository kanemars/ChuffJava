package kanemars.chuffme;

import android.media.AudioAttributes;

import static android.app.AlarmManager.*;

final class Constants {
    private Constants() {
        // restrict instantiation
    }

    static final String KEY_SOURCE = "source";
    static final String KEY_DESTINATION = "destination";
    static final String KEY_NOTIFICATION_TIME = "notification_time";
    static final String KEY_DAYS_OF_WEEK = "DaysOfWeek";
    static final String KEY_NOTIFICATION_ON = "notification_preference";
    static final String CHANNEL_ID = "chuff_me_channel_2";

    static final long CHUFF_ALARM_INTERVAL = INTERVAL_DAY;

    // Using the same notificationId will ensure that Chuff Me will only have at most one notification listed
    // Existing Chuff Me notifications will be replaced with the latest one
    // The times of existing trains will become redundant after a while anyway
    static final int CHUFF_ME_NOTIFICATION_ID = 1;

    static final long [] VIBRATOR = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};

    static final AudioAttributes AUDIO_ATTRIBUTES = new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .build();
}
