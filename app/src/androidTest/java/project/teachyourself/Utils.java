package project.teachyourself;


import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.Type;
import java.util.List;

import project.teachyourself.model.Category;
import project.teachyourself.model.User;

/**
 * Utility class containing methods used in tests
 */
public class Utils {

    /**
     * Convert a user into a json string
     * @param user user
     * @return json
     */
    public static String toJson(User user) {
        Gson gson = new Gson();

        Type type = new TypeToken<User>() {}.getType();
        return gson.toJson(user, type);
    }

    /**
     * Convert a list of category into a json string
     * @param categories list of category
     * @return json
     */
    public static String toJson(List<Category> categories) {
        Gson gson = new Gson();

        Type type = new TypeToken<List<Category>>() {}.getType();
        return gson.toJson(categories, type);
    }

    /**
     * Matcher on InputType
     */
    public static class InputTypeMatcher {

        public static Matcher<View> hasInputType(final int inputType) {
            return new BoundedMatcher<View, TextView>(TextView.class) {
                int targetViewInputType = 0;
                @Override
                public void describeTo(Description description) {
                    description.appendText("hasInputType: ").appendValue(Integer.toHexString(inputType));
                    description.appendText(" actualInputType: ").appendValue(Integer.toHexString(targetViewInputType));
                    description.appendText(" mask: ").appendValue(Integer.toHexString(inputType & targetViewInputType));
                }
                @Override
                protected boolean matchesSafely(TextView tv) {
                    if (targetViewInputType == 0) {
                        targetViewInputType = tv.getInputType();
                    }
                    return (targetViewInputType & inputType) == inputType;
                }
            };
        }
    }

    /**
     * Convenience helper to test recyclerView
     * @param recyclerViewId reyclerview's id
     * @return the matcher
     */
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
}
