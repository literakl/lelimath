package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import lelisoft.com.lelimath.R;

/**
 * Preference for a game
 * Created by Leoš on 17.01.2016.
 */
public class GamePreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.game_prefs);
    }
}
