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

/**
 * Display list of figures, their current dress and alow user to buy new items.
 * Created by Leoš on 23.08.2016.
 */
public class DressUpActivity extends LeliFragmentActivity {
    private Fragment currentFragment;

    public DressUpActivity() {
        super();
        withToolbar = true;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textView = new TextView(this);
        //noinspection deprecation
        textView.setTextColor(getResources().getColor(R.color.colorTitleText));
        textView.setPadding(5, 0, 5, 0);
//        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(14);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Gravity.CENTER_HORIZONTAL);
        toolbar.addView(textView, layoutParams);
        ((DressFigureFragment) currentFragment).setBalanceView(textView);
    }

    @Override
    protected Object getFragmentToLoad(Object oldFragment) {
        if (oldFragment == null) {
            currentFragment = new DressFigureFragment();
        } else {
            currentFragment = (Fragment) oldFragment;
        }
        return currentFragment;
    }

    public static void start(Context c) {
        Intent intent = new Intent(c, DressUpActivity.class);
        c.startActivity(intent);
    }
}
