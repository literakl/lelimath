package lelisoft.com.lelimath.logic.badges;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.logic.BadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;

/**
 * Logic for three badges awarding user stamina.
 * Created by Leoš on 26.05.2016.
 */
public class StaminaBadgeEvaluator implements BadgeEvaluator {
    private static final Logger log = LoggerFactory.getLogger(StaminaBadgeEvaluator.class);

    @Override
    public AwardedBadgesCount evaluate(Map<Badge, BadgeAward> badges, Context context) {
        log.debug("evaluate");
        BadgeAwardProvider awardProvider = new BadgeAwardProvider(context);
        AwardedBadgesCount badgesCount = new AwardedBadgesCount();
        User user = LeliMathApp.getInstance().getCurrentUser();
        return null;
    }
}
