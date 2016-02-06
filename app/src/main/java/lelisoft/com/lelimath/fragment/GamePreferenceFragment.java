package lelisoft.com.lelimath.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.InputFilter;
import android.util.Log;
import android.widget.EditText;

import lelisoft.com.lelimath.R;
import static lelisoft.com.lelimath.activities.GamePreferenceActivity.*;
import lelisoft.com.lelimath.helpers.PreferenceHelper;
import lelisoft.com.lelimath.helpers.ValuesInputFilter;

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

        InputFilter[] filters = {new ValuesInputFilter()};
        EditText editText = ((EditTextPreference) findPreference(KEY_FIRST_OPERAND)).getEditText();
        editText.setFilters(filters);
        editText = ((EditTextPreference) findPreference(KEY_SECOND_OPERAND)).getEditText();
        editText.setFilters(filters);
        editText = ((EditTextPreference) findPreference(KEY_RESULT)).getEditText();
        editText.setFilters(filters);
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

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        /* move cursor to end of line in EditText preferences */
        if (preference instanceof EditTextPreference) {
            EditText prefEt = ((EditTextPreference) preference).getEditText();
            prefEt.setSelection(prefEt.length());
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
