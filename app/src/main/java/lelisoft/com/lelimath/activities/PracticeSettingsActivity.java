package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.AdvancedPracticeSettingsFragment;
import lelisoft.com.lelimath.fragment.SimplePracticeSettingsFragment;

/**
 * Display practice preferences
 * Created by Leoš on 17.08.2016.
 */
public class PracticeSettingsActivity extends LeliBaseActivity {
    SimplePracticeSettingsFragment simpleSettings;
    AdvancedPracticeSettingsFragment advancedSettings;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_practice_settings);
        simpleSettings = new SimplePracticeSettingsFragment();
        advancedSettings = new AdvancedPracticeSettingsFragment();

        initializeCalcFragment(false);
    }

    private void initializeCalcFragment(boolean replace) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (replace) {
            transaction.replace(R.id.pref_fragment, simpleSettings);
        } else {
            transaction.add(R.id.pref_fragment, simpleSettings);
        }
        transaction.commit();
    }

    public static void start(Context c) {
        Intent intent = new Intent(c, PracticeSettingsActivity.class);
        c.startActivity(intent);
    }
}
