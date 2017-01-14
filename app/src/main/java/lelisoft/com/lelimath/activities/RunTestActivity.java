package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Test;
import lelisoft.com.lelimath.data.Campaign;

/**
 * Responsible for starting tests.
 * Created by Leoš on 08.01.2017.
 */

public class RunTestActivity extends BaseGameActivity {
    private static final Logger log = LoggerFactory.getLogger(RunTestActivity.class);

    public static final String KEY_POSITION = "POSITION";

    Campaign script;
    Test test;
    int position;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_with_fragment);

        Intent intent = getIntent();
        script = (Campaign) intent.getSerializableExtra(CampaignListActivity.KEY_CAMPAIGN);
        position = intent.getIntExtra(KEY_POSITION, 0);
        test = script.getItems().get(position);

    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(KEY_POSITION, position);
        state.putSerializable(CampaignListActivity.KEY_CAMPAIGN, script);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        script = (Campaign) state.getSerializable(CampaignListActivity.KEY_CAMPAIGN);
        position = state.getInt(KEY_POSITION, 0);
        test = script.getItems().get(position);
    }
}
