package lelisoft.com.lelimath.activities;

import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;

/**
 * This is a parent for fragment based activities
 * Created by Leoš on 20.07.2016.
 */
public abstract class LeliFragmentActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(LeliFragmentActivity.class);

    protected final String FRAGMENT_TAG = "main_fragment";

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_with_fragment);
        if (state == null) {
            loadFragment(null);
        } else {
            loadFragment(getLoadedFragment());
        }
    }

    /**
     * Return fragment to attach into {@code R.id.fragment_container} view.
     * <p>Returned object has to be instance of {@code android.support.v4.app.Fragment} or {@code android.app.Fragment}.
     * <p>If returned object is null or not instance of any Fragment class, nothing is attached to {@code R.id.fragment} view.</p>
     * @param oldFragment previously attached fragment. Return it if it is not null
     * @return fragment you want to attach or null if nothing has to be attached
     */
    protected abstract Object getFragmentToLoad(Object oldFragment);

    protected Object getLoadedFragment() {
        Object o = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (o != null) {
            return o;
        }
        return getFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void loadFragment(Object oldFragment) {
        Object fragment = getFragmentToLoad(oldFragment);
        if (fragment == oldFragment) {
            return;
        }

        if (fragment instanceof android.support.v4.app.Fragment) {
            attachSupportFragment((android.support.v4.app.Fragment) fragment);
        } else if (fragment instanceof android.app.Fragment) {
            attachFragment((android.app.Fragment) fragment);
        }
    }

    private void attachSupportFragment(android.support.v4.app.Fragment fragment) {
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, fragment, FRAGMENT_TAG).
                commit();
    }

    private void attachFragment(android.app.Fragment fragment) {
        fragment.setArguments(getIntent().getExtras());
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, fragment, FRAGMENT_TAG).
                commit();
    }
}
