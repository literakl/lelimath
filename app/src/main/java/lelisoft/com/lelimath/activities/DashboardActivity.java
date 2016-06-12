package lelisoft.com.lelimath.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.BadgeListFragment;
import lelisoft.com.lelimath.fragment.DashboardHomeFragment;
import lelisoft.com.lelimath.fragment.PlayRecordListFragment;

public class DashboardActivity extends LeliBaseActivity {
    ViewPager viewPager;
    int tabPosition = 0;

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
                case 0: return DashboardHomeFragment.newInstance();
                case 1: return BadgeListFragment.newInstance();
                case 2: return PlayRecordListFragment.newInstance();
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
