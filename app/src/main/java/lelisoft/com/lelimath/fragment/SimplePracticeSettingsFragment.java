package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import lelisoft.com.lelimath.R;

/**
 * Easy to set preferences
 * Created by Leoš on 17.08.2016.
 */
public class SimplePracticeSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.simple_practice_prefs);
    }
}
