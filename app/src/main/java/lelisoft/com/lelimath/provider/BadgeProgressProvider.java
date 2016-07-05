package lelisoft.com.lelimath.provider;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeProgress;

/**
 * Database provider for badge progress.
 * Created by Leoš on 01.07.2016.
 */
public class BadgeProgressProvider {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BadgeProgressProvider.class);
    Dao<BadgeProgress, String> dao;

    public BadgeProgressProvider(Context ctx) {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
            dao = helper.getBadgeProgressDao();
        } catch (SQLException e) {
            log.error("Unable to create PlayProvider instance", e);
        }
    }

    public int create(BadgeProgress progress) {
        try {
            return dao.create(progress);
        } catch (SQLException e) {
            log.error("Unable to create new BadgeProgress in database. progress=" + progress, e);
        }
        return 0;
    }

    public Dao.CreateOrUpdateStatus createOrUpdate(BadgeProgress progress) {
        try {
            return dao.createOrUpdate(progress);
        } catch (SQLException e) {
            log.error("Unable to create new BadgeProgress in database. progress=" + progress, e);
        }
        return null;
    }

    public BadgeProgress getById(Badge badge) {
        try {
            return dao.queryForId(badge.name());
        } catch (SQLException e) {
            log.error("Unable to get BadgeProgress by id from database. id=" + badge, e);
        }
        return null;
    }

    public List<BadgeProgress> query(PreparedQuery<BadgeProgress> query) {
        try {
            return dao.query(query);
        } catch (SQLException e) {
            log.error("Cannot execute prepared query: " + query, e);
        }
        return null;
    }

    public QueryBuilder<BadgeProgress, String> queryBuilder() {
        return dao.queryBuilder();
    }
}
