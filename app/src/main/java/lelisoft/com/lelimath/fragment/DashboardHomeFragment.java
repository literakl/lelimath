package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
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
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Metrics;
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
        setNextAward();

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

    private void setNextAward() {
        TextView button = (TextView) activity.findViewById(R.id.main_button_next_badge);
        nextBadge = chooseBadgeProgress();
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
     * @return BadgeProgress for such badge
     */
    private BadgeProgress chooseBadgeProgress() {
        if (! allAwardedBadges.containsKey(PAGE)) {
            return new BadgeProgress(PAGE, true, 0, 1);
        } else if (! allAwardedBadges.containsKey(GLADIATOR)) {
            return new BadgeProgress(GLADIATOR, true, 0, 1);
        }

        try {
            BadgeProgressProvider provider = new BadgeProgressProvider(activity);
            QueryBuilder<BadgeProgress, String> builder = provider.queryBuilder();
            List<BadgeProgress> list = builder.where().eq(IN_PROGRESS_COLUMN_NAME, true).query();
            if (! list.isEmpty()) {
                Collections.sort(list, new BadgeProgressComparator());
                return list.get(0);
            }
            return null;
        } catch (SQLException e) {
            log.error("Error while fetching the next badge", e);
            return null;
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

    /**
     * Compares badgeProgress by remaning formulas. Multi-day badges are sorted at the end.
     */
    private static class BadgeProgressComparator implements Comparator<BadgeProgress> {
        @Override
        public int compare(BadgeProgress lhs, BadgeProgress rhs) {
            switch (lhs.getBadge()) {
                case RETURNER: {
                    switch (rhs.getBadge()) {
                        case RETURNER: return 0;
                        case LONG_DISTANCE_RUNNER: return -1;
                        case MARATHON_RUNNER: return -1;
                        default: return 1;
                    }
                }
                case LONG_DISTANCE_RUNNER: {
                    switch (rhs.getBadge()) {
                        case RETURNER: return 1;
                        case LONG_DISTANCE_RUNNER: return 0;
                        case MARATHON_RUNNER: return -1;
                        default: return 1;
                    }
                }
                case MARATHON_RUNNER: {
                    switch (rhs.getBadge()) {
                        case RETURNER: return 1;
                        case LONG_DISTANCE_RUNNER: return 1;
                        case MARATHON_RUNNER: return 0;
                        default: return 1;
                    }
                }
                default:
                    return lhs.calculateRemainingFormulas() - rhs.calculateRemainingFormulas();
            }
        }
    }
}
