package project.teachyourself;


import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import project.teachyourself.model.Client;
import project.teachyourself.model.User;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    @Rule
    public IntentsTestRule<ProfileActivity> mActivityTestRule = new IntentsTestRule<>(ProfileActivity.class, false, false);

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
    public void profileActivityTest() {
        User user = new User();
        user.setEmail("test@email");
        user.setName("Tester");
        user.setAge(42);
        user.setImage("R.drawable.ic_account");
        user.setScore(9);

        server.enqueue(new MockResponse()
                .setBody(Utils.toJson(user)));

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

        // Retrieve views
        ViewInteraction profilePicture = onView(withId(R.id.imageViewProfile));
        ViewInteraction profileEmail = onView(withId(R.id.ProfileEmail));
        ViewInteraction profileName = onView(withId(R.id.ProfileName));
        ViewInteraction profileAge = onView(withId(R.id.ProfileAge));

        // Check views are displayed
        profilePicture.check(matches(isDisplayed()));
        profileEmail.check(matches(isDisplayed()))
                .check(matches(withText(user.getEmail())));
        profileName.check(matches(isDisplayed()))
                .check(matches(withText(user.getName())));
        profileAge.check(matches(isDisplayed()))
                .check(matches(withText(String.valueOf(user.getAge()))));
    }
}
