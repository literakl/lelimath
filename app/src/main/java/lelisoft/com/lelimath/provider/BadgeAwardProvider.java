package lelisoft.com.lelimath.provider;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;

/**
 * Database provider for badge awards.
 * Created by Leoš on 19.05.2016.
 */
public class BadgeAwardProvider {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BadgeAwardProvider.class);
    Dao<BadgeAward, Integer> dao;

    public BadgeAwardProvider(Context ctx) {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
            dao = helper.getBadgeAwardDao();
        } catch (SQLException e) {
            log.error("Unable to create PlayProvider instance", e);
        }
    }

    public int create(BadgeAward award) {
        try {
            return dao.create(award);
        } catch (SQLException e) {
            log.error("Unable to create new Play in database. award=" + award, e);
        }
        return 0;
    }

    public Dao.CreateOrUpdateStatus createOrUpdate(BadgeAward award) {
        try {
            return dao.createOrUpdate(award);
        } catch (SQLException e) {
            log.error("Unable to create new Play in database. award=" + award, e);
        }
        return null;
    }

    public BadgeAward getById(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            log.error("Unable to get BadgeAward by id from database. id=" + id, e);
        }
        return null;
    }

    public List<BadgeAward> query(PreparedQuery<BadgeAward> query) {
        try {
            return dao.query(query);
        } catch (SQLException e) {
            log.error("Cannot execute prepared query: " + query, e);
        }
        return null;
    }

    public QueryBuilder<BadgeAward, Integer> queryBuilder() {
        return dao.queryBuilder();
    }

    public Map<Badge, BadgeAward> getAll() {
        try {
            List<BadgeAward> list = dao.queryForAll();
            if (list == null || list.isEmpty()) {
                return Collections.emptyMap();
            }

            Map<Badge, BadgeAward> badges = new HashMap<>(list.size());
            for (BadgeAward badgeAward : list) {
                badges.put(badgeAward.getBadge(), badgeAward);
            }
            return badges;
        } catch (SQLException e) {
            log.error("Unable to get all BadgeAwards from database.", e);
            return null;
        }
    }

    public List<BadgeAward> getAwards(Badge badge) {
        try {
            return dao.queryForEq(BadgeAward.BADGE_COLUMN_NAME, badge.name());
        } catch (SQLException e) {
            log.error("Unable to get BadgeAwards for {} from database.", badge.name(), e);
        }
        return null;
    }
}
