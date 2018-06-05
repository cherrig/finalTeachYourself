package project.teachyourself;


import android.content.Intent;
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScoreActivityTest {

    @Rule
    public IntentsTestRule<ScoreActivity> mActivityTestRule = new IntentsTestRule<>(ScoreActivity.class, false, false);

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
    public void scoreActivityTest() throws Exception {
        String email = "test@email";
        String category = "Calcul mental";
        int score = 7;
        int questionNumber = 20;
        float avgResponseTime = 4.4f;

        server.enqueue(new MockResponse()
                .setBody("\"Saved\""));

        Intent intent = new Intent();
        intent.putExtra(LoginActivity.EXTRA_USER_EMAIL, email);
        intent.putExtra(QuizActivity.EXTRA_QUIZ_CATEGORY, category);
        intent.putExtra(QuizActivity.EXTRA_QUIZ_SCORE, score);
        intent.putExtra(QuizActivity.EXTRA_QUIZ_NUMBER, questionNumber);
        intent.putExtra(QuizActivity.EXTRA_QUIZ_AVG_TIME, avgResponseTime);
        mActivityTestRule.launchActivity(intent);

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check all view
        onView(withId(R.id.textScoreLabel))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.score_label)));

        onView(withId(R.id.chartScore))
                .check(matches(isDisplayed()));

        onView(withId(R.id.textTimeAverageLabel))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.time_average_label)));

        onView(withId(R.id.chartTimeAverage))
                .check(matches(isDisplayed()));

        onView(withId(R.id.buttonLeaveScore))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.button_leave_score)))
                .perform(click());

        // TODO: check request maybe
    }
}
