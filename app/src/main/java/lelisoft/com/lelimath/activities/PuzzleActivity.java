package lelisoft.com.lelimath.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;
import lelisoft.com.lelimath.view.TilesView;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends Activity {
    private static final String logTag = PuzzleActivity.class.getSimpleName();
    TilesView tilesView;
    PuzzleLogic logic = new PuzzleLogicImpl();

    @Override
    protected void onCreate(Bundle state) {
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_puzzle);
        tilesView = (TilesView) findViewById(R.id.tiles);
        tilesView.setBackgroundPicture(R.drawable.pic_cute_girl);

        logic.setFormulaDefinition(getFormulaDefinition());
        logic.setLevel(PuzzleLogic.Level.EASY);
        tilesView.setLogic(logic);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        Log.d(logTag, "onSaveInstanceState()");
        super.onSaveInstanceState(state);
//        state.putParcelable("formula", formula);
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
        tilesView.setLogic(null);// todo find proper place
        super.onDestroy();
    }

    public FormulaDefinition getFormulaDefinition() {
        Values twoDigitsNumbers = new Values(0, 99);
        FormulaDefinition definition = new FormulaDefinition();
        definition.setLeftOperand(twoDigitsNumbers);
        definition.setRightOperand(twoDigitsNumbers);
        definition.setResult(twoDigitsNumbers);
        definition.addOperator(Operator.PLUS);
        definition.addOperator(Operator.MINUS);
        definition.addUnknown(FormulaPart.FIRST_OPERAND);
        definition.addUnknown(FormulaPart.OPERATOR);
        definition.addUnknown(FormulaPart.SECOND_OPERAND);
        definition.addUnknown(FormulaPart.RESULT);

        return definition;
    }
}
