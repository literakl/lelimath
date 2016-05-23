package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

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
        ImageView typeView = (ImageView) findViewById(R.id.badge_type);
        Badge badge = (Badge) getIntent().getSerializableExtra(KEY_BADGE);
        BadgeAwardProvider provider = new BadgeAwardProvider(this);
        Map<Badge, BadgeAward> badges = provider.getAll();
        titleView.setText(badge.getTitle());
        typeView.setImageResource(Misc.getBadgeImage(badge));
    }

    public static void start(Context c, Bundle bundle) {
        c.startActivity(new Intent(c, BadgeAwardActivity.class), bundle);
    }
}
