package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.activities.CalcActivity;
import lelisoft.com.lelimath.activities.GamePreferenceActivity;
import lelisoft.com.lelimath.activities.PuzzleActivity;

/**
 * Fragment for home tab on Dashboard
 * Created by Leoš on 02.05.2016.
 */
public class DashboardHomeFragment extends Fragment implements View.OnClickListener {
    private static final Logger log = LoggerFactory.getLogger(DashboardHomeFragment.class);
    FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.fragment_dashboard_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);

        activity = getActivity();
        TextView button = (TextView) activity.findViewById(R.id.main_button_puzzle);
        button.setOnClickListener(this);
        button = (TextView) activity.findViewById(R.id.main_button_calc);
        button.setOnClickListener(this);
        button = (TextView) activity.findViewById(R.id.main_button_settings);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_button_puzzle:
                PuzzleActivity.start(activity);
                break;

            case R.id.main_button_calc:
                CalcActivity.start(activity);
                break;

            case R.id.main_button_settings:
                GamePreferenceActivity.start(activity);
                break;
        }
    }
}