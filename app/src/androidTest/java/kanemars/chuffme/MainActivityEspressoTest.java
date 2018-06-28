package kanemars.chuffme;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {

    /**
    *  A JUnit Rule to launch the activity under test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

    @Test
    public void ensurePressingCheckTimesButtonShowsResponse() {
        onView(withId(R.id.trainTimesTextView)).check(matches(withText("")));
        onView(withId(R.id.checkTimesButton)).perform(click());
        onView(withId(R.id.trainTimesTextView)).check(matches(not(withText(""))));
    }

}
