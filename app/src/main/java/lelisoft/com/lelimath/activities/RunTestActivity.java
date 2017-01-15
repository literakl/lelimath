package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.Test;
import lelisoft.com.lelimath.data.Campaign;
import lelisoft.com.lelimath.fragment.LeliGameFragment;
import lelisoft.com.lelimath.fragment.PuzzleFragment;
import lelisoft.com.lelimath.logic.BadgeEvaluationTask;
import lelisoft.com.lelimath.logic.Level;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;

/**
 * Responsible for starting tests.
 * Created by Leoš on 08.01.2017.
 */

public class RunTestActivity extends BaseGameActivity implements LeliGameFragment.GameBridge {
    private static final Logger log = LoggerFactory.getLogger(RunTestActivity.class);

    public static final String KEY_POSITION = "POSITION";

    LeliGameFragment fragment;
    Campaign campaign;
    Test test;
    int position;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_with_fragment);

        Intent intent = getIntent();
        campaign = (Campaign) intent.getSerializableExtra(CampaignListActivity.KEY_CAMPAIGN);
        position = intent.getIntExtra(KEY_POSITION, 0);
        test = campaign.getItems().get(position);

        setGameLogic(new PuzzleLogicImpl());
        initializeGameLogic();

        fragment = new PuzzleFragment();
        ((PuzzleFragment)fragment).setLogic((PuzzleLogic) gameLogic);
        displayFragment(R.id.fragment_container, fragment, true);
    }

    @Override
    public void gameFinished() {
        log.debug("puzzleFinished()");
        new BadgeEvaluationTask(this).execute();
        Toast.makeText(this, "Finished", Toast.LENGTH_LONG).show();

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
//        transaction.replace(R.id.puzzle_content, pictureFragment);
//        transaction.commit();
    }

    @Override
    public void savePlayRecord(Play play, PlayRecord record) {
        // todo in background bug #42
        storePlay(play);
        storePlayRecord(record);
    }

    @Override
    protected void initializeGameLogic() {
        FormulaDefinition definition = test.getDefinition();
        gameLogic.setLevel(Level.getCustom(definition.getCount()));
        gameLogic.setFormulaDefinition(definition);
        log.debug(definition.toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(KEY_POSITION, position);
        state.putSerializable(CampaignListActivity.KEY_CAMPAIGN, campaign);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        campaign = (Campaign) state.getSerializable(CampaignListActivity.KEY_CAMPAIGN);
        position = state.getInt(KEY_POSITION, 0);
        test = campaign.getItems().get(position);
    }
}
