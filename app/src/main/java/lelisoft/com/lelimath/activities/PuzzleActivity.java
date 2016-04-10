package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.fragment.PictureFragment;
import lelisoft.com.lelimath.fragment.PuzzleFragment;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends BaseGameActivity implements PuzzleFragment.PuzzleBridge {
    private static final Logger log = LoggerFactory.getLogger(PuzzleActivity.class);

    PuzzleFragment puzzleFragment;
    PictureFragment pictureFragment;

    static int[] pictures = new int[] {
            R.drawable.pic_cat_kitten,
            R.drawable.pic_child_ball_on_head,
            R.drawable.pic_cute_girl,
            R.drawable.pic_girl_teddy_bear,
            R.drawable.pic_green_snake,
            R.drawable.pic_kid_airplane,
            R.drawable.pic_koalas_on_tree,
            R.drawable.pic_owl
    };

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setGameLogic(new PuzzleLogicImpl());
        initializeGameLogic();

        setContentView(R.layout.activity_puzzle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPuzzle);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(PuzzleActivity.this);
            }
        });

        puzzleFragment = new PuzzleFragment();
        puzzleFragment.setLogic((PuzzleLogic) gameLogic);
        initializePuzzleFragment(false);
    }

    @Override
    public void puzzleFinished() {
        log.debug("puzzleFinished()");
        pictureFragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putInt(PictureFragment.ARG_PICTURE, pictures[Misc.getRandom().nextInt(pictures.length)]);
        pictureFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.puzzle_content, pictureFragment);
        transaction.commit();

        puzzleFragment = null;
    }

    private void initializePuzzleFragment(boolean replace) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (replace) {
            transaction.replace(R.id.puzzle_content, puzzleFragment);
        } else {
            transaction.add(R.id.puzzle_content, puzzleFragment);
        }
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.puzzle_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_game: {
                initializeGameLogic();
                restartGame();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log.debug("onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        initializeGameLogic();
        restartGame();
    }

    @Override
    public void onBackPressed() {
        log.debug("onBackPressed()");
        super.onBackPressed();
    }

    /**
     * Restarts game. If PuzzleFragment is not displayed it will be restored.
     */
    private void restartGame() {
        log.debug("restartGame(), puzzleFragment " + ((puzzleFragment == null) ? "is not " : "is ") + "null");
        puzzleFragment = new PuzzleFragment();
        puzzleFragment.setLogic((PuzzleLogic) gameLogic);
        initializePuzzleFragment(true);
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, PuzzleActivity.class));
    }
}
