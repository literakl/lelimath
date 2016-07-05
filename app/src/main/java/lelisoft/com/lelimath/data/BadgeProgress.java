package lelisoft.com.lelimath.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Badge progress state.
 * Created by Leoš on 30.06.2016.
 */
@DatabaseTable(tableName = "badge_progress")
public class BadgeProgress {

    @DatabaseField(persisted = false)
    Badge badge;

    public static final String BADGE_COLUMN_NAME = "badge";
    @DatabaseField(canBeNull=false, columnName=BADGE_COLUMN_NAME, id = true)
    String badgeStr;

    public static final String USER_COLUMN_NAME = "user_id";
    @DatabaseField(canBeNull = false, foreign = true, columnName=USER_COLUMN_NAME)
    User user;

    @DatabaseField(canBeNull = false)
    Date date;

    public static final String PROGRESS_COLUMN_NAME = "progress";
    @DatabaseField(canBeNull = false, columnName=PROGRESS_COLUMN_NAME)
    int progress;

    public static final String REQUIRED_COLUMN_NAME = "required";
    @DatabaseField(canBeNull = false, columnName=REQUIRED_COLUMN_NAME)
    int required;

    public static final String IN_PROGRESS_COLUMN_NAME = "in_progress";
    @DatabaseField(canBeNull = false, columnName=IN_PROGRESS_COLUMN_NAME)
    boolean inProgress;

    public BadgeProgress() {
    }

    public BadgeProgress(Badge badge, boolean inProgress, int progress, int required) {
        this.progress = progress;
        this.required = required;
        setBadge(badge);
        this.inProgress = inProgress;
        date = new Date();
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }
}
