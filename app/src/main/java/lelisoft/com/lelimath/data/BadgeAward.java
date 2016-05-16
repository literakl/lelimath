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

    @DatabaseField(canBeNull = false, foreign = true, columnName="user_id")
    User user;

    @DatabaseField(canBeNull = false)
    Date date;

    @DatabaseField(persisted = false)
    Badge badge;

    @DatabaseField(canBeNull=false, columnName="badge")
    String badgeStr;

    @DatabaseField(canBeNull=false, columnName="type", width = 1)
    String type;

}
