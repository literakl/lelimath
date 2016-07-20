package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;

import lelisoft.com.lelimath.fragment.ChartsFragment;

/**
 * Shows points chart
 * Created by Leoš on 20.07.2016.
 */
public class PointsChartActivity extends LeliFragmentActivity {

    @Override
    protected Object getFragmentToLoad(Object oldFragment) {
        if (oldFragment == null) {
            return new ChartsFragment();
        }
        return oldFragment;
    }

    public static void start(Context c) {
        Intent intent = new Intent(c, PointsChartActivity.class);
        c.startActivity(intent);
    }
}
