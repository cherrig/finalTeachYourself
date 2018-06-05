package project.teachyourself;


import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
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

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import project.teachyourself.model.Client;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class, false, false);

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
    public void registerActivityTest() {
        server.enqueue(new MockResponse()
                .setBodyDelay(500, TimeUnit.MILLISECONDS)
                .setBody("\"test\""));

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
        ViewInteraction editName = onView(withId(R.id.name));
        ViewInteraction editAge = onView(withId(R.id.age));
        ViewInteraction editPassword = onView(withId(R.id.password));
        ViewInteraction buttonShow = onView(withId(R.id.showForm));
        ViewInteraction buttonRegister = onView(withId(R.id.buttonRegisterForm));

        // Check views are displayed
        editEmail.check(matches(isDisplayed()));
        editName.check(matches(isDisplayed()));
        editAge.check(matches(isDisplayed()));
        editPassword.check(matches(isDisplayed()));
        buttonShow.check(matches(isDisplayed()));
        buttonRegister.check(matches(isDisplayed()));

        // Check empty form error
        buttonRegister.perform(scrollTo(), click());
        editEmail.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_field_required))));
        editName.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_field_required))));
        editAge.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_field_required))));
        editPassword.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_field_required))));
        buttonShow.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Check invalid form error
        editEmail.perform(replaceText("test"), closeSoftKeyboard());
        editName.perform(scrollTo(), replaceText("Tester"), closeSoftKeyboard());
        editAge.perform(scrollTo(), replaceText("4242424242424242"), closeSoftKeyboard());
        editPassword.perform(scrollTo(), replaceText("a"), closeSoftKeyboard());

        buttonRegister.perform(scrollTo(), click());
        editEmail.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_invalid_email))));
        editAge.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_age_invalid))));
        editPassword.check(matches(hasErrorText(mActivityTestRule.getActivity().getResources().getString(R.string.error_invalid_password))));

        // Fill forms
        editEmail.perform(scrollTo(), replaceText("test@email"), closeSoftKeyboard());
        editAge.perform(scrollTo(), replaceText("42"), closeSoftKeyboard());
        editPassword.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());
        buttonShow.check(matches(isDisplayed()));

        // Check password Show/Hide
        editPassword.check(matches((Utils.InputTypeMatcher.hasInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD))));
        buttonShow.perform(scrollTo(), click());
        editPassword.check(matches((Utils.InputTypeMatcher.hasInputType(InputType.TYPE_CLASS_TEXT))));
        buttonShow.perform(scrollTo(), click());
        editPassword.check(matches((Utils.InputTypeMatcher.hasInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD))));

        // Test register
        buttonRegister.perform(scrollTo(), click());

        // TODO: check request maybe
    }
}
