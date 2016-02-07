package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.fragment.PuzzleFragment;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends AppCompatActivity implements PuzzleFragment.PuzzleBridge {
    private static final String logTag = PuzzleActivity.class.getSimpleName();

    SharedPreferences sharedPref;
    PuzzleLogic logic = new PuzzleLogicImpl();
    PuzzleFragment fragment;

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
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_puzzle_fragments);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = new PuzzleFragment();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();

        initializeLogic();
    }

    @Override
    public PuzzleLogic getLogic() {
        return logic;
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
                fragment.restartGame();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(logTag, "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        initializeLogic();
        fragment.restartGame();
    }

    @Override
    public void onBackPressed() {
        Log.d(logTag, "onBackPressed()");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.puzzle_drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public int getPicture() {
        return pictures[Misc.getRandom().nextInt(pictures.length)];
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
        Log.d(logTag, "onSaveInstanceState()");
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onPause() {
        Log.d(logTag, "onPause()");
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.d(logTag, "onResume()");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d(logTag, "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.d(logTag, "onRestoreInstanceState()");
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onRestart() {
        Log.d(logTag, "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.d(logTag, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(logTag, "onDestroy()");
        super.onDestroy();
    }
}
