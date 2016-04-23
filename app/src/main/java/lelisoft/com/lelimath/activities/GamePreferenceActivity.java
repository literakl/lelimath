package lelisoft.com.lelimath.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private ReuseMap reuseMap;

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
        preferenceScreenHelper = new PreferenceHelper(preferenceScreen);
        reuseMap = new ReuseMap(preferenceScreen.getSharedPreferences());
        reuseMap.updateReuseList(preferenceScreen);

        changeDefinitionsState("plus", preferenceScreen);
        changeDefinitionsState("minus", preferenceScreen);
        changeDefinitionsState("multiply", preferenceScreen);
        changeDefinitionsState("divide", preferenceScreen);
//        preferenceScreen.findPreference("pref_game_plus_reuse").setOnPreferenceChangeListener(this);
//        preferenceScreen.findPreference("pref_game_operation_divide").setOnPreferenceChangeListener(this);
//        preferenceScreen.findPreference("pref_game_operation_plus").setOnPreferenceChangeListener(this);

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
                reuseMap.updateReuseList(preferencesRoot);

            case "pref_game_minus_reuse":
                changeDefinitionsState("minus", preferencesRoot);
                reuseMap.updateReuseList(preferencesRoot);

            case "pref_game_multiply_reuse":
                changeDefinitionsState("multiply", preferencesRoot);
                reuseMap.updateReuseList(preferencesRoot);

            case "pref_game_divide_reuse":
                changeDefinitionsState("divide", preferencesRoot);
                reuseMap.updateReuseList(preferencesRoot);
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

    /**
     * Enables of disables definition preferences for specified operation.
     * @param key operation key
     * @param preferenceScreen root preferences
     */
    private void changeDefinitionsState(String key, PreferenceScreen preferenceScreen) {
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        String value = sharedPreferences.getString("pref_game_" + key + "_reuse", "NONE");
        reuseMap.setReuse(key.toUpperCase(), value);
        log.debug(key + " reuse is " + value);

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

    /**
     * Handles dependency between operations
     */
    private class ReuseMap {
        Map<String, String> operations = new LinkedHashMap<>(5, 1.0f);
        Map<String, String> reuseMapping = new HashMap<>(5, 1.0f);
        String noReuse;

        public ReuseMap(SharedPreferences sharedPreferences) {
            Resources resources = getResources();
            noReuse = resources.getString(R.string.pref_no_reuse);
            operations.put("PLUS", resources.getString(R.string.operation_plus));
            operations.put("MINUS", resources.getString(R.string.operation_minus));
            operations.put("MULTIPLY", resources.getString(R.string.operation_multiply));
            operations.put("DIVIDE", resources.getString(R.string.operation_divide));

            String value = sharedPreferences.getString("pref_game_plus_reuse", "NONE");
            if (! "NONE".equals(value)) {
                reuseMapping.put("PLUS", value);
            }

            value = sharedPreferences.getString("pref_game_minus_reuse", "NONE");
            if (! "NONE".equals(value)) {
                reuseMapping.put("MINUS", value);
            }

            value = sharedPreferences.getString("pref_game_multiply_reuse", "NONE");
            if (! "NONE".equals(value)) {
                reuseMapping.put("MULTIPLY", value);
            }

            value = sharedPreferences.getString("pref_game_divide_reuse", "NONE");
            if (! "NONE".equals(value)) {
                reuseMapping.put("DIVIDE", value);
            }
        }

        public void updateReuseList(PreferenceScreen preferenceRoot) {
            ArrayList<CharSequence> entries;
            ArrayList<CharSequence> values;
            for (String operation : operations.keySet()) {
                ListPreference preference = (ListPreference) preferenceRoot.findPreference("pref_game_" + operation.toLowerCase() + "_reuse");
                if (reuseMapping.containsValue(operation)) {
                    preference.setEnabled(false); // dependency target must define its definitions
                    continue;
                } else {
                    preference.setEnabled(true);
                }

                String prefOperation = operation.toLowerCase();
                entries = new ArrayList<>(5); entries.add(noReuse);
                values = new ArrayList<>(5); values.add("NONE");

                for (String targetOperation : operations.keySet()) {
                    if (operation.equals(targetOperation)) {
                        continue; // canot create a dependency to self
                    }
                    if (reuseMapping.containsKey(targetOperation)) {
                        continue; // cannot create a dependency to operation that already depends on some operation
                    }

                    values.add(targetOperation);
                    entries.add(operations.get(targetOperation));
                }

                preference = (ListPreference) preferenceRoot.findPreference("pref_game_" + prefOperation + "_reuse");
                preference.setEntries(entries.toArray(new CharSequence[entries.size()]));
                preference.setEntryValues(values.toArray(new CharSequence[values.size()]));
            }
        }

        public void setReuse(String key, String value) {
            reuseMapping.remove(key);
            if (! "NONE".equals(value)) {
                reuseMapping.put(key, value);
            }
        }
    }
}
