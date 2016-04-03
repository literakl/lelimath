package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.fragment.PictureFragment;
import lelisoft.com.lelimath.fragment.PuzzleFragment;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends AppCompatActivity implements PuzzleFragment.PuzzleBridge {

    private static final Logger log = LoggerFactory.getLogger(PuzzleActivity.class);

    SharedPreferences sharedPref;
    PuzzleLogic logic = new PuzzleLogicImpl();
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

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_puzzle_navigation);
        initializeLogic();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(PuzzleActivity.this);
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        puzzleFragment = new PuzzleFragment();
        puzzleFragment.setLogic(logic);
        transaction.add(R.id.puzzle_content, puzzleFragment);
        transaction.commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.puzzle_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_game: {
                initializeLogic();
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
        initializeLogic();
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        puzzleFragment = new PuzzleFragment();
        puzzleFragment.setLogic(logic);
        transaction.replace(R.id.puzzle_content, puzzleFragment);
        transaction.commit();
    }

    protected void initializeLogic() {
        String prefComplexity = sharedPref.getString(GamePreferenceActivity.KEY_COMPLEXITY, "EASY");
        logic.setLevel(PuzzleLogic.Level.valueOf(prefComplexity));

        Values defaultValues = new Values(0, 30), values = defaultValues;
        FormulaDefinition definition = new FormulaDefinition().addUnknown(FormulaPart.RESULT);
        logic.setFormulaDefinition(definition);

        String sValues = sharedPref.getString(GamePreferenceActivity.KEY_FIRST_OPERAND, null);
        if (sValues != null) {
            values = Values.parse(sValues);
        }
        definition.setLeftOperand(values);

        sValues = sharedPref.getString(GamePreferenceActivity.KEY_SECOND_OPERAND, null);
        if (sValues != null) {
            values = Values.parse(sValues);
        } else {
            values = defaultValues;
        }
        definition.setRightOperand(values);

        sValues = sharedPref.getString(GamePreferenceActivity.KEY_RESULT, null);
        if (sValues != null) {
            values = Values.parse(sValues);
        } else {
            values = defaultValues;
        }
        definition.setResult(values);

        Set<String> mValues = sharedPref.getStringSet(GamePreferenceActivity.KEY_OPERATIONS, null);
        if (mValues != null) {
            for (String value : mValues) {
                definition.addOperator(Operator.valueOf(value));
            }
        } else {
            definition.addOperator(Operator.PLUS).addOperator(Operator.MINUS);
            definition.addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        log.debug("onSaveInstanceState()");
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onPause() {
        log.debug("onPause()");
        super.onPause();

    }

    @Override
    protected void onResume() {
        log.debug("onResume()");
        super.onResume();
    }

    @Override
    protected void onStart() {
        log.debug("onStart()");
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        log.debug("onRestoreInstanceState()");
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onRestart() {
        log.debug("onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        log.debug("onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log.debug("onDestroy()");
        super.onDestroy();
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, PuzzleActivity.class));
    }
}
