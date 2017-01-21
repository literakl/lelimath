package lelisoft.com.lelimath.provider;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.data.Campaign;
import lelisoft.com.lelimath.data.Columns;
import lelisoft.com.lelimath.data.TestRecord;

/**
 * DB provider for test records
 * Created by Leoš on 20.01.2017.
 */
public class TestRecordProvider {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestRecordProvider.class);

    private static final String SQL_CAMPAIGN_TEST_STATS = "SELECT test_id, score FROM test_record WHERE campaign_id=?";
    private static final String SQL_CAMPAIGNS_STATS = "select campaign_id, count(*), sum(score) from test_record group by campaign_id";

    private Dao<TestRecord, Integer> dao;

    public TestRecordProvider(Context ctx) {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
            dao = helper.getTestRecordDao();
        } catch (SQLException e) {
            log.error("Unable to create TestRecordProvider instance", e);
            Crashlytics.logException(e);
        }
    }

    /**
     * Creates new record and delete any axisting record for the same Test
     * @param testRecord record
     * @return 0
     */
    public int create(TestRecord testRecord) {
        try {
            DeleteBuilder<TestRecord, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq(Columns.CAMPAIGN_ID, testRecord.getCampaignId()).and().eq(Columns.TEST_ID, testRecord.getTestId());
            log.debug("sql {}",deleteBuilder.prepare().getStatement());
            int count = deleteBuilder.delete();
            log.debug("Deleted {} existing test records(s)", count);
            return dao.create(testRecord);
        } catch (SQLException e) {
            log.error("Unable to create new TestRecord in database. testRecord=" + testRecord, e);
            Crashlytics.logException(e);
        }
        return 0;
    }

    @SuppressWarnings("unused")
    public TestRecord getById(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            log.error("Unable to get TestRecord by id from database. testRecordRecordId=" + id, e);
            Crashlytics.logException(e);
        }
        return null;
    }

    /**
     * Finds test scores for all tests within selected campaign
     * @param campaign id
     * @return map of tests id and its score
     */
    @SuppressWarnings("unused")
    public Map<String, Integer> getTestScores(Campaign campaign) {
        Map<String, Integer> map = new HashMap<>();
        log.debug("Started at  {}", System.currentTimeMillis());
        try {
            GenericRawResults<String[]> results = dao.queryRaw(SQL_CAMPAIGN_TEST_STATS, campaign.getId());
            for (String[] result : results) {
                map.put(result[0], Integer.parseInt(result[1]));
            }
        } catch (SQLException e) {
            log.error("Unable to query test scores", e);
            Crashlytics.logException(e);
        }
        log.debug("Finished at {}", System.currentTimeMillis());
        return map;
    }

    /**
     * Calculates score for all campaigns
     * @param campaigns list of campaigns to update
     */
    @SuppressWarnings("unused")
    public void setCampaignsData(List<Campaign> campaigns) {
        Map<String, String[]> map = new HashMap<>();
        log.debug("Started at  {}", System.currentTimeMillis());
        try {
            GenericRawResults<String[]> results = dao.queryRaw(SQL_CAMPAIGNS_STATS);
            for (String[] result : results) {
                map.put(result[0], result);
            }

            for (Campaign campaign : campaigns) {
                String[] result = map.get(campaign.getId());
                if (result != null) {
                    int finished = Integer.parseInt(result[1]);
                    int scoreSum = Integer.parseInt(result[2]);
                    campaign.setFinished(finished);
                    campaign.setScore(scoreSum / finished);
                }
            }
        } catch (SQLException e) {
            log.error("Unable to query test scores", e);
            Crashlytics.logException(e);
        }
        log.debug("Finished at {}", System.currentTimeMillis());
    }

    public QueryBuilder<TestRecord, Integer> queryBuilder() {
        return dao.queryBuilder();
    }

    @SuppressWarnings("unused")
    public List<TestRecord> query(PreparedQuery<TestRecord> query) {
        try {
            return dao.query(query);
        } catch (SQLException e) {
            log.error("Cannot execute prepared query: " + query, e);
            Crashlytics.logException(e);
        }
        return null;
    }
}
