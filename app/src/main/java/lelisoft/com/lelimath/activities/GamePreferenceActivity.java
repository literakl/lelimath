package lelisoft.com.lelimath.activities;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.GamePreferenceFragment;

/**
 * Preference for a game
 * Created by Leoš on 17.01.2016.
 */
public class GamePreferenceActivity extends AppCompatActivity {
    private static final Logger log = LoggerFactory.getLogger(GamePreferenceActivity.class);

    public static final String KEY_COMPLEXITY = "pref_game_complexity";
    public static final String KEY_OPERATIONS = "pref_game_operations";
    public static final String KEY_FIRST_OPERAND = "pref_game_first_operand";
    public static final String KEY_SECOND_OPERAND = "pref_game_second_operand";
    public static final String KEY_RESULT = "pref_game_result";
    public static final String KEY_UNKNOWN = "pref_game_calc_unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate()");
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, new GamePreferenceFragment());
        transaction.commit();

        PreferenceManager.setDefaultValues(this, R.xml.game_prefs, false);
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, GamePreferenceActivity.class));
    }
}
