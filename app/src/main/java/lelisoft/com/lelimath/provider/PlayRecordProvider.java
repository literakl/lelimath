package lelisoft.com.lelimath.provider;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.TimePeriod;

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
            Crashlytics.logException(e);
            log.error("Unable to create PlayRecordProvider instance", e);
        }
    }

    public int create(PlayRecord playRecord) {
        try {
            return dao.create(playRecord);
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Unable to create new PlayRecord in database. playRecord=" + playRecord, e);
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public Dao.CreateOrUpdateStatus createOrUpdate(PlayRecord playRecord) {
        try {
            return dao.createOrUpdate(playRecord);
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Unable to create new PlayRecord in database. playRecord=" + playRecord, e);
            return null;
        }
    }

    @SuppressWarnings("unused")
    public List<PlayRecord> getAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            Crashlytics.logException(e);
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
            Crashlytics.logException(e);
            log.error("Unable to get all PlayRecords from database.", e);
            return null;
        }
    }

    @SuppressWarnings("unused")
    public PlayRecord getById(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Unable to get PlayRecord by id from database. playRecordId=" + id, e);
            return null;
        }
    }

    @SuppressWarnings("unused")
    public List<PlayRecord> getPlayRecords(Play play) {
        try {
            return dao.queryBuilder().where().eq("play_id", play.getId()).query();
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Unable to get PlayRecords for Play from database. play_id=" + play.getId(), e);
            return null;
        }
    }

    @SuppressWarnings("unused")
    public QueryBuilder<PlayRecord, Integer> queryBuilder() {
        return dao.queryBuilder();
    }

    @SuppressWarnings("unused")
    public List<PlayRecord> query(PreparedQuery<PlayRecord> query) {
        try {
            return dao.query(query);
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Cannot execute prepared query: " + query, e);
            return null;
        }
    }

    @SuppressWarnings("unused")
    public long getPlayRecordsCount() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Cannot get count of all PlayRecords stored in DB.", e);
            return -1;
        }
    }

    public long getLastPlayRecordDate() {
        try {
            QueryBuilder<PlayRecord, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("id", false);
            PreparedQuery<PlayRecord> query = queryBuilder.prepare();
            PlayRecord record = dao.queryForFirst(query);
            if (record == null) {
                return 0;
            } else {
                return record.getDate().getTime();
            }
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Cannot get last play record date.", e);
            return 0;
        }
    }

    /**
     * Returns a sum of points in PlayRecords grouped by given time unit in selected period.
     * It returns null in case of error.
     * @param since maximum date
     * @param unit time unit used for grouping and for limiting data
     * @param countBack number of time units to be returned preceding <code>since</code> date
     * @return list of string array
     */
    public List<String[]> getPlayRecordsPointSums(long since, TimePeriod unit, int countBack) {
        return queryPlayRecords("SUM(points)", since, countBack, unit);
    }

    @SuppressWarnings("unused")
    public List<String[]> getPlayRecordsCounts(long since, TimePeriod unit, int countBack) {
        return queryPlayRecords("COUNT(*)", since, countBack, unit);
    }

    protected List<String[]> queryPlayRecords(String column, long since, int countBack, TimePeriod unit) {
        try {
            log.debug("queryPlayRecords({},{},{})", column, countBack, unit);
            String timeExpression;
            switch (unit) {
                case DAY:
                    timeExpression = "'%Y-%m-%d'"; break;
                case WEEK:
                    timeExpression = "'%Y-%W'"; break;
                case MONTH:
                    timeExpression = "'%Y-%m'"; break;
                default:
                    throw new IllegalArgumentException("Unit " + unit + " is not supported!");
            }
            String sql = "SELECT strftime(" + timeExpression + ", date), " + column +
                    " FROM play_record WHERE date >= ? AND date <= ?" +
                    " GROUP BY strftime(" + timeExpression + ", date) ORDER BY 1 ASC";

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(since);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String upToStr = format.format(calendar.getTime());

            switch (unit) {
                case DAY:
                    calendar.add(Calendar.DAY_OF_YEAR, -1 * countBack); break;
                case WEEK:
                    calendar.add(Calendar.WEEK_OF_YEAR, -1 * countBack); break;
                case MONTH:
                    calendar.add(Calendar.MONTH, -1 * countBack); break;
            }
            String sinceStr = format.format(calendar.getTime());

            GenericRawResults<String[]> results = dao.queryRaw(sql, sinceStr, upToStr);
            log.debug("queryPlayRecords() finished");
            return results.getResults();
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Cannot query database for ", e);
            return null;
        }
    }

    /**
     * @return sum of points gathered during the last play
     */
    public int getLastPlayPoints() {
        try {
            String sql = "select sum(points) from play_record where play_id = (select max(id) from play order by id desc)";
            GenericRawResults<String[]> results = dao.queryRaw(sql);
            return Integer.parseInt(results.getFirstResult()[0]);
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Cannot get last play points.", e);
            return 0;
        }
    }

    /**
     * @return sum of all points
     */
    public int getPoints() {
        try {
            String sql = "select coalesce(sum(points), 0) from play_record";
            GenericRawResults<String[]> results = dao.queryRaw(sql);
            String[] result = results.getFirstResult();
            return Integer.parseInt(result[0]);
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Cannot get points.", e);
            return 0;
        }
    }
}
