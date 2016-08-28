package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import lelisoft.com.lelimath.fragment.DressFigureFragment;

/**
 * Display list of figures, their current dress and alow user to buy new items.
 * Created by Leoš on 23.08.2016.
 */
public class DressUpActivity extends LeliFragmentActivity {
    private Fragment currentFragment;

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
