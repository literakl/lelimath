package lelisoft.com.lelimath.helpers;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import io.fabric.sdk.android.Fabric;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import lelisoft.com.lelimath.BuildConfig;
import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.activities.GamePreferenceActivity;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.provider.DatabaseHelper;

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

    private Thread.UncaughtExceptionHandler androidExceptionHandler;
    public User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        configureLogbackDirectly();
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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPref.getBoolean(GamePreferenceActivity.KEY_SOUND_ENABLED, true);
        setVolume(sharedPref.getInt(GamePreferenceActivity.KEY_SOUND_LEVEL, 50));
        toggleSound(soundEnabled);
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
            SharedPreferences.Editor editor = preferences.edit();
            int version = preferences.getInt(GamePreferenceActivity.KEY_CURRENT_VERSION, 0);
            if (version < packageInfo.versionCode) {
                editor.putInt(GamePreferenceActivity.KEY_CURRENT_VERSION, packageInfo.versionCode);
                editor.apply();
            }
            if (version == 0) {
                return; // new installation
            }

            if (version == 180) {
                boolean enabled = preferences.getBoolean("pref_sound", true);
                editor.putBoolean(GamePreferenceActivity.KEY_SOUND_ENABLED, enabled);
            }
        } catch (PackageManager.NameNotFoundException e) {
            log.warn("Package search failed!", e);
        }
    }

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

    private void configureLogbackDirectly() {
//        new File("/data/data/lelisoft.com.lelimath/files/log/").mkdirs();

        // reset the default context (which may already have been initialized) since we want to reconfigure it
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        lc.reset();

        // setup FileAppender
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(lc);
        encoder1.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder1.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(lc);
        fileAppender.setFile(new File(getExternalFilesDir(null), "app.log").getAbsolutePath());
        fileAppender.setEncoder(encoder1);
        fileAppender.start();

        // setup LogcatAppender
        PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
        encoder2.setContext(lc);
        encoder2.setPattern("%msg%n");
        encoder2.start();

        PatternLayoutEncoder encoder3 = new PatternLayoutEncoder();
        encoder3.setContext(lc);
        encoder3.setPattern("%logger{15}");
        encoder3.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setTagEncoder(encoder3);
        logcatAppender.setEncoder(encoder2);
        logcatAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//        root.setLevel(Level.TRACE);
        root.addAppender(fileAppender);
        root.addAppender(logcatAppender);
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

    // todo remove
    private class FeedPreferencesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void[] params) {
            LeliMathApp.this.setDefaultPreferences();
            return null;
        }
    }
}
