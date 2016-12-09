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
    @DatabaseField(generatedId = true, columnName = Columns.ID)
    Integer id;

    @DatabaseField(canBeNull = false)
    Date date;

    @DatabaseField(persisted = false)
    Badge badge;

    @DatabaseField(canBeNull=false, columnName= Columns.BADGE)
    String badgeStr;

    @DatabaseField(canBeNull=true, columnName=Columns.LAST_AWARDED_ID)
    Integer lastAwardedId;

    @DatabaseField(canBeNull=true, columnName=Columns.LAST_WRONG_ID)
    Integer lastWrongId;

    @DatabaseField(canBeNull=true, columnName=Columns.LAST_WRONG_DATE)
    Date lastWrongDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
