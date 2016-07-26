package lelisoft.com.lelimath.provider;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import lelisoft.com.lelimath.data.Play;

/**
 * DB provider for plays
 * Created by Leoš on 13.05.2016.
 */
public class PlayProvider {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlayProvider.class);

    Dao<Play, Integer> dao;

    public PlayProvider(Context ctx) {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
            dao = helper.getPlayDao();
        } catch (SQLException e) {
            log.error("Unable to create PlayProvider instance", e);
        }
    }

    public int create(Play play) {
        try {
            return dao.create(play);
        } catch (SQLException e) {
            log.error("Unable to create new Play in database. play=" + play, e);
        }
        return 0;
    }

    @SuppressWarnings("unused")
    public Dao.CreateOrUpdateStatus createOrUpdate(Play play) {
        try {
            return dao.createOrUpdate(play);
        } catch (SQLException e) {
            log.error("Unable to create new Play in database. play=" + play, e);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public Play getById(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            log.error("Unable to get Play by id from database. playRecordId=" + id, e);
        }
        return null;
    }
    public QueryBuilder<Play, Integer> queryBuilder() {
        return dao.queryBuilder();
    }

    @SuppressWarnings("unused")
    public List<Play> query(PreparedQuery<Play> query) {
        try {
            return dao.query(query);
        } catch (SQLException e) {
            log.error("Cannot execute prepared query: " + query, e);
        }
        return null;
    }
}
