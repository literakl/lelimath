package lelisoft.com.lelimath.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.GamePreferenceFragment;

/**
 * Preference for a game
 * Created by Leoš on 17.01.2016.
 */
public class GamePreferenceActivity extends Activity {
    public static final String KEY_COMPLEXITY = "prefGameComplexity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GamePreferenceFragment())
                .commit();

        PreferenceManager.setDefaultValues(this, R.xml.game_prefs, false);
    }
}