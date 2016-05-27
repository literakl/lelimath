package lelisoft.com.lelimath.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Record of the last badge evaluator task.
 * Created by Leoš on 26.05.2016.
 */
@DatabaseTable(tableName = "badge_eval")
public class BadgeEvaluation {
    @DatabaseField(generatedId = true)
    Integer id;

    @DatabaseField(canBeNull = false, foreign = true, columnName="user_id")
    User user;

    @DatabaseField(canBeNull = false)
    Date date;

    @DatabaseField(persisted = false)
    Badge badge;

    public static final String BADGE_COLUMN_NAME = "badge";
    @DatabaseField(canBeNull=false, columnName=BADGE_COLUMN_NAME)
    String badgeStr;

    @DatabaseField(canBeNull=true, columnName="last_awarded_row")
    Integer lastAwardedRow;

    @DatabaseField(canBeNull=true, columnName="last_wrong_row")
    Integer lastWrongRow;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Badge getBadge() {
        if (badge != null) {
            return badge;
        } else if (badgeStr != null) {
            badge = Badge.valueOf(badgeStr);
        }
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
        badgeStr = badge.name();
    }

    /**
     * @return id of last formula / play record that fit last award of this badge
     */
    public Integer getLastAwardedRow() {
        return lastAwardedRow;
    }

    public void setLastAwardedRow(Integer lastAwardedRow) {
        this.lastAwardedRow = lastAwardedRow;
    }

    /**
     * @return id of last formula / play record that breaks eavluator rules for this badge
     */
    public Integer getLastWrongRow() {
        return lastWrongRow;
    }

    public void setLastWrongRow(Integer lastWrongRow) {
        this.lastWrongRow = lastWrongRow;
    }

}
