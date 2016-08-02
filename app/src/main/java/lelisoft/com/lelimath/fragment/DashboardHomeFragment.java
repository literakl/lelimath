package lelisoft.com.lelimath.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.activities.BadgeAwardActivity;
import lelisoft.com.lelimath.activities.CalcActivity;
import lelisoft.com.lelimath.activities.GamePreferenceActivity;
import lelisoft.com.lelimath.activities.InformationActivity;
import lelisoft.com.lelimath.activities.PointsChartActivity;
import lelisoft.com.lelimath.activities.PuzzleActivity;
import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeProgress;
import lelisoft.com.lelimath.helpers.BadgeProgressComparator;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.logic.BadgeProgressCalculator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.BadgeProgressProvider;
import lelisoft.com.lelimath.provider.PlayRecordProvider;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

import static lelisoft.com.lelimath.data.BadgeProgress.IN_PROGRESS_COLUMN_NAME;
import static lelisoft.com.lelimath.data.Badge.*;

/**
 * Fragment for home tab on Dashboard
 * Created by Leoš on 02.05.2016.
 */
public class DashboardHomeFragment extends LeliBaseFragment implements View.OnClickListener {
    private static final Logger log = LoggerFactory.getLogger(DashboardHomeFragment.class);

    FragmentActivity activity;
    TabSwitcher callback;
    Map<Badge, List<BadgeAward>> allAwardedBadges;
    BadgeProgress nextBadge;

    public interface TabSwitcher {
        void switchToBadgesTab();
    }

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
        int points = provider.getPoints();
        BadgeAwardProvider awardProvider = new BadgeAwardProvider(activity);
        AwardedBadgesCount badgesCount = awardProvider.getBadgesCount();
        setBadgeProgress();
        displayNextAward();

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
        button = (TextView) activity.findViewById(R.id.main_button_badges);
        button.setOnClickListener(this);
        button.setText(resources.getString(R.string.action_badges, badgesCount.gold, badgesCount.silver, badgesCount.bronze));

        new RefreshNextBadgeTask(this).execute();

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

            case R.id.main_button_badges:
                callback.switchToBadgesTab();
                break;

            case R.id.main_button_next_badge:
                Intent intent = new Intent(activity, BadgeAwardActivity.class);
                intent.putExtra(BadgeAwardActivity.KEY_BADGE, nextBadge.getBadge());
                activity.startActivity(intent);
                break;
        }
    }

    private void displayNextAward() {
        TextView button = (TextView) activity.findViewById(R.id.main_button_next_badge);
        if (nextBadge != null) {
            button.setOnClickListener(this);
            Resources resources = LeliMathApp.getInstance().getResources();
            button.setText(resources.getString(R.string.action_next_badge, nextBadge.getBadge().getTitle(),
                    nextBadge.getProgress(), nextBadge.getRequired()));
        } else {
            button.setVisibility(View.GONE);
        }
    }

    /**
     * Choose a badge that is the most easy to gain.
     */
    private void setBadgeProgress() {
        log.debug("setBadgeProgress() starts");
        try {
            if (! allAwardedBadges.containsKey(PAGE)) {
                nextBadge = new BadgeProgress(PAGE, true, 0, 1);
                return;
            } else if (! allAwardedBadges.containsKey(GLADIATOR)) {
                nextBadge = new BadgeProgress(GLADIATOR, true, 0, 1);
                return;
            }

            try {
                BadgeProgressProvider provider = new BadgeProgressProvider(activity);
                QueryBuilder<BadgeProgress, String> builder = provider.queryBuilder();
                List<BadgeProgress> list = builder.where().eq(IN_PROGRESS_COLUMN_NAME, true).query();
                if (! list.isEmpty()) {
                    Collections.sort(list, new BadgeProgressComparator());
                    nextBadge = list.get(0);
                    return;
                }
                nextBadge = null;
            } catch (SQLException e) {
                log.error("Error while fetching the next badge", e);
                nextBadge = null;
            }
        } finally {
            log.debug("setBadgeProgress() finished");
        }
    }

    public void setAllAwardedBadges(Map<Badge, List<BadgeAward>> allAwardedBadges) {
        this.allAwardedBadges = allAwardedBadges;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented the callback interface
        try {
            callback = (TabSwitcher) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TabSwitcher");
        }
    }

    public static DashboardHomeFragment newInstance() {
        return new DashboardHomeFragment();
    }

    static class RefreshNextBadgeTask extends AsyncTask<Void, Void, Void> {
        // Weak references will still allow the Activity to be garbage-collected
        private final WeakReference<DashboardHomeFragment> weakFragment;

        RefreshNextBadgeTask(DashboardHomeFragment fragment) {
            this.weakFragment = new WeakReference<>(fragment);
        }

        @Override
        public Void doInBackground(Void... params) {
            BadgeProgressCalculator.refresh(weakFragment.get().getActivity());
            weakFragment.get().setBadgeProgress();
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            // Re-acquire a strong reference to the activity, and verify
            // that it still exists and is active.
            Activity activity = weakFragment.get().getActivity();
            if (activity == null || activity.isFinishing()) {
                // activity is no longer valid, don't do anything!
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed()) {
                    // activity is no longer valid, don't do anything!
                    return;
                }
            }

            // The activity is still valid, do main-thread stuff here
            weakFragment.get().displayNextAward();
        }
    }
}
