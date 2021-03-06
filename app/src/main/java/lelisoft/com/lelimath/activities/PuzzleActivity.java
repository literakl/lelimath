package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.fragment.LeliGameFragment;
import lelisoft.com.lelimath.fragment.PictureFragment;
import lelisoft.com.lelimath.fragment.PuzzleFragment;
import lelisoft.com.lelimath.logic.BadgeEvaluationTask;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends BaseGameActivity implements LeliGameFragment.GameBridge, PictureFragment.PictureBridge {
    private static final Logger log = LoggerFactory.getLogger(PuzzleActivity.class);

    PuzzleFragment puzzleFragment;
    PictureFragment pictureFragment;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_puzzle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPuzzle);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(PuzzleActivity.this);
                }
            });
        }

        setGameLogic(new PuzzleLogicImpl());
        initializeGameLogic();

        puzzleFragment = new PuzzleFragment();
        puzzleFragment.setLogic((PuzzleLogic) gameLogic);
        displayFragment(R.id.puzzle_content, puzzleFragment, false, false);
    }

    @Override
    public void gameFinished(Play play) {
        log.debug("puzzleFinished()");
        new BadgeEvaluationTask(this).execute();

        pictureFragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putInt(PictureFragment.ARG_PICTURE, selectRandomPicture());
        pictureFragment.setArguments(args);

        displayFragment(R.id.puzzle_content, pictureFragment, true, true);
        puzzleFragment = null;
    }

    @Override
    public void savePlayRecord(Play play, PlayRecord record) {
        // todo in background bug #42
        storePlay(play);
        storePlayRecord(record);
    }

    /**
     * Restarts game. If PuzzleFragment is not displayed it will be restored.
     */
    @Override
    public void restartGame() {
        log.debug("restartGame(), puzzleFragment " + ((puzzleFragment == null) ? "is not null" : "is null"));
        puzzleFragment = new PuzzleFragment();
        puzzleFragment.setLogic((PuzzleLogic) gameLogic);
        displayFragment(R.id.puzzle_content, puzzleFragment, true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initializeGameLogic();
        restartGame();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.practice_settings:
                Intent intent = new Intent(this, PracticeSettingsActivity.class);
                startActivityForResult(intent, 1);
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice, menu);
        return true;
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, PuzzleActivity.class));
    }
}
