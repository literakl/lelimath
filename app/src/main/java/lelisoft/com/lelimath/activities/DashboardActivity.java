package lelisoft.com.lelimath.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.fragment.BadgeListFragment;
import lelisoft.com.lelimath.fragment.DashboardHomeFragment;
import lelisoft.com.lelimath.fragment.PlayRecordListFragment;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;

public class DashboardActivity extends LeliBaseActivity implements DashboardHomeFragment.TabSwitcher {
    ViewPager viewPager;
    int tabPosition = 0;
    Map<Badge, List<BadgeAward>> allAwardedBadges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

        viewPager  = (ViewPager) findViewById(R.id.dashboard_viewpager);
        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(tabPosition);

        BadgeAwardProvider provider = new BadgeAwardProvider(this);
        allAwardedBadges = provider.getAll();

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void switchToBadgesTab() {
        viewPager.setCurrentItem(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tabPosition = viewPager.getCurrentItem();
    }

    class TabsAdapter extends FragmentStatePagerAdapter {
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0:
                    DashboardHomeFragment homeFragment = DashboardHomeFragment.newInstance();
                    homeFragment.setAllAwardedBadges(allAwardedBadges);
                    return homeFragment;
                case 1:
                    BadgeListFragment badgeListFragment = BadgeListFragment.newInstance();
                    badgeListFragment.setAllAwardedBadges(allAwardedBadges);
                    return badgeListFragment;
                case 2:
                    return PlayRecordListFragment.newInstance();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return getString(R.string.tab_home);
                case 1: return getString(R.string.tab_badges);
                case 2: return getString(R.string.tab_log);
            }
            return "";
        }
    }
}
