/*
 * Copyright (c) 2014 Wincor Nixdorf s.r.o.
 * All rights reserved.
 */
package lelisoft.com.lelimath.helpers;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.widget.BaseAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Badge;

import static lelisoft.com.lelimath.activities.GamePreferenceActivity.KEY_NEXT_BADGE;
import static lelisoft.com.lelimath.activities.GamePreferenceActivity.KEY_SOUND_ENABLED;
import static lelisoft.com.lelimath.activities.GamePreferenceActivity.KEY_SOUND_LEVEL;

/**
 * Workaround for Android bug that Summary is not generated for most types of Preferences
 * http://stackoverflow.com/questions/7017082/change-the-summary-of-a-listpreference-with-the-new-value-android/15329652#15329652
 */
public class PreferenceHelper {
	private static final Logger log = LoggerFactory.getLogger(PreferenceHelper.class);

	final Map<Preference, CharSequence> defaultSummaries = new HashMap<>();

	public PreferenceHelper(PreferenceScreen preferenceScreen) {
        log.debug("started for screen " + preferenceScreen);
		for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
			initSummary(preferenceScreen.getPreference(i), preferenceScreen.getSharedPreferences());
		}
        log.debug("finished");
	}

	private void initSummary(Preference preference, SharedPreferences sharedPreferences) {
		if (preference instanceof PreferenceGroup) {
            PreferenceGroup group = (PreferenceGroup) preference;
            if (group.getKey() != null) {
                switch (preference.getKey()) {
                    case "pref_game_plus_category":
                        setScreenSummary("plus", group, sharedPreferences);
                        break;
                    case "pref_game_minus_category":
                        setScreenSummary("minus", group, sharedPreferences);
                        break;
                    case "pref_game_multiply_category":
                        setScreenSummary("multiply", group, sharedPreferences);
                        break;
                    case "pref_game_divide_category":
                        setScreenSummary("divide", group, sharedPreferences);
                        break;
                    case "pref_misc_sound_category":
                        setSoundSummary((PreferenceScreen) group, sharedPreferences, null, null);
                        break;
                }
            }

			for (int i = 0; i < group.getPreferenceCount(); i++) {
				initSummary(group.getPreference(i), sharedPreferences);
			}
        } else {
            if (KEY_NEXT_BADGE.equals(preference.getKey())) {
                String value = sharedPreferences.getString(KEY_NEXT_BADGE, null);
                if (value != null) {
                    Badge badge = Badge.valueOf(value);
                    preference.setSummary(badge.getTitle());
                } else {
                    preference.setSummary(null);
                }
            } else {
                updatePreferenceSummary(preference);
            }
		}
	}

    public void setScreenSummary(String key, Preference preference, SharedPreferences sharedPreferences) {
        boolean value = sharedPreferences.getBoolean("pref_game_operation_" + key, true);
        preference.setSummary(value ? R.string.pref_operation_enabled : R.string.pref_operation_disabled);
    }

    public void setScreenSummary(String key, PreferenceScreen preferencesRoot, SharedPreferences sharedPreferences) {
        boolean value = sharedPreferences.getBoolean("pref_game_operation_" + key, true);
        PreferenceScreen preference = (PreferenceScreen) preferencesRoot.findPreference("pref_game_" + key + "_category");
        preference.setSummary(value ? R.string.pref_operation_enabled : R.string.pref_operation_disabled);
		//This is necessary to reflect change after coming back from sub-pref screen
		((BaseAdapter)preferencesRoot.getRootAdapter()).notifyDataSetChanged();
	}

	public void setSoundSummary(PreferenceScreen preferenceScreen, SharedPreferences sharedPreferences, Boolean enabled, Integer volume) {
        if (enabled == null) {
            enabled = sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true);
        }
        if (volume == null) {
            volume = sharedPreferences.getInt(KEY_SOUND_LEVEL, 50);
        }

        Preference volumePreference = preferenceScreen.findPreference(KEY_SOUND_LEVEL);
		if (! enabled) {
            preferenceScreen.setSummary(R.string.pref_sound_muted);
            volumePreference.setSummary("");
        } else {
            Resources resources = LeliMathApp.getInstance().getResources();
            String summary = resources.getString(R.string.pref_sound_current_level, volume);
            if (volume == 0) {
                preferenceScreen.setSummary(R.string.pref_sound_muted);
            } else {
                preferenceScreen.setSummary(summary);
            }
            volumePreference.setSummary(summary);
        }
		//This is necessary to reflect change after coming back from sub-pref screen
		((BaseAdapter)preferenceScreen.getRootAdapter()).notifyDataSetChanged();
	}

    public void updatePreferenceSummary(Preference preference) {
	    if (preference == null) {
		    return;
	    }
	    addToDefaults(preference);

        if (KEY_NEXT_BADGE.equals(preference.getKey())) {
            String value = preference.getSharedPreferences().getString(KEY_NEXT_BADGE, null);
            if (value != null) {
                Badge badge = Badge.valueOf(value);
                preference.setSummary(badge.getTitle());
            } else {
                preference.setSummary(null);
            }
            return;
        }

	    CharSequence currentValue = null;
	    if (preference instanceof ListPreference) {
		    currentValue = ((ListPreference)preference).getEntry();
        } else if ((preference instanceof EditTextPreference)) {
		    currentValue = ((EditTextPreference)preference).getText();
        } else if ((preference instanceof MultiSelectListPreference)) {
            MultiSelectListPreference msPref = (MultiSelectListPreference) preference;
            StringBuilder sb = new StringBuilder();
            CharSequence[] entries = msPref.getEntries();
            Set<String> values = msPref.getValues();
            Iterator<String> iterator = values.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                int index = msPref.findIndexOfValue(key);
                sb.append(entries[index]);
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
            currentValue = sb;
        }
	    setSummary(preference, currentValue);
    }

	private void addToDefaults(Preference preference) {
		if (!defaultSummaries.containsKey(preference)) {
			String summary = Misc.toString(preference.getSummary(), false);
			defaultSummaries.put(preference, summary);
		}
	}

	private void setSummary(Preference preference, CharSequence currentValue) {
		String summary = createSummary(preference, currentValue);
		if (!Misc.isNullOrEmpty(summary)) {
			preference.setSummary(summary);
		} else {
			preference.setSummary(null);
		}
	}

	private String createSummary(Preference preference, CharSequence currentValue) {
		String summary = Misc.toString(defaultSummaries.get(preference), false);
		if (summary.contains("%s")) {
			summary = format(summary, currentValue);
		}
		if (summary.indexOf('}') > 0) {
			summary = summary.substring(summary.indexOf('}') + 1);
		}
        if (preference instanceof ListPreference) {
            summary = (currentValue == null) ? "" : currentValue.toString();
        }
        if (preference instanceof MultiSelectListPreference) {
            summary = (currentValue == null) ? "" : currentValue.toString();
        }
		return summary;
	}

	private String format(String summary, CharSequence currentValue) {
		String value;
		if (Misc.isNullOrEmpty(currentValue)) {
			//..nothing selected, get {xyz} value from xml
			if (summary.startsWith("{") && summary.indexOf('}') > 0) {
				value = summary.substring(1, summary.indexOf('}'));
			} else {
				//..no {} in the summary, which is wrong because the summary contains '%s'
				value = "???";
			}
		} else {
			value = currentValue.toString();
		}
		return String.format(summary, value);
	}
}