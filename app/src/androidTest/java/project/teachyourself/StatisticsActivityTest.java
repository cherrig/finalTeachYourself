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

import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import project.teachyourself.model.Category;
import project.teachyourself.model.Client;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StatisticsActivityTest {

    @Rule
    public IntentsTestRule<StatisticsActivity> mActivityTestRule = new IntentsTestRule<>(StatisticsActivity.class, false, false);

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

    private String getPercentage(Category category) {
        if (category.getQuestions() > 0)
            return String.format(mActivityTestRule.getActivity().getString(R.string.score_percent),
                    category.getScore() * 100 / category.getQuestions());
        else
            return mActivityTestRule.getActivity().getString(R.string.no_score);
    }

    @Test
    public void statisticsActivityTest() throws Exception {
        List<Category> listUser = new ArrayList<>();

        listUser.add(new Category("Calcul mental", 10, 5, 1.2f));
        listUser.add(new Category("Mot du jour", 10, 2, 5f));
        listUser.add(new Category("Association d'image", 10, 5, 9.9f));
        listUser.add(new Category("Phrase dans l'ordre", 10, 9, 3.5f));
        listUser.add(new Category("Phonétique", 10, 10, 4f));
        listUser.add(new Category("Grammaire", 10, 3, 4.4f));
        listUser.add(new Category("Conjugaison", 10, 6, 8f));

        List<Category> listGlobal = new ArrayList<>();

        listGlobal.add(new Category("Calcul mental", 100, 45, 1.2f));
        listGlobal.add(new Category("Mot du jour", 100, 90, 6f));
        listGlobal.add(new Category("Association d'image", 10, 5, 9.9f));
        listGlobal.add(new Category("Phrase dans l'ordre", 10, 5, 3.5f));
        listGlobal.add(new Category("Phonétique", 100, 69, 4f));
        listGlobal.add(new Category("Grammaire", 100, 96, 4.4f));
        listGlobal.add(new Category("Conjugaison", 100, 20, 8f));

        server.enqueue(new MockResponse()
                .setBody(Utils.toJson(listUser)));
        server.enqueue(new MockResponse()
                .setBody(Utils.toJson(listGlobal)));
        server.enqueue(new MockResponse()
                .setBody(Utils.toJson(listUser)));


        String email = "test@email";
        Intent intent = new Intent();
        intent.putExtra(LoginActivity.EXTRA_USER_EMAIL, email);
        mActivityTestRule.launchActivity(intent);

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check Request
        RecordedRequest request1 = server.takeRequest();
        assertEquals("GET", request1.getMethod());
        assertEquals("/stat?email=" + email, request1.getPath());

        ViewInteraction navigationPersonal  = onView(withId(R.id.statistics_navigation_personal));
        ViewInteraction navigationGlobal = onView(withId(R.id.statistics_navigation_global));

        // Check list of categories
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(listUser.get(0).getTitle()))))
                .check(matches(hasDescendant(withText(getPercentage(listUser.get(0))))));
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(1))
                .check(matches(hasDescendant(withText(listUser.get(1).getTitle()))))
                .check(matches(hasDescendant(withText(getPercentage(listUser.get(1))))));
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(2))
                .check(matches(hasDescendant(withText(listUser.get(2).getTitle()))))
                .check(matches(hasDescendant(withText(getPercentage(listUser.get(2))))));
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(3))
                .check(matches(hasDescendant(withText(listUser.get(3).getTitle()))))
                .check(matches(hasDescendant(withText(getPercentage(listUser.get(3))))));
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(4))
                .check(matches(hasDescendant(withText(listUser.get(4).getTitle()))))
                .check(matches(hasDescendant(withText(getPercentage(listUser.get(4))))));
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(5))
                .check(matches(hasDescendant(withText(listUser.get(5).getTitle()))))
                .check(matches(hasDescendant(withText(getPercentage(listUser.get(5))))));
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(6))
                .check(matches(hasDescendant(withText(listUser.get(6).getTitle()))))
                .check(matches(hasDescendant(withText(getPercentage(listUser.get(6))))));

        // Click first item
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(0))
                .perform(click());

        // Check expanded item
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(0))
                .check(matches(hasDescendant(allOf(
                        withId(R.id.itemChartScoreLabel),
                        withText(R.string.score_label),
                        isDisplayed()))))
                .check(matches(hasDescendant(allOf(
                        withId(R.id.itemChartTimeLabel),
                        withText(R.string.time_average_label),
                        isDisplayed()))))
                .check(matches(hasDescendant(allOf(
                        withId(R.id.itemChartScore), isDisplayed()))))
                .check(matches(hasDescendant(allOf(
                        withId(R.id.itemChartTime), isDisplayed()))));


        // Click on second item
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(1))
                .perform(click());

        // Check last item not expanded
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(0))
                .check(matches(hasDescendant(allOf(
                        withId(R.id.itemExpandable),
                        not(isDisplayed())
                ))));

        // Check current item expanded
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(1))
                .check(matches(hasDescendant(allOf(
                        withId(R.id.itemExpandable),
                        isDisplayed()
                ))));

        // Global statistics
        navigationGlobal.perform(click());
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(getPercentage(listGlobal.get(0))))));

        // Check Request
        RecordedRequest request2 = server.takeRequest();
        assertEquals("GET", request2.getMethod());
        assertEquals("/stat", request2.getPath());

        // Personal statistics
        navigationPersonal.perform(click());
        onView(Utils.withRecyclerView(R.id.statisticRecyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(getPercentage(listUser.get(0))))));

        // Check Request
        RecordedRequest request3 = server.takeRequest();
        assertEquals("GET", request3.getMethod());
        assertEquals("/stat?email=" + email, request3.getPath());
    }
}
