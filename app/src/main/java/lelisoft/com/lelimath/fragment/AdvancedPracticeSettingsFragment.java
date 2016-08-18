package lelisoft.com.lelimath.fragment;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.helpers.DependencyMap;
import lelisoft.com.lelimath.helpers.PreferenceHelper;
import lelisoft.com.lelimath.helpers.PreferenceInputValidator;

/**
 * Tune everything preferences
 * Created by Leoš on 17.08.2016.
 */
public class AdvancedPracticeSettingsFragment extends PreferenceFragmentCompat implements
                                                      SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Logger log = LoggerFactory.getLogger(AdvancedPracticeSettingsFragment.class);

    private PreferenceHelper preferenceScreenHelper;
    private DependencyMap dependencyMap;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.advanced_practice_prefs);
        //noinspection deprecation
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        dependencyMap = new DependencyMap(preferenceScreen.getSharedPreferences());
        dependencyMap.updateDependencies(preferenceScreen);

        changeDefinitionsState("plus", preferenceScreen);
        changeDefinitionsState("minus", preferenceScreen);
        changeDefinitionsState("multiply", preferenceScreen);
        changeDefinitionsState("divide", preferenceScreen);

        String[] operations = new String[]{"plus", "minus", "multiply", "divide"};
        for (String operation : operations) {
            EditTextPreference preference = ((EditTextPreference) preferenceScreen.findPreference("pref_game_" + operation + "_first_arg"));
            new ValuesPreferenceValidator(preference);
            preference = ((EditTextPreference) preferenceScreen.findPreference("pref_game_" + operation + "_second_arg"));
            new ValuesPreferenceValidator(preference);
            preference = ((EditTextPreference) preferenceScreen.findPreference("pref_game_" + operation + "_result"));
            new ValuesPreferenceValidator(preference);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        log.debug("onSharedPreferenceChanged(" + key + ")");
        //noinspection deprecation
        PreferenceScreen preferencesRoot = getPreferenceScreen();
        PreferenceScreen preference;
        switch (key) {
            case "pref_game_plus_depends":
                changeDefinitionsState("plus", preferencesRoot);
                dependencyMap.updateDependencies(preferencesRoot);
                return;

            case "pref_game_minus_depends":
                changeDefinitionsState("minus", preferencesRoot);
                dependencyMap.updateDependencies(preferencesRoot);
                return;

            case "pref_game_multiply_depends":
                changeDefinitionsState("multiply", preferencesRoot);
                dependencyMap.updateDependencies(preferencesRoot);
                return;

            case "pref_game_divide_depends":
                changeDefinitionsState("divide", preferencesRoot);
                dependencyMap.updateDependencies(preferencesRoot);
                return;

            case "pref_game_operation_plus":
                preferenceScreenHelper.setScreenSummary("plus", preferencesRoot, sharedPreferences);
                return;

            case "pref_game_operation_minus":
                preferenceScreenHelper.setScreenSummary("minus", preferencesRoot, sharedPreferences);
                return;

            case "pref_game_operation_multiply":
                preferenceScreenHelper.setScreenSummary("multiply", preferencesRoot, sharedPreferences);
                return;

            case "pref_game_operation_divide":
                preferenceScreenHelper.setScreenSummary("divide", preferencesRoot, sharedPreferences);
                return;
        }

        //noinspection deprecation
        updatePreferenceSummary(findPreference(key));
    }

    /**
     * Enables of disables definition preferences for specified operation.
     * @param key operation key
     * @param preferenceScreen root preferences
     */
    private void changeDefinitionsState(String key, PreferenceScreen preferenceScreen) {
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        String value = sharedPreferences.getString("pref_game_" + key + "_depends", "NONE");
        dependencyMap.setReuse(key.toUpperCase(), value);
        log.debug(key + " depends is " + value);

        boolean enabled = "NONE".equals(value);
        preferenceScreen.findPreference("pref_game_" + key + "_first_arg").setEnabled(enabled);
        preferenceScreen.findPreference("pref_game_" + key + "_second_arg").setEnabled(enabled);
        preferenceScreen.findPreference("pref_game_" + key + "_result").setEnabled(enabled);
    }

    public class ValuesPreferenceValidator extends PreferenceInputValidator {

        public ValuesPreferenceValidator(EditTextPreference preference) {
            super(preference);
        }

        @Override
        public void isValid(CharSequence s) throws Exception {
            Values.parse(s);
        }
    }

    private void updatePreferenceSummary(Preference preference) {
        log.debug("updatePreferenceSummary(" + preference.getKey() + ")");
        preferenceScreenHelper.updatePreferenceSummary(preference);
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
}
