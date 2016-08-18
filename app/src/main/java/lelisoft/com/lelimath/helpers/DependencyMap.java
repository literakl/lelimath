package lelisoft.com.lelimath.helpers;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.R;

/**
 * Handles dependency between operations
 */
public class DependencyMap {
    private static final Logger log = LoggerFactory.getLogger(DependencyMap.class);

    Map<String, String> operations = new LinkedHashMap<>(5, 1.0f);
    Map<String, String> dependencyMapping = new HashMap<>(5, 1.0f);
    String noReuse;
    Resources resources;
    private PreferenceHelper preferenceScreenHelper;

    public DependencyMap(SharedPreferences sharedPreferences, Resources resources, PreferenceHelper preferenceScreenHelper) {
        this.resources = resources;
        this.preferenceScreenHelper = preferenceScreenHelper;

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
                sb.append(resources.getString(R.string.pref_operation_dependencies, operationCaption));
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
            return resources.getString(id);
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
