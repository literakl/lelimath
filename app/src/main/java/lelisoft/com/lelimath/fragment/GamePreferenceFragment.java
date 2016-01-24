package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import lelisoft.com.lelimath.R;

/**
 * Preference for a game
 * Created by Leoš on 17.01.2016.
 */
public class GamePreferenceFragment extends PreferenceFragment {
    private static final String logTag = GamePreferenceFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(logTag, "onCreate()");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.game_prefs);
    }
/*
cz.wincor.aevi.smartpos.helpers.PreferenceScreenHelper
http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su
http://stackoverflow.com/questions/3827356/setting-ui-preference-summary-field-to-the-value-of-the-preference

implements SharedPreferences.OnSharedPreferenceChangeListener
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (key.equals("list_0")) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
*/
}
