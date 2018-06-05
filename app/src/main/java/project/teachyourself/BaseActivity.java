package project.teachyourself;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import project.teachyourself.model.Sound;

/**
 * Base Activity inherited from every Activities.
 * Retrieves application visibility state.
 * Handles sound.
 * Handles back button (home) in click (in app bar)
 */
public class BaseActivity extends AppCompatActivity {
    protected static int visible = 0;
    protected static boolean orientationChange = false;
    protected static Sound sound = null;
    protected ProgressDialog progress = null;
    public static AtomicBoolean isRunningTest;

    /**
     * Callback called when activity is visible on screen.
     * Calculate a visibility count and start the sound when application starts
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        visible++;

        if (orientationChange)
            orientationChange = false;
        else if (visible == 1 && sound == null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            boolean play = sharedPref.getBoolean("switch_preference_1", true);
            boolean m1 = sharedPref.getBoolean("check_box_preference_1", false);
            boolean m2 = sharedPref.getBoolean("check_box_preference_2", false);
            boolean m3 = sharedPref.getBoolean("check_box_preference_3", false);

            if (!isRunningTest())
                sound = Sound.getInstance(getResources(), play, m1 ? 1 : m2 ? 2 : m3 ? 3 : 1);

        } else if (visible == 1 && sound != null && !isRunningTest())
            sound.start();

        // Define the progress dialog to show during asynchronous http requests
        progress = new ProgressDialog(this);
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
    }

    /**
     * Callback called when activity is not visible.
     * Calculate a visibility count and stop sound when application is not visible
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        visible--;

        if (sound != null && visible <= 0 && !orientationChange)
            sound.stop();
    }

    /**
     * Detect screen rotation
     * @param configuration configuration
     */
    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        orientationChange = true;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Finish the activity when clicking the back button on the app bar
     * @param item item selected
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * Display a toast with the given message
     * @param message message to display
     */
    protected void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Check if the app run in espresso test mode
     * @return boolean
     */
    public static synchronized boolean isRunningTest () {
        if (null == isRunningTest) {
            boolean istest;

            try {
                Class.forName ("android.support.test.espresso.Espresso");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }

            isRunningTest = new AtomicBoolean (istest);
        }

        return isRunningTest.get();
    }
}
