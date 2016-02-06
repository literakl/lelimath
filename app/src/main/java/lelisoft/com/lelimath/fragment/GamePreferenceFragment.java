package lelisoft.com.lelimath.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.helpers.PreferenceHelper;

/**
 * Preference for a game
 * Created by Leoš on 17.01.2016.
 */
public class GamePreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String logTag = GamePreferenceFragment.class.getSimpleName();

    PreferenceHelper preferenceScreenHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(logTag, "onCreate()");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.game_prefs);

        preferenceScreenHelper = new PreferenceHelper(getPreferenceScreen());
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

    private void updatePreferenceSummary(Preference p) {
        preferenceScreenHelper.updatePreferenceSummary(p);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferenceSummary(findPreference(key));
    }
}
