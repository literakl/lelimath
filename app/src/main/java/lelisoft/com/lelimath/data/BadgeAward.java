package lelisoft.com.lelimath.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Register of awarded badges
 * Created by Leoš on 15.05.2016.
 */
@DatabaseTable(tableName = "badge_award")
public class BadgeAward {
    @DatabaseField(generatedId = true)
    Integer id;

    // TODO remove, but sqlite dows not allow to drop a column
    @DatabaseField(canBeNull = true, columnName="user_id", persisted = false)
    User user;

    @DatabaseField(canBeNull = false)
    Date date;

    @DatabaseField(persisted = false)
    Badge badge;

    public static final String BADGE_COLUMN_NAME = "badge";
    @DatabaseField(canBeNull=false, columnName=BADGE_COLUMN_NAME)
    String badgeStr;

    @DatabaseField(canBeNull=false, columnName="type", width = 1)
    String type;

    @DatabaseField(canBeNull=true, columnName="data")
    String data;

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
            type = Character.toString(badge.getType());
        }
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
        badgeStr = badge.name();
        type = Character.toString(badge.getType());
    }

    public String getType() {
        return type;
    }

    /**
     * Holds data for displaying what was used for awarding this badge
     * @return some data, typically comma separated formula record ids
     */
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
