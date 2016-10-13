package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.DressFigureFragment;
import lelisoft.com.lelimath.fragment.DressListFragment;

/**
 * Display list of figures, their current dress and alow user to buy new items.
 * Created by Leoš on 23.08.2016.
 */
public class DressUpActivity extends LeliFragmentActivity implements DressListFragment.FragmentSwitcher {
    private DressFigureFragment dressFigureFragment;
    private Fragment currentFragment;
    private TextView balanceTextView;

    public DressUpActivity() {
        super();
        withToolbar = true;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        balanceTextView = new TextView(this);
        //noinspection deprecation
        balanceTextView.setTextColor(getResources().getColor(R.color.colorTitleText));
        balanceTextView.setPadding(5, 0, 5, 0);
//        balanceTextView.setTypeface(null, Typeface.BOLD);
        balanceTextView.setTextSize(14);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Gravity.CENTER_HORIZONTAL);
        toolbar.addView(balanceTextView, layoutParams);
    }

    @Override
    protected Object getFragmentToLoad(Object oldFragment) {
        if (oldFragment == null) {
            if (dressFigureFragment != null) {
                currentFragment = dressFigureFragment;
            } else {
                currentFragment = new DressListFragment();
            }
        } else {
            currentFragment = (Fragment) oldFragment;
        }
        return currentFragment;
    }

    @Override
    public void startDressingFragment(String figureDefinition) {
        dressFigureFragment = new DressFigureFragment();
        dressFigureFragment.setBalanceView(balanceTextView);
        dressFigureFragment.setFigurePath(figureDefinition);
        loadFragment(null);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof DressFigureFragment) {
            dressFigureFragment = null;
            loadFragment(null);
        } else {
            super.onBackPressed();
        }
    }

    public static void start(Context c) {
        Intent intent = new Intent(c, DressUpActivity.class);
        c.startActivity(intent);
    }
}
