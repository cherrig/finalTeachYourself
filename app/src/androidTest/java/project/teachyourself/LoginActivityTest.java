package project.teachyourself;


import android.app.Instrumentation;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.matcher.BundleMatchers;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.InputType;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import project.teachyourself.model.Client;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<LoginActivity> mActivityTestRule = new IntentsTestRule<>(LoginActivity.class, false, false);

    private UiDevice mDevice;
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        assertThat(mDevice, notNullValue());

        server = new MockWebServer();
        server.start(8080);
        Client.BASE_URL = server.url("/test/").toString();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    private void denyPermissionsIfNeeded()  {
        if (Build.VERSION.SDK_INT >= 23) {
            UiObject denyPermissions = mDevice.findObject(new UiSelector().text("REFUSER"));
            if (denyPermissions.exists()) {
                try {
                    denyPermissions.click();
                } catch (UiObjectNotFoundException e) {
                    Log.e("UI TEST ERROR", "There is no permissions dialog to interact with ");
                }
            }
        }
    }

    @Test
    public void loginActivityTest() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("\"test@email\""));

        // Monitor activity launches and block them
        Instrumentation.ActivityMonitor categoryMonitor =
                new Instrumentation.ActivityMonitor(CategoryActivity.class.getName(), null, true);
        Instrumentation.ActivityMonitor registerMonitor =
                new Instrumentation.ActivityMonitor(RegisterActivity.class.getName(), null, true);
        InstrumentationRegistry.getInstrumentation().addMonitor(categoryMonitor);
        InstrumentationRegistry.getInstrumentation().addMonitor(registerMonitor);

        mActivityTestRule.launchActivity(null);

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Deny permissions if there is a dialog
        denyPermissionsIfNeeded();

        // Retrieve views
        ViewInteraction editEmail = onView(withId(R.id.email));
        ViewInteraction editPassword = onView(withId(R.id.password));
        ViewInteraction buttonShow = onView(withId(R.id.show));
        ViewInteraction buttonSignIn = onView(withId(R.id.buttonSignIn));
        ViewInteraction buttonRegister = onView(withId(R.id.buttonRegister));

        // Check views are displayed
        editEmail.check(matches(isDisplayed()));
        editPassword.check(matches(isDisplayed()));
        buttonShow.check(matches(isDisplayed()));
        buttonSignIn.check(matches(isDisplayed()));
        buttonRegister.check(matches(isDisplayed()));

        // Test Register button click
        buttonRegister.perform(scrollTo(), click());
        intended(allOf(
                IntentMatchers.isInternal(),
                IntentMatchers.hasComponent(RegisterActivity.class.getName())
        ));
        assertTrue(InstrumentationRegistry.getInstrumentation().checkMonitorHit(registerMonitor, 1));
        InstrumentationRegistry.getInstrumentation().removeMonitor(registerMonitor);

        // Check empty form error
        buttonSignIn.perform(scrollTo(), click());
        editEmail.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_field_required))));
        editPassword.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_field_required))));
        buttonShow.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Fill forms
        editEmail.perform(scrollTo(), replaceText("test@email"), closeSoftKeyboard());
        editPassword.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());
        buttonShow.check(matches(isDisplayed()));

        // Check password Show/Hide
        editPassword.check(matches((Utils.InputTypeMatcher.hasInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD))));
        buttonShow.perform(scrollTo(), click());
        editPassword.check(matches((Utils.InputTypeMatcher.hasInputType(InputType.TYPE_CLASS_TEXT))));
        buttonShow.perform(scrollTo(), click());
        editPassword.check(matches((Utils.InputTypeMatcher.hasInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD))));

        // Test Sign In
        buttonSignIn.perform(scrollTo(), click());
        // TODO: check request maybe
        intended(allOf(
                IntentMatchers.isInternal(),
                IntentMatchers.hasComponent(CategoryActivity.class.getName()),
                IntentMatchers.hasExtras(allOf(
                        BundleMatchers.hasEntry(LoginActivity.EXTRA_USER_EMAIL, "test@email")
                ))
        ));
        assertTrue(InstrumentationRegistry.getInstrumentation().checkMonitorHit(categoryMonitor, 1));
        InstrumentationRegistry.getInstrumentation().removeMonitor(categoryMonitor);
    }
}
