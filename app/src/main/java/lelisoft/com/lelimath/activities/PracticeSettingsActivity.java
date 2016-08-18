package lelisoft.com.lelimath.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.AdvancedPracticeSettingsFragment;
import lelisoft.com.lelimath.fragment.SimplePracticeSettingsFragment;

/**
 * Display practice preferences
 * Created by Leoš on 17.08.2016.
 */
public class PracticeSettingsActivity extends LeliBaseActivity implements SimplePracticeSettingsFragment.SimpleSettingsBridge {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LeliBaseActivity.class);

    SimplePracticeSettingsFragment simpleSettings;
    AdvancedPracticeSettingsFragment advancedSettings;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_practice_settings);
        simpleSettings = new SimplePracticeSettingsFragment();
        advancedSettings = new AdvancedPracticeSettingsFragment();
        displayFragments(false, true);
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
