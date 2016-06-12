package lelisoft.com.lelimath.logic;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.logic.badges.CorrectnessBadgeEvaluator;
import lelisoft.com.lelimath.logic.badges.PlayCountBadgeEvaluator;
import lelisoft.com.lelimath.logic.badges.StaminaBadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

/**
 * Main class for awarding Badges
 * Created by Leoš on 18.05.2016.
 */
public class BadgeEvaluationTask extends AsyncTask<Void, Void, Integer> {
    private static final Logger log = LoggerFactory.getLogger(BadgeEvaluationTask.class);

    Context context;

    public BadgeEvaluationTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        log.debug("doInBackground starts");
        BadgeAwardProvider provider = new BadgeAwardProvider(context);
        Map<Badge, List<BadgeAward>> badges = provider.getAll();

        BadgeEvaluator evaluator = new PlayCountBadgeEvaluator();
        AwardedBadgesCount badgesCount = evaluator.evaluate(badges, context);
        evaluator = new StaminaBadgeEvaluator();
        badgesCount.add(evaluator.evaluate(badges, context));
        evaluator = new CorrectnessBadgeEvaluator();
        badgesCount.add(evaluator.evaluate(badges, context));

        log.debug("doInBackground finished with {} / {} / {} badges", badgesCount.gold, badgesCount.silver, badgesCount.bronze);
        return badgesCount.bronze + badgesCount.silver + badgesCount.gold;
    }

    @Override
    protected void onPostExecute(Integer count) {
        if (count > 0) {
            String string = context.getString(R.string.message_badge_received, count);
            Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        }
    }
}
