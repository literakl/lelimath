package lelisoft.com.lelimath.helpers;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
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

    private Thread.UncaughtExceptionHandler androidExceptionHandler;
    public User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        new File("/data/data/lelisoft.com.lelimath/files/log/").mkdirs();
        configureLogbackDirectly();
        androidExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        resources = getResources();
        performUpgrade();
        new FeedPreferencesTask().execute();
    }

    public static LeliMathApp getInstance() {
        return instance;
    }

    private void performUpgrade() {
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            log.info("Started version " + packageInfo.versionCode);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            int version = preferences.getInt(GamePreferenceActivity.KEY_CURRENT_VERSION, 0);
            if (version == 0 || version < packageInfo.versionCode) {
                editor.putInt(GamePreferenceActivity.KEY_CURRENT_VERSION, packageInfo.versionCode);
                editor.apply();
                return; // new installation
            }

            if (version <= 141) {
                log.info("Removing deprecated preferences from version 1.4.1 and older");
                editor.remove("pref_game_operations");
                editor.remove("pref_game_first_operand");
                editor.remove("pref_game_second_operand");
                editor.remove("pref_game_result");
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

    private class FeedPreferencesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void[] params) {
            log.debug("Loading default values from XML");
            PreferenceManager.setDefaultValues(LeliMathApp.this, R.xml.game_prefs, false);
            log.debug("Defaults are loaded");
            return null;
        }
    }
}
