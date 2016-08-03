package lelisoft.com.lelimath.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeEvaluation;
import lelisoft.com.lelimath.data.BadgeProgress;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Manages database interactions. It is supposed to work as singleton - use application context.
 * Used blog https://horaceheaven.com/android-ormlite-tutorial/ and http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html
 * Created by Leoš on 29.04.2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final String DEFAULT_DATABASE_NAME = "lelimath.sqlite";
    private static final int DATABASE_VERSION = 6;
    private static File path;
    private static String databaseName = DEFAULT_DATABASE_NAME;

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, DATABASE_VERSION);
        path = context.getDatabasePath(databaseName);
//        recoverDatabase();
    }

    // TODO To set the logger to a particular type, set the system property ("com.j256.ormlite.logger.type")
    // contained in LOG_TYPE_SYSTEM_PROPERTY to be one of the values in LogType enum.

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        log.info("Creating new database version {}", DATABASE_VERSION);
        try {
            TableUtils.createTable(connectionSource, User.class);
            getUserDao().create(new User());
            TableUtils.createTable(connectionSource, Play.class);
            TableUtils.createTable(connectionSource, PlayRecord.class);
            TableUtils.createTable(connectionSource, BadgeAward.class);
            TableUtils.createTable(connectionSource, BadgeEvaluation.class);
            TableUtils.createTable(connectionSource, BadgeProgress.class);
        } catch (SQLException e) {
            Crashlytics.logException(e);
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
                oldVersion = 4;
            } catch (SQLException e) {
                Crashlytics.logException(e);
                log.error("Error upgrading a database from version 3!", e);
                throw new RuntimeException("Error upgrading a database from version 3!");
            }
        }

        if (oldVersion == 4) {
            try {
                Dao<PlayRecord, Integer> dao = getPlayRecordDao();
                dao.executeRaw("ALTER TABLE `play_record` ADD COLUMN points INTEGER NOT NULL DEFAULT 0");
                dao.updateRaw("UPDATE `play_record` SET points = length(first || second || result) - 2 WHERE correct > 0");
                oldVersion = 5;
            } catch (SQLException e) {
                Crashlytics.logException(e);
                log.error("Error upgrading a database from version 4!", e);
                throw new RuntimeException("Error upgrading a database from version 4!");
            }
        }

        if (oldVersion == 5) {
            try {
                backupDatabase();
                dropColumns(database, connectionSource, "play", new String[]{"user_id"}, Play.class);
                dropColumns(database, connectionSource, "play_record", new String[]{"user_id"}, PlayRecord.class);
                dropColumns(database, connectionSource, "badge_eval", new String[]{"user_id", "progress"}, BadgeEvaluation.class);
                dropColumns(database, connectionSource, "badge_award", new String[]{"user_id"}, BadgeAward.class);
                dropColumns(database, connectionSource, "badge_progress", new String[]{"user_id"}, BadgeProgress.class);
            } catch (SQLException e) {
                Crashlytics.logException(e);
                log.error("Error upgrading a database from version 5!", e);
                throw new RuntimeException("Error upgrading a database from version 5!");
            }
        }

        log.info("Database upgrade finished");
    }

    // http://stackoverflow.com/questions/8442147/how-to-delete-or-add-column-in-sqlite

    private void dropColumns(SQLiteDatabase db,  ConnectionSource connectionSource,
                            String tableName, String[] colsToRemove, Class aClass) throws java.sql.SQLException {
        log.info("Droping columns {} from table {}", colsToRemove, tableName);

        String backupTable = tableName + "_old";
        List<String> updatedTableColumns = getTableColumns(db, tableName);
        // Remove the columns we don't want anymore from the table's list of columns
        updatedTableColumns.removeAll(Arrays.asList(colsToRemove));
        String columnsSeperated = TextUtils.join(",", updatedTableColumns);

        db.beginTransaction();
        try {
            db.execSQL("PRAGMA foreign_keys=off");
            db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + backupTable);
            log.info("Table {} renamed to {}", tableName, backupTable);
            TableUtils.createTable(connectionSource, aClass);

            // Populating the table with the data
            String sql = "INSERT INTO " + tableName + "(" + columnsSeperated + ") " +
                    "SELECT " + columnsSeperated + " FROM " + backupTable;
            log.info("Migrating data using query {}", sql);
            db.execSQL(sql);
            log.info("Data copied from {} to {}", backupTable, tableName);
            db.execSQL("DROP TABLE " + backupTable);
            log.info("Backup table {} dropped", backupTable);
            db.execSQL("PRAGMA foreign_keys=on");
            db.setTransactionSuccessful();

            log.info("Columns successfully dropped");
        } finally {
            db.endTransaction();
        }
    }

    private List<String> getTableColumns(SQLiteDatabase db, String tableName) {
        ArrayList<String> columns = new ArrayList<>();
        String cmd = "PRAGMA table_info(" + tableName + ");";
        Cursor cursor = db.rawQuery(cmd, null);
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(cursor.getColumnIndex("name")));
        }
        cursor.close();

        return columns;
    }

    @SuppressLint("SetWorldReadable")
    private static void backupDatabase() {
        File in = getDatabasePath();
        String timestamp = new SimpleDateFormat("yyMMdd_HHmm").format(new Date());
        File out = new File(Environment.getExternalStorageDirectory(), timestamp + "_" + in.getName());
        //noinspection ResultOfMethodCallIgnored
        out.setReadable(true, false);
        log.debug("Copying database {} to SD card at {}", in.getName(), out.getAbsolutePath());
        boolean result = Misc.copyFile(in, out);
        log.debug("finished {}", result ? "successfully" : "unsuccessfully");
    }

    @SuppressWarnings("unused")
    private void recoverDatabase() {
        File out = getDatabasePath();
        File in = new File(Environment.getExternalStorageDirectory(), databaseName);
        log.debug("Copying database {} from SD card at {}", in.getName(), out.getAbsolutePath());
        boolean result = Misc.copyFile(in, out);
        log.debug("Recover finished with {}", result);
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
