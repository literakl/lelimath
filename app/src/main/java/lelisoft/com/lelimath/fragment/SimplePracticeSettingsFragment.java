package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;

/**
 * Easy to set preferences
 * Created by Leoš on 17.08.2016.
 */
public class SimplePracticeSettingsFragment extends PreferenceFragment implements
                                                    SharedPreferences.OnSharedPreferenceChangeListener {
    private static final Logger log = LoggerFactory.getLogger(SimplePracticeSettingsFragment.class);

    public static final String PREF_SIMPLE_PRACTICE_SETTINGS = "pref_simple_practice_settings";

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
        PreferenceScreen preferencesRoot = getPreferenceScreen();
        if (PREF_SIMPLE_PRACTICE_SETTINGS.equals(key)) {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented the callback interface
        try {
            callback = (SimpleSettingsBridge) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SimpleSettingsBridge");
        }
    }
}
