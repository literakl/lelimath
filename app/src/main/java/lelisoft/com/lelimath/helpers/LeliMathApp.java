package lelisoft.com.lelimath.helpers;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import io.fabric.sdk.android.Fabric;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.BuildConfig;
import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.activities.GamePreferenceActivity;
import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.provider.PlayRecordProvider;

import static lelisoft.com.lelimath.activities.GamePreferenceActivity.KEY_NEXT_BADGE;

/**
 * Application handler
 * Created by Leoš on 27.02.2016.
 */
public class LeliMathApp extends Application implements Thread.UncaughtExceptionHandler {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LeliMathApp.class);

    public static Resources resources;
    private static LeliMathApp instance;
    private SoundPool mShortPlayer = null;
    private Map<Integer, Integer> mSounds = new HashMap<>();
    private boolean soundEnabled;
    private float volume;
    private long lastFormulaDate;
    Map<Badge, List<BadgeAward>> badges;
    private Thread.UncaughtExceptionHandler androidExceptionHandler;
    public User currentUser;

    @Override
    public void onCreate() {
        log.debug("onCreate()");
        super.onCreate();
        instance = this;

        androidExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

//        insideFirebase = Settings.System.getString(getContentResolver(), "firebase.test.lab") != null;
//        https://docs.fabric.io/android/crashlytics/build-tools.html
        if (! BuildConfig.DEBUG) {
            CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
            Fabric.with(this, new Crashlytics.Builder().core(core).build());
        }

        resources = getResources();
        performUpgrade();
        setDefaultPreferences();

        // it may go to AsyncTask to save some 100 ms but there is potentional synchronization issue
        setLastFormulaDate();
        BadgeAwardProvider provider = new BadgeAwardProvider(this);
        badges = provider.getAll();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPref.getBoolean(GamePreferenceActivity.KEY_SOUND_ENABLED, true);
        setVolume(sharedPref.getInt(GamePreferenceActivity.KEY_SOUND_LEVEL, 50));
        toggleSound(soundEnabled);
        log.debug("onCreate() finished");
    }

    public static LeliMathApp getInstance() {
        return instance;
    }

    public void playSound(int resourceId) {
        if (soundEnabled) {
            Integer soundId = mSounds.get(resourceId);
            if (soundId == null) {
                log.warn("Failed to get sound {}!", resourceId);
                return;
            }
            mShortPlayer.play(soundId, volume, volume, 0, 0, 1);
        }
    }

    private void performUpgrade() {
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            log.info("Started version " + packageInfo.versionCode);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int version = preferences.getInt(GamePreferenceActivity.KEY_CURRENT_VERSION, 0);
            if (version < packageInfo.versionCode) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(GamePreferenceActivity.KEY_CURRENT_VERSION, packageInfo.versionCode);
                editor.apply();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Crashlytics.logException(e);
            log.warn("Package search failed!", e);
        }
    }

    @SuppressWarnings("unused")
    public synchronized User getCurrentUser() {
        if (currentUser == null) {
            try {
                Dao<User, Integer> dao = getDatabaseHelper().getUserDao();
                currentUser = dao.queryForId(1);
            } catch (SQLException e) {
                log.error("Cannot load user!", e);
            }
        }
        return currentUser;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            log.error("UncaughtExceptionHandler just caught " + ex, ex);
        } catch (Throwable ignored) {
            //..avoid infinite loop
        }

        androidExceptionHandler.uncaughtException(thread, ex);
    }

    /**
     * @return shared database helper instance
     */
    public DatabaseHelper getDatabaseHelper() {
        return OpenHelperManager.getHelper(this, DatabaseHelper.class);
    }

    protected void setDefaultPreferences() {
        log.debug("Loading default values from XML");
        PreferenceManager.setDefaultValues(LeliMathApp.this, R.xml.game_prefs, false);
        log.debug("Defaults are loaded");
    }

    public void toggleSound(boolean state) {
        log.debug("Toggle sound support({})", state);
        if (! state && mShortPlayer != null) {
            mShortPlayer.release();
            mShortPlayer = null;
            mSounds.clear();
        } else if (state && mShortPlayer == null) {
            if (Build.VERSION.SDK_INT >= 23) {
                mShortPlayer = new SoundPool.Builder().build();
            } else {
                //noinspection deprecation
                mShortPlayer = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            }
            mSounds.put(R.raw.correct, this.mShortPlayer.load(this, R.raw.correct, 1));
            mSounds.put(R.raw.incorrect, this.mShortPlayer.load(this, R.raw.incorrect, 1));
            mSounds.put(R.raw.victory, this.mShortPlayer.load(this, R.raw.victory, 1));
        }
    }

    public void setVolume(int volume) {
        log.debug("Set sound volume({})", volume);
        this.volume = volume / 100f;
    }

    /**
     * @return date when last formula was finished
     */
    public long getLastFormulaDate() {
        return lastFormulaDate;
    }

    /**
     * Set a date in milliseconds when last formula was finished
     * @param lastFormulaDate date
     */
    public void setLastFormulaDate(long lastFormulaDate) {
        this.lastFormulaDate = lastFormulaDate;
    }

    /**
     * Set a date in milliseconds when last formula was finished
     */
    public void setLastFormulaDate() {
        log.debug("setLastFormulaDate()");
        PlayRecordProvider provider = new PlayRecordProvider(this);
        this.lastFormulaDate = provider.getLastPlayRecordDate();
        log.debug("setLastFormulaDate() finished");
    }

    public Map<Badge, List<BadgeAward>> getBadges() {
        return Collections.unmodifiableMap(badges);
    }

    public void addBadgeAward(BadgeAward badgeAward) {
        List<BadgeAward> previous = badges.get(badgeAward.getBadge());
        if (previous != null) {
            List<BadgeAward> list = previous;
            if (previous.size() == 1) {
                list = new ArrayList<>(3);
                list.add(previous.get(0));
                badges.put(badgeAward.getBadge(), list);
            }
            list.add(badgeAward);
        } else {
            badges.put(badgeAward.getBadge(), Collections.singletonList(badgeAward));
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sValues = sharedPref.getString(KEY_NEXT_BADGE, null);
        if (badgeAward.getBadge().name().equals(sValues)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(KEY_NEXT_BADGE);
            editor.apply();
        }
    }
}
