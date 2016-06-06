package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lelisoft.com.lelimath.BuildConfig;
import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;

/**
 * Shows detail of badge award
 * Created by Leoš on 22.05.2016.
 */
public class BadgeAwardActivity extends AppCompatActivity {
    public static final String KEY_BADGE = BuildConfig.APPLICATION_ID + ".BADGE";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_badge_award);
        TextView titleView = (TextView) findViewById(R.id.badge_title);
        TextView descView = (TextView) findViewById(R.id.badge_description);
        LinearLayout earnedView = (LinearLayout) findViewById(R.id.earned_badges);
        ImageView typeView = (ImageView) findViewById(R.id.badge_type);
        Badge badge = (Badge) getIntent().getSerializableExtra(KEY_BADGE);
        BadgeAwardProvider provider = new BadgeAwardProvider(this);
        List<BadgeAward> awards = provider.getAwards(badge);
        titleView.setText(badge.getTitle());
        descView.setText(badge.getDescription());
        typeView.setImageResource(Misc.getBadgeImage(badge));

        if (awards.isEmpty()) {
            TextView view = (TextView) getLayoutInflater().inflate(R.layout.template_earned_badge, earnedView, false);
            view.setText(getText(R.string.message_badge_not_awarded));
            earnedView.addView(view);
        } else {
            for (BadgeAward badgeAward : awards) {
                TextView view = (TextView) getLayoutInflater().inflate(R.layout.template_earned_badge, earnedView, false);
                view.setText(Misc.format(badgeAward.getDate(), getApplicationContext()));
                earnedView.addView(view);
            }
        }
    }

    public static void start(Context c, Bundle bundle) {
        c.startActivity(new Intent(c, BadgeAwardActivity.class), bundle);
    }
}
