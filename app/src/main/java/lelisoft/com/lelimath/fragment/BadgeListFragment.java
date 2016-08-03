package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.BadgeAdapter;
import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.view.BadgeView;

/**
 * List of all badges with info if a badge is taken
 * Created by Leoš on 16.05.2016.
 */
public class BadgeListFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(BadgeListFragment.class);

    RecyclerView recyclerView;
    FragmentActivity activity;
    BadgeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_dashboard_badges, container, false);
        activity = getActivity();
        List<BadgeView> records = fetchBadgeViews();
        adapter = new BadgeAdapter(records);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        return recyclerView;
    }

    private List<BadgeView> fetchBadgeViews() {
        List<Badge> badges = Arrays.asList(Badge.values());
        List<BadgeView> views = new ArrayList<>(badges.size());
        Map<Badge, List<BadgeAward>> allAwardedBadges = LeliMathApp.getInstance().getBadges();
        for (Badge badge : badges) {
            BadgeView view = new BadgeView(badge);
            List<BadgeAward> awards = allAwardedBadges.get(badge);
            if (awards != null) {
                view.awarded = true;
                long maxTime = 0;
                for (BadgeAward award : awards) {
                    if (award.getDate().getTime() > maxTime) {
                        maxTime = award.getDate().getTime();
                    }
                }
                view.awardedToday = Misc.isInCurrentDay(maxTime);
            }
            views.add(view);
        }
        return views;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);
        Metrics.saveContentDisplayed("dashboard", "badges");
    }

    public static BadgeListFragment newInstance() {
        return new BadgeListFragment();
    }
}
