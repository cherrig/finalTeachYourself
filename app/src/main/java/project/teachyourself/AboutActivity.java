package project.teachyourself;

import android.os.Bundle;

/**
 * About Activity display informations about the app
 */
public class AboutActivity extends BaseActivity {

    /**
     * Callback method called when the activity is created.
     * Display informations.
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupActionBar();
    }
}
