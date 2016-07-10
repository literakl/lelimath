package lelisoft.com.lelimath.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;

import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeEvaluation;
import lelisoft.com.lelimath.data.BadgeProgress;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.User;

/**
 * Manages database interactions. It is supposed to work as singleton - use application context.
 * Used blog https://horaceheaven.com/android-ormlite-tutorial/ and http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html
 * Created by Leoš on 29.04.2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final String DEFAULT_DATABASE_NAME = "lelimath.sqlite";
    private static final int DATABASE_VERSION = 5;
    private static File path;
    private static String databaseName = DEFAULT_DATABASE_NAME;

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, DATABASE_VERSION);
        path = context.getDatabasePath(databaseName);
    }

    // TODO To set the logger to a particular type, set the system property ("com.j256.ormlite.logger.type")
    // contained in LOG_TYPE_SYSTEM_PROPERTY to be one of the values in LogType enum.

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        log.info("Creating new database version " + DATABASE_VERSION);
        try {
            TableUtils.createTable(connectionSource, User.class);
            getUserDao().create(new User());
            TableUtils.createTable(connectionSource, Play.class);
            TableUtils.createTable(connectionSource, PlayRecord.class);
            TableUtils.createTable(connectionSource, BadgeAward.class);
            TableUtils.createTable(connectionSource, BadgeEvaluation.class);
            TableUtils.createTable(connectionSource, BadgeProgress.class);
        } catch (SQLException e) {
            log.error("Error creating new database!", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        log.info("Upgrading database from version {} to {}", oldVersion, newVersion);
        if (oldVersion == 3) {
            try {
                // drop column badge_eval.progress - unsupported feature
                TableUtils.createTable(connectionSource, BadgeProgress.class);
            } catch (SQLException e) {
                log.error("Error upgrading a database from version 3!", e);
            }
        }

        if (oldVersion == 4) {
            try {
                Dao<PlayRecord, Integer> dao = getPlayRecordDao();
                dao.executeRaw("ALTER TABLE `play_record` ADD COLUMN points INTEGER NOT NULL DEFAULT 0");
                dao.updateRaw("UPDATE `play_record` SET points = length(first || second || result) - 2 WHERE correct > 0");
            } catch (SQLException e) {
                log.error("Error upgrading a database from version 3!", e);
            }
        }
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        return getDao(User.class);
    }

    public Dao<PlayRecord, Integer> getPlayRecordDao() throws SQLException {
        return getDao(PlayRecord.class);
    }

    public Dao<Play, Integer> getPlayDao() throws SQLException {
        return getDao(Play.class);
    }

    public Dao<BadgeAward, Integer> getBadgeAwardDao() throws SQLException {
        return getDao(BadgeAward.class);
    }

    public Dao<BadgeEvaluation, Integer> getBadgeEvaluationDao() throws SQLException {
        return getDao(BadgeEvaluation.class);
    }

    public Dao<BadgeProgress, String> getBadgeProgressDao() throws SQLException {
        return getDao(BadgeProgress.class);
    }

    public static File getDatabasePath() {
        return path;
    }

    public static void setDatabaseName(String databaseName) {
        DatabaseHelper.databaseName = databaseName;
    }
}
