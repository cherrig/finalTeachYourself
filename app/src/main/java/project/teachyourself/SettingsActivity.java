package project.teachyourself;

import android.os.Bundle;

/**
 * Settings activity allow user to change the app settings such as sound
 */
public class SettingsActivity extends BaseActivity {

    /**
     * Callback method called when the activity is created.
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}


