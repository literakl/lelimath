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

    @DatabaseField(canBeNull = false)
    Date date;

    @DatabaseField(persisted = false)
    Badge badge;

    @DatabaseField(canBeNull=false, columnName= Columns.BADGE)
    String badgeStr;

    @DatabaseField(canBeNull=false, columnName=Columns.TYPE, width = 1)
    String type;

    @DatabaseField(canBeNull=true, columnName=Columns.DATA)
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
