package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
public class PuzzleActivity extends AppCompatActivity implements PuzzleFragment.PuzzleBridge,
        NavigationView.OnNavigationItemSelectedListener {

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.puzzle_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.puzzle_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
            case R.id.action_level: {
                Intent intent = new Intent();
                intent.setClass(this, GamePreferenceActivity.class);
                startActivityForResult(intent, 1);
                return true;
            }
            default: {
                Toast.makeText(this, "Default", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        log.debug("onNavigationItemSelected");
        FormulaDefinition definition = null;
        switch (item.getItemId()) {
            case R.id.nav_level_A10: {
                Values values = new Values(0, 10);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_S10: {
                Values values = new Values(0, 10);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_A20: {
                Values values = new Values(0, 20);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_S20: {
                Values values = new Values(0, 20);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_AS50: {
                Values values = new Values(0, 50);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS).addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_AS100: {
                Values values = new Values(0, 100);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS).addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_M3: {
                Values left = new Values(3), right = new Values(0, 10), result = new Values(0, 30);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_M4: {
                Values left = new Values(4), right = new Values(0, 10), result = new Values(0, 40);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD5: {
                Values left = new Values(5), right = new Values(0, 10), result = new Values(0, 50);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD6: {
                Values left = new Values(6), right = new Values(0, 10), result = new Values(0, 60);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD7: {
                Values left = new Values(7), right = new Values(0, 10), result = new Values(0, 70);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD8: {
                Values left = new Values(8), right = new Values(0, 10), result = new Values(0, 80);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD9: {
                Values left = new Values(9), right = new Values(0, 10), result = new Values(0, 90);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            default: {
                Toast.makeText(this, "Chybi handler!", Toast.LENGTH_LONG).show();
            }
        }

        if (definition != null) {
            logic.setFormulaDefinition(definition);
            restartGame();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.puzzle_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.puzzle_drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
}
