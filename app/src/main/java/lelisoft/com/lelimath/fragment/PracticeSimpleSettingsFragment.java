package lelisoft.com.lelimath.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;

/**
 * Easy to set preferences
 * Created by Leoš on 17.08.2016.
 */
public class PracticeSimpleSettingsFragment extends PreferenceFragment implements
                                                    SharedPreferences.OnSharedPreferenceChangeListener {
    private static final Logger log = LoggerFactory.getLogger(PracticeSimpleSettingsFragment.class);

    public static final String KEY_SIMPLE_PRACTICE_SETTINGS = "pref_simple_practice_settings";
    public static final String KEY_FIRST_ARG = "pref_practice_simple_first_arg";
    public static final String KEY_SECOND_ARG = "pref_practice_simple_second_arg";
    public static final String KEY_RESULT = "pref_practice_simple_result";
    public static final String KEY_OPERATIONS = "pref_practice_operations";

    SimpleSettingsBridge callback;

    public interface SimpleSettingsBridge {
        void settingsChanged(boolean simpleMode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.simple_practice_prefs);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        log.debug("onSharedPreferenceChanged(" + key + ")");
        //noinspection deprecation
        if (KEY_SIMPLE_PRACTICE_SETTINGS.equals(key)) {
            callback.settingsChanged(false);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        //noinspection deprecation
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        //noinspection deprecation
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented the callback interface
        try {
            callback = (SimpleSettingsBridge) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SimpleSettingsBridge");
        }
    }
}
