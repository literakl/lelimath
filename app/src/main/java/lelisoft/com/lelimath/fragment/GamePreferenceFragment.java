package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lelisoft.com.lelimath.R;

/**
 * Preference for a game
 * Created by Leoš on 17.01.2016.
 */
public class GamePreferenceFragment extends Fragment {
    private static final String logTag = GamePreferenceFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(logTag, "onCreateView()");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.prototyo, container, false);
    }
}
