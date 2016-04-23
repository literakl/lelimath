package lelisoft.com.lelimath.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.helpers.PreferenceHelper;

/**
 * Preferences for a game. Utilizes source code from https://github.com/davcpas1234/MaterialSettings.
 * Created by Leoš on 17.01.2016.
 */
public class GamePreferenceActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {
    private static final Logger log = LoggerFactory.getLogger(GamePreferenceActivity.class);

    public static final String KEY_CURRENT_VERSION = "pref_current_version";
    public static final String KEY_COMPLEXITY = "pref_game_complexity";
    public static final String KEY_UNKNOWN = "pref_game_calc_unknown";

    private PreferenceHelper preferenceScreenHelper;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        log.debug("onPostCreate");
        super.onPostCreate(savedInstanceState);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.list).getParent().getParent().getParent();
        AppBarLayout appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(appBar, 0);

        Toolbar toolbar = (Toolbar) appBar.getChildAt(0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //noinspection deprecation
        addPreferencesFromResource(R.xml.game_prefs);
        //noinspection deprecation
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        removeEntry((ListPreference) preferenceScreen.findPreference("pref_game_plus_reuse"), "PLUS");
        removeEntry((ListPreference) preferenceScreen.findPreference("pref_game_minus_reuse"), "MINUS");
        removeEntry((ListPreference) preferenceScreen.findPreference("pref_game_multiply_reuse"), "MULTIPLY");
        removeEntry((ListPreference) preferenceScreen.findPreference("pref_game_divide_reuse"), "DIVIDE");
        changeDefinitionsState("plus", preferenceScreen);
        changeDefinitionsState("minus", preferenceScreen);
        changeDefinitionsState("multiply", preferenceScreen);
        changeDefinitionsState("divide", preferenceScreen);
        preferenceScreen.findPreference("pref_game_plus_reuse").setOnPreferenceChangeListener(this);
        preferenceScreen.findPreference("pref_game_operation_divide").setOnPreferenceChangeListener(this);
        preferenceScreen.findPreference("pref_game_operation_plus").setOnPreferenceChangeListener(this);

        preferenceScreenHelper = new PreferenceHelper(preferenceScreen);

/*
        InputFilter[] filters = {new ValuesInputFilter()};
        EditText editText = ((EditTextPreference) findPreference(KEY_FIRST_OPERAND)).getEditText();
        editText.setFilters(filters);
*/
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        log.debug("onPreferenceTreeClick(" + preference.getKey() + ")");
        super.onPreferenceTreeClick(preferenceScreen, preference);

        if (preference instanceof PreferenceScreen) {
            if (((PreferenceScreen) preference).getDialog() != null) {
                Drawable background = this.getWindow().getDecorView().getBackground().getConstantState().newDrawable();
                ((PreferenceScreen) preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(background);
                setUpNestedScreen((PreferenceScreen) preference);

                return false;
            }
        }

            /* move cursor to end of line in EditText preferences */
        if (preference instanceof EditTextPreference) {
            EditText prefEt = ((EditTextPreference) preference).getEditText();
            prefEt.setSelection(prefEt.length());
        }

        return false;
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        log.debug("onPreferenceChange(" + preference.getKey() + ")");
/*
        //noinspection deprecation
        PreferenceScreen preferencesRoot = getPreferenceScreen();
        Preference category;
        switch (preference.getKey()) {
            case "pref_game_plus_reuse":
                category = preferencesRoot.findPreference("pref_game_divide_category");
                category.setSummary("tesr");
                log.debug(category.getSummary().toString());
                return true;

            case "pref_game_operation_plus":
                category = preferencesRoot.findPreference("pref_game_divide_category");
                category.setSummary("pokus " + ((CheckBoxPreference)preference).isChecked());
                log.debug(category.getSummary().toString());
                return true;

            case "pref_game_operation_divide":
                category = preferencesRoot.findPreference("pref_game_plus_category");
                category.setSummary("pokus2 " + ((CheckBoxPreference)preference).isChecked());
                log.debug(category.getSummary().toString());
                return true;
        }
*/

        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        log.debug("onSharedPreferenceChanged(" + key + ")");
        //noinspection deprecation
        PreferenceScreen preferencesRoot = getPreferenceScreen();
        Preference preference;
        switch (key) {
            case "pref_game_plus_reuse":
                changeDefinitionsState("plus", preferencesRoot);
                return;

            case "pref_game_minus_reuse":
                changeDefinitionsState("minus", preferencesRoot);
                return;

            case "pref_game_multiply_reuse":
                changeDefinitionsState("multiply", preferencesRoot);
                return;

            case "pref_game_divide_reuse":
                changeDefinitionsState("divide", preferencesRoot);
                return;
/*

            case "pref_game_operation_plus":
//                preferenceScreenHelper.setScreenSummary("plus", preferencesRoot, sharedPreferences);
                preference = preferencesRoot.findPreference("pref_game_divide_category");
                preference.setSummary("pokus");
                log.debug(preference.getSummary().toString());
                return;

            case "pref_game_operation_minus":
                preferenceScreenHelper.setScreenSummary("minus", preferencesRoot, sharedPreferences);
                return;

            case "pref_game_operation_multiply":
                preferenceScreenHelper.setScreenSummary("multiply", preferencesRoot, sharedPreferences);
                return;

            case "pref_game_operation_divide":
                preference = preferencesRoot.findPreference("pref_game_plus_category");
                preference.setSummary("pokus");
                log.debug(preference.getSummary().toString());
                preferenceScreenHelper.setScreenSummary("divide", preferencesRoot, sharedPreferences);
                return;
*/
        }

        //noinspection deprecation
        updatePreferenceSummary(findPreference(key));
    }

    private void changeDefinitionsState(String key, PreferenceScreen preferenceScreen) {
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        String value = sharedPreferences.getString("pref_game_" + key + "_reuse", "NONE");
        boolean enabled = "NONE".equals(value);
        preferenceScreen.findPreference("pref_game_" + key + "_first_arg").setEnabled(enabled);
        preferenceScreen.findPreference("pref_game_" + key + "_second_arg").setEnabled(enabled);
        preferenceScreen.findPreference("pref_game_" + key + "_result").setEnabled(enabled);
    }

    private void updatePreferenceSummary(Preference p) {
        log.debug("updatePreferenceSummary");
        preferenceScreenHelper.updatePreferenceSummary(p);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();
        LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
        AppBarLayout appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(appBar, 0);

        View listRoot = dialog.findViewById(android.R.id.list);
        if(listRoot != null) {
            listRoot.setPadding(0, listRoot.getPaddingTop(), 0, listRoot.getPaddingBottom());
        }

        Toolbar toolbar = (Toolbar) appBar.getChildAt(0);
        toolbar.setTitle(preferenceScreen.getTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Finds key in List preference and removes it. The key must be present between entry values.
     * @param pref List preference
     * @param key key
     */
    private void removeEntry(ListPreference pref, String key) {
        CharSequence[] entries = pref.getEntries(), copyEntries = new CharSequence[entries.length - 1];
        CharSequence[] values = pref.getEntryValues(), copyValues = new CharSequence[values.length - 1];

        int index = -1;
        for (int i = 0, j = 0; i < values.length; i++) {
            if (key.equals(values[i])) {
                index = i;
                continue;
            }
            copyValues[j++] = values[i];
        }
        pref.setEntryValues(copyValues);

        for (int i = 0, j = 0; i < entries.length; i++) {
            if (i == index) {
                continue;
            }
            copyEntries[j++] = entries[i];
        }
        pref.setEntries(copyEntries);
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

    public static void start(Context c) {
        c.startActivity(new Intent(c, GamePreferenceActivity.class));
    }
}
