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

    @DatabaseField(canBeNull=false, columnName= Columns.BADGE, id = true)
    String badgeStr;

    @DatabaseField(canBeNull = false, columnName = Columns.DATE)
    Date date;

    @DatabaseField(canBeNull = false, columnName= Columns.PROGRESS)
    int progress;

    @DatabaseField(canBeNull = false, columnName= Columns.REQUIRED)
    int required;

    @DatabaseField(canBeNull = false, columnName= Columns.IN_PROGRESS)
    boolean inProgress;

    @SuppressWarnings("unused")
    public BadgeProgress() {
    }

    public BadgeProgress(Badge badge, boolean inProgress, int progress, int required) {
        this.progress = progress;
        this.required = required;
        setBadge(badge);
        this.inProgress = inProgress;
        date = new Date();
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

    /**
     * @return how many formulas it is neccessary to solve to finish this BadgeProgress
     */
    public int calculateRemainingFormulas() {
        if (! inProgress) {
            return 0;
        }

        switch (getBadge()) {
            case PAGE:
                return 1;
            case KNIGHT:
                return 10* (required - progress);
            case PALADIN:
                return 10 * (required - progress);
            case GLADIATOR:
                return 1;
            case VIKING:
                return 20* (required - progress);
            case SAMURAI:
                return 20* (required - progress);

            case RETURNER:
                return 10 * (required - progress);
            case LONG_DISTANCE_RUNNER:
                return 10 * (required - progress);
            case MARATHON_RUNNER:
                return 10 * (required - progress);

            case GOOD_SUMMATION:
            case GREAT_SUMMATION:
            case EXCELLENT_SUMMATION:
            case GOOD_SUBTRACTION:
            case GREAT_SUBTRACTION:
            case EXCELLENT_SUBTRACTION:
            case GOOD_MULTIPLICATION:
            case GREAT_MULTIPLICATION:
            case EXCELLENT_MULTIPLICATION:
            case GOOD_DIVISION:
            case GREAT_DIVISION:
            case EXCELLENT_DIVISION:
                return (required - progress);

            default:
                throw new RuntimeException("Missing code for a badge " + badge);
        }
    }

    @SuppressWarnings("unused")
    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    @Override
    public String toString() {
        return "BadgeProgress{" +
                "badge='" + badgeStr + '\'' +
                ", inProgress=" + inProgress +
                ", " + progress +
                " of " + required +
                '}';
    }
}
