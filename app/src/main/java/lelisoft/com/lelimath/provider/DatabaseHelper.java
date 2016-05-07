package lelisoft.com.lelimath.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import lelisoft.com.lelimath.data.FormulaRecord;
import lelisoft.com.lelimath.data.User;

/**
 * Manages database interactions. It is supposed to work as singleton - use application context.
 * Used blog https://horaceheaven.com/android-ormlite-tutorial/ and http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html
 * Created by Leoš on 29.04.2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final String DATABASE_NAME = "lelimath";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // TODO To set the logger to a particular type, set the system property ("com.j256.ormlite.logger.type")
    // contained in LOG_TYPE_SYSTEM_PROPERTY to be one of the values in LogType enum.

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        log.info("Creating new database version " + DATABASE_VERSION);
        try {
            TableUtils.createTable(connectionSource, User.class);
            getUserDao().create(new User());
            TableUtils.createTable(connectionSource, FormulaRecord.class);
        } catch (SQLException e) {
            log.error("Error creating new database!", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        log.info("Upgrading existing database " + oldVersion + " version " + newVersion);
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        return getDao(User.class);
    }

    public Dao<FormulaRecord, Integer> getFormulaRecordDao() throws SQLException {
        return getDao(FormulaRecord.class);
    }
}
