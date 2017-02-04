package lelisoft.com.lelimath.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
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
import lelisoft.com.lelimath.helpers.LeliMathApp;
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

    private static List<Game> DEFAULT_GAMES = Collections.singletonList(Game.FAST_CALC);

    TestRecordProvider provider;
    LeliGameFragment fragment;
    AlertDialog dialog;
    View progressbar;
    Toolbar toolbar;

    Campaign campaign;
    Test test;
    int position, errors;
    boolean newGame = true;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);
        provider = new TestRecordProvider(this);

        setContentView(R.layout.act_with_fragment_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setPoints();
        toolbar.setNavigationOnClickListener(listListener);

        Intent intent = getIntent();
        campaign = (Campaign) intent.getSerializableExtra(CampaignListActivity.KEY_CAMPAIGN);
        position = intent.getIntExtra(KEY_POSITION, 0);
        test = campaign.getItems().get(position);
        generateTest();
        displayFragment(R.id.fragment_container, fragment, false, false);
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

        showGameCompletedDialog(testRecord.getScore());
        LeliMathApp.getInstance().playSound(R.raw.level);
    }

    @Override
    public void savePlayRecord(Play play, PlayRecord record) {
        if (record.isCorrect()) {
            newGame = true;
            setPoints();
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

    private void showGameCompletedDialog(int score) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams")
            View view = inflater.inflate(R.layout.dlg_game_completed, null);
            Misc.setRating(view, score);

            TextView caption = (TextView) view.findViewById(R.id.caption);
            if (score >= 60) {
                if (score >= 90) {
                    caption.setText(R.string.caption_game_completed_great);
                } else {
                    caption.setText(R.string.caption_game_completed_good);
                }
            } else {
                caption.setText(R.string.caption_game_completed);
            }

            ImageView button = (ImageView) view.findViewById(R.id.buttonList);
            button.setOnClickListener(listListener);

            button = (ImageView) view.findViewById(R.id.buttonNext);
            if (position + 1 < campaign.getCount()) {
                button.setOnClickListener(nextListener);
            } else {
                button.setImageResource(R.drawable.ic_action_next_disabled);
            }

            dialog = new AlertDialog.Builder(RunTestActivity.this).setView(view).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
            dialog.show();
        } catch (Exception e) {
            log.debug("Error", e);
        }
    }

    private void generateTest() {
        Game game = selectGame(test);
        if (game == Game.PUZZLE) {
            setGameLogic(new PuzzleLogicImpl());
            initializeGameLogic();
            fragment = new PuzzleFragment();
            ((PuzzleFragment) fragment).setLogic((PuzzleLogic) gameLogic);

            if (progressbar != null) {
                toolbar.removeView(progressbar);
                progressbar = null;
            }
        } else {
            setGameLogic(new CalcLogicImpl());
            initializeGameLogic();
            fragment = new CalcFragment();
            ((CalcFragment) fragment).setLogic((CalcLogic) gameLogic);

            if (progressbar == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.tmpl_progressbar, toolbar, true);
                progressbar = toolbar.findViewById(R.id.progressBar);
            }
        }
    }

    @Override
    protected void initializeGameLogic() {
        FormulaDefinition definition = test.getDefinition();
        gameLogic.setLevel(Level.getCustom(definition.getCount()));
        gameLogic.setFormulaDefinition(definition);
        log.debug(definition.toString());
    }

    private Game selectGame(Test test) {
        List<Game> games = test.getDefinition().getGames();
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

    @SuppressWarnings("ConstantConditions")
    private void setPoints() {
        int balance = LeliMathApp.getBalanceHelper().getBalance();
        String title = LeliMathApp.resources.getString(R.string.caption_points, balance);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    View.OnClickListener nextListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            test = campaign.getItems().get(++position);
            generateTest();
            displayFragment(R.id.fragment_container, fragment, true, true);
        }
    };

    View.OnClickListener listListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            setResult(RESULT_OK);
            finish();
        }
    };

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
