package project.teachyourself;


import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.matcher.BundleMatchers;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.Gravity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import project.teachyourself.model.Client;
import project.teachyourself.model.User;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CategoryActivityTest {

    @Rule
    public IntentsTestRule<CategoryActivity> mActivityTestRule = new IntentsTestRule<>(CategoryActivity.class, false, false);

    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start(8080);
        Client.BASE_URL = server.url("/test/").toString();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void categoryActivityTest() {
        User user = new User();
        user.setEmail("test@email");
        user.setName("Tester");
        user.setAge(42);
        user.setImage("R.drawable.ic_account");
        user.setScore(9);

        server.enqueue(new MockResponse()
                .setBody(Utils.toJson(user)));

        // Monitor activity launches and block them
        Instrumentation.ActivityMonitor profileMonitor =
                new Instrumentation.ActivityMonitor(ProfileActivity.class.getName(), null, true);
        Instrumentation.ActivityMonitor statisticsMonitor =
                new Instrumentation.ActivityMonitor(StatisticsActivity.class.getName(), null, true);
        Instrumentation.ActivityMonitor aboutMonitor =
                new Instrumentation.ActivityMonitor(AboutActivity.class.getName(), null, true);
        Instrumentation.ActivityMonitor quizMonitor =
                new Instrumentation.ActivityMonitor(QuizActivity.class.getName(), null, true);
        Instrumentation.ActivityMonitor settingsMonitor =
                new Instrumentation.ActivityMonitor(SettingsActivity.class.getName(), null, true);
        getInstrumentation().addMonitor(profileMonitor);
        getInstrumentation().addMonitor(statisticsMonitor);
        getInstrumentation().addMonitor(aboutMonitor);
        getInstrumentation().addMonitor(quizMonitor);
        getInstrumentation().addMonitor(settingsMonitor);

        Intent intent = new Intent();
        intent.putExtra(LoginActivity.EXTRA_USER_EMAIL, user.getEmail());
        mActivityTestRule.launchActivity(intent);

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Open left navigation drawer
        ViewInteraction drawer = onView(withId(R.id.drawer_layout));
        drawer.check(matches(isClosed(Gravity.LEFT)));
        drawer.perform(DrawerActions.open());
        drawer.check(matches(isOpen()));

        // Check user informations
        onView(withId(R.id.drawerUserName)).check(matches(withText(user.getName())));
        onView(withId(R.id.drawerUserEmail)).check(matches(withText(user.getEmail())));
        onView(withId(R.id.drawerUserScore)).check(matches(withText(String.valueOf(user.getScore()) + "%")));

        // Retrieve navigation
        ViewInteraction navigation = onView(withId(R.id.nav_view));

        // Check profile
        navigation.perform(NavigationViewActions.navigateTo(R.id.nav_profile));
        SystemClock.sleep(500); // Slow down test if animations are not disabled in dev options
        intended(allOf(
                IntentMatchers.isInternal(),
                IntentMatchers.hasComponent(ProfileActivity.class.getName()),
                IntentMatchers.hasExtras(allOf(
                        BundleMatchers.hasEntry(LoginActivity.EXTRA_USER_EMAIL, user.getEmail())
                ))
        ));
        assertTrue(getInstrumentation().checkMonitorHit(profileMonitor, 1));
        getInstrumentation().removeMonitor(profileMonitor);
        drawer.check(matches(isClosed(Gravity.LEFT)));

        drawer.perform(DrawerActions.open());

        // Check statistics
        navigation.perform(NavigationViewActions.navigateTo(R.id.nav_statistics));
        SystemClock.sleep(500); // Slow down test if animations are not disabled in dev options
        intended(allOf(
                IntentMatchers.isInternal(),
                IntentMatchers.hasComponent(StatisticsActivity.class.getName()),
                IntentMatchers.hasExtras(allOf(
                        BundleMatchers.hasEntry(LoginActivity.EXTRA_USER_EMAIL, user.getEmail())
                ))
        ));
        assertTrue(getInstrumentation().checkMonitorHit(statisticsMonitor, 1));
        getInstrumentation().removeMonitor(statisticsMonitor);
        drawer.check(matches(isClosed(Gravity.LEFT)));

        drawer.perform(DrawerActions.open());

        // Check about
        navigation.perform(NavigationViewActions.navigateTo(R.id.nav_about));
        SystemClock.sleep(500); // Slow down test if animations are not disabled in dev options
        intended(allOf(
                IntentMatchers.isInternal(),
                IntentMatchers.hasComponent(AboutActivity.class.getName())
        ));
        assertTrue(getInstrumentation().checkMonitorHit(aboutMonitor, 1));
        getInstrumentation().removeMonitor(aboutMonitor);
        drawer.check(matches(isClosed(Gravity.LEFT)));

        // Open menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Check settings action
        ViewInteraction settings = onView(withText(R.string.action_settings));
        settings.check(matches(isDisplayed()));
        settings.perform(click());
        SystemClock.sleep(500); // Slow down test if animations are not disabled in dev options
        intended(allOf(
                IntentMatchers.isInternal(),
                IntentMatchers.hasComponent(SettingsActivity.class.getName())
        ));
        assertTrue(getInstrumentation().checkMonitorHit(settingsMonitor, 1));
        getInstrumentation().removeMonitor(settingsMonitor);


        // Check list of categories
        onView(Utils.withRecyclerView(R.id.recyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(R.string.category1))));
        onView(Utils.withRecyclerView(R.id.recyclerView).atPosition(1))
                .check(matches(hasDescendant(withText(R.string.category2))));
        onView(Utils.withRecyclerView(R.id.recyclerView).atPosition(2))
                .check(matches(hasDescendant(withText(R.string.category3))));
        onView(Utils.withRecyclerView(R.id.recyclerView).atPosition(3))
                .check(matches(hasDescendant(withText(R.string.category4))));
        onView(Utils.withRecyclerView(R.id.recyclerView).atPosition(4))
                .check(matches(hasDescendant(withText(R.string.category5))));
        onView(Utils.withRecyclerView(R.id.recyclerView).atPosition(5))
                .check(matches(hasDescendant(withText(R.string.category6))));
        onView(Utils.withRecyclerView(R.id.recyclerView).atPosition(6))
                .check(matches(hasDescendant(withText(R.string.category7))));


        // Click item 1
        onView(Utils.withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click());

        // Check level dialog
        onView(withId(R.id.textTitle))
                .inRoot(isDialog())
                .check(matches(withText(R.string.category1)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.textDescription))
                .inRoot(isDialog())
                .check(matches(withText(R.string.category1_description)))
                .check(matches(isDisplayed()));

        DataInteraction levels = onData(instanceOf(String.class))
                .inAdapterView(withId(R.id.levelList))
                .inRoot(isDialog());

        levels.atPosition(0).check(matches(withText(R.string.level1)));
        levels.atPosition(1).check(matches(withText(R.string.level2)));
        levels.atPosition(2).check(matches(withText(R.string.level3)));

        // Check quiz start
        levels.atPosition(0).perform(click());
        SystemClock.sleep(500); // Slow down test if animations are not disabled in dev options
        intended(allOf(
                IntentMatchers.isInternal(),
                IntentMatchers.hasComponent(QuizActivity.class.getName()),
                IntentMatchers.hasExtras(allOf(
                        BundleMatchers.hasEntry(LoginActivity.EXTRA_USER_EMAIL, user.getEmail()),
                        BundleMatchers.hasEntry(QuizActivity.EXTRA_QUIZ_CATEGORY,
                                mActivityTestRule.getActivity().getString(R.string.category1)),
                        BundleMatchers.hasEntry(QuizActivity.EXTRA_QUIZ_LEVEL, 1)
                ))
        ));
        assertTrue(getInstrumentation().checkMonitorHit(quizMonitor, 1));
        getInstrumentation().removeMonitor(quizMonitor);
    }
}
