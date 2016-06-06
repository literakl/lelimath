package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;

import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.logic.GameLogic;
import lelisoft.com.lelimath.provider.DatabaseHelper;

/**
 * Database aware fragment that inherits from Android support library.
 * Created by Leoš on 30.04.2016.
 */
public class LeliBaseFragment extends Fragment {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LeliBaseFragment.class);

    /**
     * Maximum spent time to save - user could leave the mobile device and we do not want to destroy statistics of such peaks
     */
    private static final int MAX_SPENT_TIME = 10000;

    private volatile DatabaseHelper helper;
    private volatile boolean created = false;
    private volatile boolean destroyed = false;
    long started, stopped;

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

    protected void startRecordingSpentTime() {
        if (started == 0) { started = System.currentTimeMillis(); }
    }

    /**
     * Calculates time a user spent on solving this task
     * @param formula formula record to be updated
     */
    protected void updateSpentTime(PlayRecord formula) {
        long now = System.currentTimeMillis();
        long spent = now - started;
        if (spent > MAX_SPENT_TIME) {
            spent = MAX_SPENT_TIME;
        }
        formula.setTimeSpent(spent);
        started = now;
    }

    public void saveKeyMetricGameStarted(Game game, GameLogic.Level level) {
        Answers.getInstance().logLevelStart(new LevelStartEvent().putLevelName(game.name())
                .putCustomAttribute("level", level.name()));
    }

    public void saveKeyMetricGameFinished(Game game, GameLogic.Level level) {
        Answers.getInstance().logLevelEnd(new LevelEndEvent().putLevelName(game.name())
                .putCustomAttribute("level", level.name()));
    }

    /**
     * Get a connection source for this action.
     */
    public ConnectionSource getConnectionSource() {
        return getHelper().getConnectionSource();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (helper == null) {
            helper = getHelperInternal(getActivity());
            created = true;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
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
}
