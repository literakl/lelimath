package lelisoft.com.lelimath.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.helpers.PreferenceHelper;
import lelisoft.com.lelimath.helpers.PreferenceInputValidator;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Preferences for a game. Utilizes source code from https://github.com/davcpas1234/MaterialSettings.
 * Created by Leoš on 17.01.2016.
 */
@RuntimePermissions
public class GamePreferenceActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final Logger log = LoggerFactory.getLogger(GamePreferenceActivity.class);

    public static final String KEY_CURRENT_VERSION = "pref_current_version";
    public static final String KEY_COMPLEXITY = "pref_game_complexity";
    public static final String KEY_UNKNOWN = "pref_game_calc_unknown";

    private PreferenceHelper preferenceScreenHelper;
    private DependencyMap dependencyMap;
    private long copyDBLastClick = 0;

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
            return false;
        }

        if ("pref_build_number".equals(preference.getKey())) {
            if (System.currentTimeMillis() - copyDBLastClick < 500) {
                GamePreferenceActivityPermissionsDispatcher.copyDatabaseWithCheck(this);
            } else {
                copyDBLastClick = System.currentTimeMillis();
            }
        }

        return false;
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void copyDatabase() {
        new CopyDatabaseTask().execute();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForStorage(final PermissionRequest permissionRequest) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_fs_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionRequest.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionRequest.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForStorage() {
        Toast.makeText(this, R.string.permission_fs_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForStorage() {
        Toast.makeText(this, R.string.permission_fs_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        GamePreferenceActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        log.debug("onSharedPreferenceChanged(" + key + ")");
        //noinspection deprecation
        PreferenceScreen preferencesRoot = getPreferenceScreen();
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

    private void updatePreferenceSummary(Preference preference) {
        log.debug("updatePreferenceSummary(" + preference.getKey() + ")");
        preferenceScreenHelper.updatePreferenceSummary(preference);
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

    public class ValuesPreferenceValidator extends PreferenceInputValidator {

        public ValuesPreferenceValidator(EditTextPreference preference) {
            super(preference);
        }

        @Override
        public void isValid(CharSequence s) throws Exception {
            Values.parse(s);
        }
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
    private class DependencyMap {
        Map<String, String> operations = new LinkedHashMap<>(5, 1.0f);
        Map<String, String> dependencyMapping = new HashMap<>(5, 1.0f);
        String noReuse;

        public DependencyMap(SharedPreferences sharedPreferences) {
            Resources resources = getResources();
            noReuse = resources.getString(R.string.pref_value_not_depend);
            operations.put("PLUS", resources.getString(R.string.operation_plus));
            operations.put("MINUS", resources.getString(R.string.operation_minus));
            operations.put("MULTIPLY", resources.getString(R.string.operation_multiply));
            operations.put("DIVIDE", resources.getString(R.string.operation_divide));

            String value = sharedPreferences.getString("pref_game_plus_depends", "NONE");
            if (! "NONE".equals(value)) {
                dependencyMapping.put("PLUS", value);
            }

            value = sharedPreferences.getString("pref_game_minus_depends", "NONE");
            if (! "NONE".equals(value)) {
                dependencyMapping.put("MINUS", value);
            }

            value = sharedPreferences.getString("pref_game_multiply_depends", "NONE");
            if (! "NONE".equals(value)) {
                dependencyMapping.put("MULTIPLY", value);
            }

            value = sharedPreferences.getString("pref_game_divide_depends", "NONE");
            if (! "NONE".equals(value)) {
                dependencyMapping.put("DIVIDE", value);
            }
        }

        public void updateDependencies(PreferenceScreen preferenceRoot) {
            ArrayList<CharSequence> entries;
            ArrayList<CharSequence> values;
            for (String operation : operations.keySet()) {
                String prefOperation = operation.toLowerCase();
                ListPreference preference = (ListPreference) preferenceRoot.findPreference("pref_game_" + prefOperation + "_depends");
                List<String> dependencies = getKeys(dependencyMapping, operation);
                if (! dependencies.isEmpty()) {
                    preference.setEnabled(false); // dependency target must define definitions itself
                    StringBuilder sb = new StringBuilder();
                    String operationCaption = getOperationName(operation);
                    sb.append(getString(R.string.pref_operation_dependencies, operationCaption));
                    for (String dependency : dependencies) {
                        sb.append(getOperationName(dependency));
                        sb.append(", ");
                    }
                    sb.setLength(sb.length() - 2);
                    preference.setSummary(sb);
                    continue;
                } else {
                    preference.setEnabled(true);
                    preferenceScreenHelper.updatePreferenceSummary(preference);
                }

                entries = new ArrayList<>(5); entries.add(noReuse);
                values = new ArrayList<>(5); values.add("NONE");

                for (String targetOperation : operations.keySet()) {
                    if (operation.equals(targetOperation)) {
                        continue; // canot create a dependency to self
                    }
                    if (dependencyMapping.containsKey(targetOperation)) {
                        continue; // cannot create a dependency to operation that already depends on some operation
                    }

                    values.add(targetOperation);
                    entries.add(operations.get(targetOperation));
                }

                preference = (ListPreference) preferenceRoot.findPreference("pref_game_" + prefOperation + "_depends");
                preference.setEntries(entries.toArray(new CharSequence[entries.size()]));
                preference.setEntryValues(values.toArray(new CharSequence[values.size()]));
            }
        }

        private String getOperationName(String operation) {
            try {
                int id = R.string.class.getField("operation_" + operation.toLowerCase()).getInt(null);
                return getString(id);
            } catch (IllegalAccessException e) {
                log.warn("Cannot find resource: " + "operation_" + operation.toLowerCase(), e);
            } catch (NoSuchFieldException e) {
                log.warn("Cannot find resource:  " + "operation_" + operation.toLowerCase(), e);
            }
            return operation;
        }

        public void setReuse(String key, String value) {
            dependencyMapping.remove(key);
            if (! "NONE".equals(value)) {
                dependencyMapping.put(key, value);
            }
        }

        private List<String> getKeys(Map<String, String> map, @NonNull String value) {
            List<String> result = new ArrayList<>(map.size());
            for (String key : map.keySet()) {
                String s = map.get(key);
                if (value.equals(s)) {
                    result.add(key);
                }
            }
            return result;
        }
    }

    private class CopyDatabaseTask extends AsyncTask<Void, Void, Void> {
        String message = "Database copied";

        @SuppressLint("SetWorldReadable")
        @Override
        protected Void doInBackground(Void[] params) {
            if (! Misc.isExternalStorageWritable()) {
                message = "External storage is not writable! Is not it mounted to a computer?";
                return null;
            }

            File in = DatabaseHelper.getDatabasePath();
            File out = new File(Environment.getExternalStorageDirectory(), in.getName());
            //noinspection ResultOfMethodCallIgnored
            out.setReadable(true, false);
            log.debug("Copying database {} to SD card at {}", in.getName(), out.getAbsolutePath());
            boolean result = Misc.copyFile(in, out);
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{out.getAbsolutePath()}, null, null);
            log.debug("finished {}", result ? "successfully" : "unsuccessfully");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(GamePreferenceActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
