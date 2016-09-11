package lelisoft.com.lelimath.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import com.crashlytics.android.Crashlytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeProgress;
import lelisoft.com.lelimath.helpers.BadgeProgressComparator;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.helpers.PreferenceHelper;
import lelisoft.com.lelimath.provider.BadgeProgressProvider;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static lelisoft.com.lelimath.data.BadgeProgress.IN_PROGRESS_COLUMN_NAME;

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
    public static final String KEY_SOUND_ENABLED = "pref_sound_enabled";
    public static final String KEY_SOUND_LEVEL = "pref_sound_level";
    public static final String KEY_NEXT_BADGE = "pref_next_badge";

    private PreferenceHelper preferenceScreenHelper;
    private long copyDBLastClick = 0;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        log.debug("onPostCreate");
        super.onPostCreate(savedInstanceState);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.list).getParent().getParent().getParent();
        AppBarLayout appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.act_settings_toolbar, root, false);
        root.addView(appBar, 0);

        Toolbar toolbar = (Toolbar) appBar.getChildAt(0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //noinspection deprecation
        addPreferencesFromResource(R.xml.app_prefs);
        //noinspection deprecation
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreenHelper = new PreferenceHelper(preferenceScreen);
        setNextBadgePreference(preferenceScreen);
    }

    private void setNextBadgePreference(PreferenceScreen preferenceScreen) {
        ListPreference preference = ((ListPreference) preferenceScreen.findPreference("pref_next_badge"));
        BadgeProgressProvider provider = new BadgeProgressProvider(this);
        ArrayList<String> captions = new ArrayList<>(Badge.values().length);
        ArrayList<String> values = new ArrayList<>(captions.size());
        try {
            List<BadgeProgress> list = provider.queryBuilder().where().eq(IN_PROGRESS_COLUMN_NAME, true).query();
            Collections.sort(list, new BadgeProgressComparator());
            for (BadgeProgress progress : list) {
                captions.add(getString(R.string.pref_badge_progress, progress.getBadge().getTitle(),
                        progress.getProgress(), progress.getRequired()));
                values.add(progress.getBadge().name());
            }
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Error while fetching badge progress list", e);
        }

        preference.setEntries(captions.toArray(new CharSequence[captions.size()]));
        preference.setEntryValues(values.toArray(new CharSequence[values.size()]));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Metrics.saveContentDisplayed("preferences", null);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        log.debug("onSharedPreferenceChanged(" + key + ")");
        //noinspection deprecation
        PreferenceScreen preferencesRoot = getPreferenceScreen();
        PreferenceScreen preference;
        switch (key) {
            case KEY_SOUND_ENABLED:
                boolean enabled = sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true);
                LeliMathApp.getInstance().toggleSound(enabled);
                preference = (PreferenceScreen) preferencesRoot.findPreference("pref_misc_sound_category");
                preferenceScreenHelper.setSoundSummary(preference, sharedPreferences, enabled, null);
                return;

            case KEY_SOUND_LEVEL:
                int volume = sharedPreferences.getInt(KEY_SOUND_LEVEL, 50);
                LeliMathApp.getInstance().setVolume(volume);
                preference = (PreferenceScreen) preferencesRoot.findPreference("pref_misc_sound_category");
                preferenceScreenHelper.setSoundSummary(preference, sharedPreferences, null, volume);
                return;
        }

        //noinspection deprecation
        updatePreferenceSummary(findPreference(key));
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
        View listRoot = dialog.findViewById(android.R.id.list);
        listRoot.setPadding(0, listRoot.getPaddingTop(), 0, listRoot.getPaddingBottom());

        LinearLayout root;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            root = (LinearLayout) listRoot.getParent().getParent();
        } else {
            root = (LinearLayout) listRoot.getParent();
        }

        AppBarLayout appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.act_settings_toolbar, root, false);
        root.addView(appBar, 0);

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
