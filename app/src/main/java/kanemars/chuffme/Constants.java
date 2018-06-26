package kanemars.chuffme;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import kanemars.KaneHuxleyJavaConsumer.Models.Journey;

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

    // Same one should be used so that correct AlarmManager should be used
    // https://stackoverflow.com/questions/28922521/how-to-cancel-alarm-from-alarmmanager
    static final int PENDING_INTENT_REQUEST_CODE = 1;

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

    static Journey getJourney(SharedPreferences chuffPreferences, Context context) {
        String strSource = chuffPreferences.getString(KEY_SOURCE, context.getString(R.string.default_source_station));
        String strDestination = chuffPreferences.getString(KEY_DESTINATION, context.getString(R.string.default_destination_station));

        return new Journey(strSource, strDestination);
    }
}
