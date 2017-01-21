package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.Test;
import lelisoft.com.lelimath.data.Campaign;
import lelisoft.com.lelimath.data.TestRecord;
import lelisoft.com.lelimath.fragment.CalcFragment;
import lelisoft.com.lelimath.fragment.LeliGameFragment;
import lelisoft.com.lelimath.fragment.PuzzleFragment;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.logic.BadgeEvaluationTask;
import lelisoft.com.lelimath.logic.CalcLogic;
import lelisoft.com.lelimath.logic.CalcLogicImpl;
import lelisoft.com.lelimath.logic.Level;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;
import lelisoft.com.lelimath.provider.TestRecordProvider;

/**
 * Responsible for starting tests.
 * Created by Leoš on 08.01.2017.
 */

public class RunTestActivity extends BaseGameActivity implements LeliGameFragment.GameBridge {
    private static final Logger log = LoggerFactory.getLogger(RunTestActivity.class);

    public static final String KEY_POSITION = "POSITION";
    public static final String KEY_ERRORS = "ERRORS";
    public static final String KEY_NEW_GAME = "NEW_GAME";

    private static List<Game> DEFAULT_GAMES = new ArrayList<>(2);
    static {
        DEFAULT_GAMES.add(Game.FAST_CALC);
        DEFAULT_GAMES.add(Game.PUZZLE);
    }

    TestRecordProvider provider;
    LeliGameFragment fragment;
    Campaign campaign;
    Test test;
    int position, errors;
    boolean newGame = true;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);
        provider = new TestRecordProvider(this);

        setContentView(R.layout.act_with_fragment);

        Intent intent = getIntent();
        campaign = (Campaign) intent.getSerializableExtra(CampaignListActivity.KEY_CAMPAIGN);
        position = intent.getIntExtra(KEY_POSITION, 0);
        test = campaign.getItems().get(position);

        Game game = selectGame(test);
        if (game == Game.PUZZLE) {
            setGameLogic(new PuzzleLogicImpl());
            initializeGameLogic();
            fragment = new PuzzleFragment();
            ((PuzzleFragment) fragment).setLogic((PuzzleLogic) gameLogic);
        } else {
            // todo progress bar
            setGameLogic(new CalcLogicImpl());
            initializeGameLogic();
            fragment = new CalcFragment();
            ((CalcFragment) fragment).setLogic((CalcLogic) gameLogic);
        }
        displayFragment(R.id.fragment_container, fragment, true);
    }

    @Override
    public void gameFinished(Play play) {
        log.debug("puzzleFinished()");
        new BadgeEvaluationTask(this).execute();

        TestRecord testRecord = new TestRecord();
        testRecord.setPlay(play);
        testRecord.setTestId(test.getId());
        testRecord.setCampaignId(campaign.getId());
        testRecord.setScore((int) (100 * (1f - ((float)errors) / play.getCount())));
        provider.create(testRecord);

        Toast.makeText(this, "Finished", Toast.LENGTH_LONG).show();

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
//        transaction.replace(R.id.puzzle_content, pictureFragment);
//        transaction.commit();
    }

    @Override
    public void savePlayRecord(Play play, PlayRecord record) {
        if (record.isCorrect()) {
            newGame = true;
        } else {
            if (newGame) {
                newGame = false;
                errors++;
            }
        }

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

    private Game selectGame(Test test) {
        List<Game> games = test.getGames();
        if (games == null || games.isEmpty()) {
            games = DEFAULT_GAMES;
        }
        if (games.size() == 1) {
            return games.get(0);
        }
        int position = Misc.getRandom().nextInt(games.size());
        return games.get(position);
//        return Game.FAST_CALC;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(KEY_POSITION, position);
        state.putInt(KEY_ERRORS, errors);
        state.putBoolean(KEY_NEW_GAME, newGame);
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
