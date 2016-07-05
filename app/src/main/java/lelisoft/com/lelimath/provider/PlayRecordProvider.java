package lelisoft.com.lelimath.provider;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;

/**
 * DB provider for play records
 * Created by Leoš on 06.05.2016.
 */
public class PlayRecordProvider {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlayRecordProvider.class);

    Dao<PlayRecord, Integer> dao;

    public PlayRecordProvider(Context ctx) {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
            dao = helper.getPlayRecordDao();
        } catch (SQLException e) {
            log.error("Unable to create PlayRecordProvider instance", e);
        }
    }

    public int create(PlayRecord playRecord) {
        try {
            return dao.create(playRecord);
        } catch (SQLException e) {
            log.error("Unable to create new PlayRecord in database. playRecord=" + playRecord, e);
            return 0;
        }
    }

    public Dao.CreateOrUpdateStatus createOrUpdate(PlayRecord playRecord) {
        try {
            return dao.createOrUpdate(playRecord);
        } catch (SQLException e) {
            log.error("Unable to create new PlayRecord in database. playRecord=" + playRecord, e);
            return null;
        }
    }

    public List<PlayRecord> getAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            log.error("Unable to get all PlayRecords from database.", e);
            return null;
        }
    }

    public List<PlayRecord> getAllInDescendingOrder() {
        try {
            QueryBuilder<PlayRecord, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderByRaw("id DESC");

            PreparedQuery<PlayRecord> query = queryBuilder.prepare();
            return dao.query(query);
        } catch (SQLException e) {
            log.error("Unable to get all PlayRecords from database.", e);
            return null;
        }
    }

    public PlayRecord getById(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            log.error("Unable to get PlayRecord by id from database. playRecordId=" + id, e);
            return null;
        }
    }

    public List<PlayRecord> getPlayRecords(Play play) {
        try {
            return dao.queryBuilder().where().eq("play_id", play.getId()).query();
        } catch (SQLException e) {
            log.error("Unable to get PlayRecords for Play from database. play_id=" + play.getId(), e);
            return null;
        }
    }

    public QueryBuilder<PlayRecord, Integer> queryBuilder() {
        return dao.queryBuilder();
    }

    public List<PlayRecord> query(PreparedQuery<PlayRecord> query) {
        try {
            return dao.query(query);
        } catch (SQLException e) {
            log.error("Cannot execute prepared query: " + query, e);
            return null;
        }
    }

    public long getPlayRecordsCount() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            log.error("Cannot get count of all PlayRecords stored in DB.", e);
            return -1;
        }
    }

    public long getLastPlayRecordDate() {
        try {
            QueryBuilder<PlayRecord, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderByRaw("id DESC");
            PreparedQuery<PlayRecord> query = queryBuilder.prepare();
            PlayRecord record = dao.queryForFirst(query);
            if (record == null) {
                return 0;
            } else {
                return record.getDate().getTime();
            }
        } catch (SQLException e) {
            log.error("Cannot get last play record date.", e);
            return 0;
        }
    }
}
