package lelisoft.com.lelimath.provider;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import lelisoft.com.lelimath.data.FormulaRecord;

/**
 * DB provider for formula records
 * Created by Leoš on 06.05.2016.
 */
public class FormulaRecordProvider {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FormulaRecordProvider.class);

    Dao<FormulaRecord, Integer> dao;

    public FormulaRecordProvider(Context ctx) {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
            dao = helper.getFormulaRecordDao();
        } catch (SQLException e) {
            log.error("Unable to create FormulaRecordProvider instance", e);
        }
    }

    public int create(FormulaRecord formulaRecord) {
        try {
            return dao.create(formulaRecord);
        } catch (SQLException e) {
            log.error("Unable to create new FormulaRecord in database. formulaRecord=" + formulaRecord, e);
        }
        return 0;
    }

    public Dao.CreateOrUpdateStatus createOrUpdate(FormulaRecord formulaRecord) {
        try {
            return dao.createOrUpdate(formulaRecord);
        } catch (SQLException e) {
            log.error("Unable to create new FormulaRecord in database. formulaRecord=" + formulaRecord, e);
        }
        return null;
    }

    public List<FormulaRecord> getAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            log.error("Unable to get all FormulaRecords from database.", e);
        }
        return null;
    }

    public FormulaRecord getById(Integer formulaRecordId) {
        try {
            return dao.queryForId(formulaRecordId);
        } catch (SQLException e) {
            log.error("Unable to get FormulaRecord by id from database. formulaRecordId=" + formulaRecordId, e);
        }
        return null;
    }

    public List<FormulaRecord> findForReverse() {
        try {
            QueryBuilder<FormulaRecord, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("reversal", true);

            PreparedQuery<FormulaRecord> query = queryBuilder.prepare();
            return dao.query(query);
        } catch (SQLException e) {
            log.error("Unable to get FormulaRecords for reverse from database", e);
        }
        return null;
    }
    
    public List<FormulaRecord> executeQuery(PreparedQuery<FormulaRecord> query) {
        try {
            return dao.query(query);
        } catch (SQLException e) {
            log.error("Cannot execute prepared query: " + query, e);
        }
        return null;
    }

    public long getFormulaRecordsCount() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            log.error("Cannot get count of all FormulaRecords stored in DB.", e);
        }
        return -1;
    }
}
