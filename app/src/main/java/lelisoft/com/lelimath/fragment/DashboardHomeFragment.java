package lelisoft.com.lelimath.fragment;

import android.content.Intent;
import android.content.res.Resources;
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
import lelisoft.com.lelimath.activities.InformationActivity;
import lelisoft.com.lelimath.activities.LeliFragmentActivity;
import lelisoft.com.lelimath.activities.PointsChartActivity;
import lelisoft.com.lelimath.activities.PuzzleActivity;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.provider.PlayRecordProvider;

/**
 * Fragment for home tab on Dashboard
 * Created by Leoš on 02.05.2016.
 */
public class DashboardHomeFragment extends LeliBaseFragment implements View.OnClickListener {
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

        PlayRecordProvider provider = new PlayRecordProvider(activity);
        int points = provider.getUserPoints(LeliMathApp.getInstance().getCurrentUser());

        TextView button = (TextView) activity.findViewById(R.id.main_button_puzzle);
        button.setOnClickListener(this);
        button = (TextView) activity.findViewById(R.id.main_button_calc);
        button.setOnClickListener(this);
        button = (TextView) activity.findViewById(R.id.main_button_settings);
        button.setOnClickListener(this);
        button = (TextView) activity.findViewById(R.id.main_button_info);
        button.setOnClickListener(this);
        button = (TextView) activity.findViewById(R.id.main_button_points);
        button.setOnClickListener(this);
        Resources resources = LeliMathApp.getInstance().getResources();
        button.setText(resources.getString(R.string.action_points, points));

        Metrics.saveContentDisplayed("dashboard", "home");
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

            case R.id.main_button_info:
                InformationActivity.start(activity);
                break;

            case R.id.main_button_points:
                PointsChartActivity.start(activity);
                break;
        }
    }

    public static Fragment newInstance() {
        return new DashboardHomeFragment();
    }
}
