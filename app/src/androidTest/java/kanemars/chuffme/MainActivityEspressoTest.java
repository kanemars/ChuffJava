package kanemars.chuffme;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {

    /**
     * A JUnit Rule to launch the activity under test.
     */
    @Rule public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);
    //@Rule public GrantPermissionRule wifiStateRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);
    //@Rule public GrantPermissionRule wifiChangeRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);

    @Test
    public void ensurePressingCheckTimesButtonShowsResponse() {
        onView(withId(R.id.trainTimesTextView)).check(matches(withText("")));
        onView(withId(R.id.checkTimesButton)).perform(click());
        onView(withId(R.id.trainTimesTextView)).check(matches(not(withText(""))));
    }

    @Test
    public void ensurePressingCheckTimesButtonWithNoInternetShowsErrorMessageResponse()  {
        onView(withId(R.id.trainTimesTextView)).check(matches(withText("")));
        onView(withId(R.id.checkTimesButton)).perform(click());
        Context context = mActivityRule.getActivity().getApplicationContext();
        if (isConnected (context)) {
            onView(withId(R.id.trainTimesTextView)).check(matches(withText(containsString("; "))));
        } else {
            onView(withId(R.id.trainTimesTextView)).check(matches(withText("Problem connecting to internet, try turning off WIFI")));
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void turnOffMobileData() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ConnectivityManager dataManager=(ConnectivityManager)mActivityRule.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        Method dataClass = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
        dataClass.setAccessible(true);
        dataClass.invoke(dataManager, true);
    }

    private void turnOffWifi() {
        WifiManager wifi = (WifiManager) mActivityRule.getActivity().getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifi.isWifiEnabled();
        wifi.setWifiEnabled(false);
    }
}