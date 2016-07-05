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
    public static final String ID_COLUMN_NAME = "id";
    @DatabaseField(generatedId = true, columnName = ID_COLUMN_NAME)
    Integer id;

    public static final String USER_COLUMN_NAME = "user_id";
    @DatabaseField(canBeNull = false, foreign = true, columnName=USER_COLUMN_NAME)
    User user;

    @DatabaseField(canBeNull = false)
    Date date;

    @DatabaseField(persisted = false)
    Badge badge;

    public static final String BADGE_COLUMN_NAME = "badge";
    @DatabaseField(canBeNull=false, columnName=BADGE_COLUMN_NAME)
    String badgeStr;

    @DatabaseField(canBeNull=true, columnName="last_awarded_id")
    Integer lastAwardedId;

    @DatabaseField(canBeNull=true, columnName="last_wrong_id")
    Integer lastWrongId;

    @DatabaseField(canBeNull=true, columnName="last_wrong_date")
    Date lastWrongDate;

    // this field is unused but sqlite cannot remove it
    @DatabaseField(canBeNull=true, columnName="progress")
    Integer progress;

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
    public Integer getLastAwardedId() {
        return lastAwardedId;
    }

    public void setLastAwardedId(Integer lastAwardedId) {
        this.lastAwardedId = lastAwardedId;
    }

    /**
     * @return id of last formula / play record that breaks evaluator rules for this badge
     */
    public Integer getLastWrongId() {
        return lastWrongId;
    }

    public void setLastWrongId(Integer lastWrongId) {
        this.lastWrongId = lastWrongId;
    }

    /**
     * @return id of last date that breaks evaluator rules for this badge
     */
    public Date getLastWrongDate() {
        return lastWrongDate;
    }

    public void setLastWrongDate(Date lastWrongDate) {
        this.lastWrongDate = lastWrongDate;
    }
}
