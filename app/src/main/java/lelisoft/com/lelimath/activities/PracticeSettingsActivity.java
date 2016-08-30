package lelisoft.com.lelimath.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.PracticeAdvancedSettingsFragment;
import lelisoft.com.lelimath.fragment.PracticeSimpleSettingsFragment;

/**
 * Display practice preferences
 * Created by Leoš on 17.08.2016.
 */
public class PracticeSettingsActivity extends LeliBaseActivity implements PracticeSimpleSettingsFragment.SimpleSettingsBridge {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LeliBaseActivity.class);

    PracticeSimpleSettingsFragment simpleSettings;
    PracticeAdvancedSettingsFragment advancedSettings;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.act_practice_settings);
        simpleSettings = new PracticeSimpleSettingsFragment();
        advancedSettings = new PracticeAdvancedSettingsFragment();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean easy = sharedPref.getBoolean(PracticeSimpleSettingsFragment.KEY_SIMPLE_PRACTICE_SETTINGS, true);
        displayFragments(false, easy);
    }

    @Override
    public void settingsChanged(boolean simpleMode) {
        displayFragments(true, simpleMode);
    }

    private void displayFragments(boolean replace, boolean simple) {
        Fragment fragment = simple ? simpleSettings : advancedSettings;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (replace) {
            log.debug("Replacing current fragment with " + fragment.getClass().getSimpleName());
            transaction.replace(R.id.pref_fragment, fragment);
        } else {
            log.debug("Adding new fragment " + fragment.getClass().getSimpleName());
            transaction.add(R.id.pref_fragment, fragment);
        }
        transaction.commit();
    }

    public static void start(Context c) {
        Intent intent = new Intent(c, PracticeSettingsActivity.class);
        c.startActivity(intent);
    }
}
