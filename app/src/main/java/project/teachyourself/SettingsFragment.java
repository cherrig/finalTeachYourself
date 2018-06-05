package project.teachyourself;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import project.teachyourself.model.Sound;

/**
 * SettingsFragment contains sound settings
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private CheckBoxPreference p1;
    private CheckBoxPreference p2;
    private CheckBoxPreference p3;
    private SwitchPreference sw;

    private Sound sound;

    /**
     * Callback method called when the activity is created.
     * @param savedInstanceState saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        p1 = (CheckBoxPreference)findPreference("check_box_preference_1");
        p2 = (CheckBoxPreference)findPreference("check_box_preference_2");
        p3 = (CheckBoxPreference)findPreference("check_box_preference_3");
        sw = (SwitchPreference)findPreference("switch_preference_1");

        p1.setOnPreferenceChangeListener(this);
        p2.setOnPreferenceChangeListener(this);
        p3.setOnPreferenceChangeListener(this);
        sw.setOnPreferenceChangeListener(this);

        sound = Sound.getInstance(getResources(), true, 1);
    }

    /**
     * Callback method called when a preference is changed
     * @param preference the preference changed
     * @param newValue the new value
     * @return boolean
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();

        if (key.equals("check_box_preference_1")) {
            p2.setChecked(false);
            p3.setChecked(false);
            sound.setSelected(1);
        }
        if (key.equals("check_box_preference_2")) {
            p1.setChecked(false);
            p3.setChecked(false);
            sound.setSelected(2);
        }
        if (key.equals("check_box_preference_3")) {
            p1.setChecked(false);
            p2.setChecked(false);
            sound.setSelected(3);
        }
        if (key.equals("switch_preference_1")) {
            boolean check = ((SwitchPreference) preference).isChecked();
            if (check)
                sound.stop();
            else
                sound.start();
            return true;
        }

        //Force the current focused checkbox to always stay checked when pressed
        //i.e confirms value when newValue is checked (true) and discards newValue
        //when newValue is unchecked (false)
        return (Boolean)newValue;
    }
}