package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;

import lelisoft.com.lelimath.provider.DatabaseHelper;

/**
 * Provides database access. Based on OrmLiteBaseActivity.
 * Created by Leoš on 30.04.2016.
 */
public class LeliBaseActivity extends AppCompatActivity {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LeliBaseActivity.class);

    private volatile DatabaseHelper helper;
    private volatile boolean created = false;
    private volatile boolean destroyed = false;

    /**
     * Get a helper for this action.
     */
    public DatabaseHelper getHelper() {
        if (helper == null) {
            if (!created) {
                throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
            } else if (destroyed) {
                throw new IllegalStateException("A call to onDestroy has already been made and the helper cannot be used after that point");
            } else {
                throw new IllegalStateException("Helper is null for some unknown reason");
            }
        } else {
            return helper;
        }
    }

    /**
     * Get a connection source for this action.
     */
    public ConnectionSource getConnectionSource() {
        return getHelper().getConnectionSource();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (helper == null) {
            helper = getHelperInternal(this);
            created = true;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseHelper(helper);
        destroyed = true;
    }

    /**
     * This is called internally by the class to populate the helper object instance. This should not be called directly
     * by client code unless you know what you are doing. Use {@link #getHelper()} to get a helper instance. If you are
     * managing your own helper creation, override this method to supply this activity with a helper instance.
     */
    protected DatabaseHelper getHelperInternal(Context context) {
        DatabaseHelper newHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        log.trace("{}: got new helper {} from OpenHelperManager", this, newHelper);
        return newHelper;
    }

    /**
     * Release the helper instance created in {@link #getHelperInternal(Context)}. You most likely will not need to call
     * this directly since {@link #onDestroy()} does it for you.
     *
     * <p>
     * <b> NOTE: </b> If you override this method, you most likely will need to override the
     * {@link #getHelperInternal(Context)} method as well.
     * </p>
     */
    protected void releaseHelper(DatabaseHelper helper) {
        OpenHelperManager.releaseHelper();
        log.trace("{}: helper {} was released, set to null", this, helper);
        this.helper = null;
    }

    @Override
    protected void onStart() {
        log.debug("onStart()");
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        log.debug("onSaveInstanceState()");
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onPause() {
        log.debug("onPause()");
        super.onPause();

    }

    @Override
    protected void onResume() {
        log.debug("onResume()");
        super.onResume();
    }

    @Override
    protected void onStop() {
        log.debug("onStop()");
        super.onStop();
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        log.debug("onRestoreInstanceState()");
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onRestart() {
        log.debug("onRestart()");
        super.onRestart();
    }
}
