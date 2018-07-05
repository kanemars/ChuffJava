package kanemars.chuffme;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static kanemars.chuffme.EspressoHelpers.waitId;
import static kanemars.chuffme.EspressoHelpers.waitText;

@RunWith(AndroidJUnit4.class)
public class ChuffPreferenceActivityTests {
    /**
     * A JUnit Rule to launch the activity under test.
     */
    @Rule public ActivityTestRule<ChuffPreferenceActivity> mActivityRule = new ActivityTestRule(ChuffPreferenceActivity.class);

    @Test
    public void ensureChoosingDaysShowsDaysSelected () {
        onView(withText("Show daily notifications")).check(matches(not(isChecked())));
        //onView(withId(R.id.notificationPreference)).check(matches(not(isChecked())));
        onView(withText("Show daily notifications")).perform(click());
        onView(withText("Days of week to be notified")).perform(click());
        //waitText("Sunday", TimeUnit.SECONDS.toMillis(15));
        onView(withText("Sunday")).check(matches(not(isChecked())));
        onView(withText("Monday")).check(matches(isChecked()));
        onView(withText("Tuesday")).check(matches(isChecked()));
        onView(withText("Wednesday")).check(matches(isChecked()));
        onView(withText("Thursday")).check(matches(isChecked()));
        onView(withText("Friday")).check(matches(isChecked()));
        onView(withText("Saturday")).check(matches(not(isChecked())));
        onView(withText("OK")).perform(click());
        onView(withText("Monday, Tuesday, Wednesday, Thursday, Friday")).check(matches(isDisplayed()));
    }
}
