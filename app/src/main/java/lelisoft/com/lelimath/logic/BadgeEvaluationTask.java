package lelisoft.com.lelimath.logic;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.logic.badges.CorrectnessBadgeEvaluator;
import lelisoft.com.lelimath.logic.badges.PlayCountBadgeEvaluator;
import lelisoft.com.lelimath.logic.badges.StaminaBadgeEvaluator;
import lelisoft.com.lelimath.provider.PlayRecordProvider;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

/**
 * Main class for awarding Badges
 * Created by Leoš on 18.05.2016.
 */
public class BadgeEvaluationTask extends AsyncTask<Void, Void, BadgeEvaluationTask.Holder> {
    private static final Logger log = LoggerFactory.getLogger(BadgeEvaluationTask.class);

    WeakReference<Context> context;

    public BadgeEvaluationTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Holder doInBackground(Void... params) {
        log.debug("doInBackground starts");
        int points = getPointsForCurrenPlay();

        BadgeEvaluator evaluator = new PlayCountBadgeEvaluator();
        AwardedBadgesCount badgesCount = evaluator.evaluate(context.get());
        evaluator = new StaminaBadgeEvaluator();
        badgesCount.add(evaluator.evaluate(context.get()));
        evaluator = new CorrectnessBadgeEvaluator();
        badgesCount.add(evaluator.evaluate(context.get()));

        log.debug("doInBackground finished with {} points and {} / {} / {} badges", points, badgesCount.gold, badgesCount.silver, badgesCount.bronze);
        return new Holder(badgesCount, points);
    }

    private int getPointsForCurrenPlay() {
        PlayRecordProvider provider = new PlayRecordProvider(context.get());
        return provider.getLastPlayPoints();
    }

    @Override
    protected void onPostExecute(Holder holder) {
        AwardedBadgesCount badgesCount = holder.badgesCount;
        int badges = badgesCount.bronze + badgesCount.silver + badgesCount.gold;
        String string = context.get().getString(R.string.message_badge_received, badges, holder.points);
        Toast.makeText(context.get(), string, Toast.LENGTH_SHORT).show();
    }

    public static class Holder {
        AwardedBadgesCount badgesCount;
        int points;

        public Holder(AwardedBadgesCount badgesCount, int points) {
            this.badgesCount = badgesCount;
            this.points = points;
        }
    }
}
