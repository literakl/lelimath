package lelisoft.com.lelimath.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.DashboardHomeFragment;
import lelisoft.com.lelimath.fragment.FormulaRecordListFragment;

public class DashboardActivity extends LeliBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

        ViewPager viewPager  = (ViewPager) findViewById(R.id.dashboard_viewpager);
        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    class TabsAdapter extends FragmentStatePagerAdapter {
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0: return DashboardHomeFragment.newInstance();
                case 1: return FormulaRecordListFragment.newInstance();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Home";
                case 1: return "Log";
            }
            return "";
        }
    }
}
