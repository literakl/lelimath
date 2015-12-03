package lelisoft.com.lelimath.activities;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.View;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.view.TilesView;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends Activity {
    private static final String logTag = CalcActivity.class.getSimpleName();
    TilesView tilesView;

    @Override
    protected void onCreate(Bundle state) {
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_puzzle);
        View puzzleView = findViewById(R.id.puzzleScreen);

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int widthdp = (int) (width / getResources().getDisplayMetrics().density);
        int height = display.getHeight();
        int heightdp = (int) (height / getResources().getDisplayMetrics().density);
        Log.d(logTag, width + ", " + height);
        Log.d(logTag, widthdp + ", " + heightdp);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            Log.d(logTag, "actionBarHeight " + actionBarHeight + ", height = " + (height - actionBarHeight));
            Log.d(logTag, "actionBarHeight " + actionBarHeight + ", heightdp = " + (heightdp - actionBarHeight));
        }

        if (getTheme().resolveAttribute(android.R.attr.windowTitleSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            Log.d(logTag, "windowTitleSize " + actionBarHeight + ", height = " + (height - actionBarHeight));
            Log.d(logTag, "windowTitleSize " + actionBarHeight + ", heightdp = " + (heightdp - actionBarHeight));
        }

        tilesView = (TilesView) findViewById(R.id.tiles);
        tilesView.setBackgroundPicture(R.drawable.pic_cute_girl);
//        Formula formula = FormulaGenerator.generateRandomFormula(getFormulaDefinition());
    }

    /*
    int textAppearance = android.R.attr.textAppearanceLarge;
    myTextView.setTextAppearance(context, textAppearance);
     */

    public void tileClicked(View view) {
        Log.d(logTag, "tileClicked()");
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
