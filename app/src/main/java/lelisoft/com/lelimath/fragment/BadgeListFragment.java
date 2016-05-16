package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.BadgeAdapter;
import lelisoft.com.lelimath.data.Badge;

/**
 * List of all badges with info if a badge is taken
 * Created by Leoš on 16.05.2016.
 */
public class BadgeListFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(DashboardHomeFragment.class);

    RecyclerView recyclerView;
    FragmentActivity activity;
    BadgeAdapter adapter;
    List<Badge> badges;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_dashboard_badges, container, false);
        activity = getActivity();
        badges = Arrays.asList(Badge.values());
        adapter = new BadgeAdapter(badges);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        return recyclerView;
    }

    public static Fragment newInstance() {
        return new BadgeListFragment();
    }
}
