package lelisoft.com.lelimath.logic;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

/**
 * Performs database analysis for certain set of badges and decides if a user is elligible
 * to receive them. Implementation shall pass valid Context and return count of newly awarded badges.
 */
public abstract class BadgeEvaluator {

    /**
     * Evaluates current FormulaRecords and optionally rewards new badges.
     * @param badges currently awarded badges
     * @return number of newly awarded badged
     */
    public abstract AwardedBadgesCount evaluate(Map<Badge, BadgeAward> badges, Context context);

    @NonNull
    protected BadgeAward createBadgeAward(Badge badge, User user) {
        BadgeAward award = new BadgeAward();
        award.setBadge(badge);
        award.setDate(new Date());
        award.setUser(user);
        return award;
    }

    protected void setPlayId(BadgeAward award, Play play) {
        award.setData(Integer.toString(play.getId()));
    }

    protected void setPlayIds(BadgeAward award, List<Play> plays) {
        StringBuilder sb = new StringBuilder();
        for (Play play : plays) {
            sb.append(Integer.toString(play.getId())).append(',');
        }
        sb.setLength(sb.length() - 1);
        award.setData(sb.toString());
    }

    protected void setPlayRecordIds(BadgeAward award, List<PlayRecord> records) {
        StringBuilder sb = new StringBuilder();
        for (PlayRecord record : records) {
            sb.append(Integer.toString(record.getId())).append(',');
        }
        sb.setLength(sb.length() - 1);
        award.setData(sb.toString());
    }
}
