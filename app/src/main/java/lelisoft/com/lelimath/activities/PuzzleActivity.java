package lelisoft.com.lelimath.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.logic.FormulaGenerator;
import lelisoft.com.lelimath.view.TileGroupLayout;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends Activity {
    private static final String logTag = CalcActivity.class.getSimpleName();
    TileGroupLayout hiddenPicture;

    @Override
    protected void onCreate(Bundle state) {
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_puzzle);
        hiddenPicture = (TileGroupLayout) findViewById(R.id.hidddenPictureArea);
        hiddenPicture.setPictureResource(R.drawable.pic_cute_girl);
        appendTile();
        appendTile();
//        appendTile();
    }

    private void appendTile() {
        Button view = (Button) getLayoutInflater().inflate(R.layout.template_tile, hiddenPicture, false);
        view.setId(View.generateViewId());
        view.setOnClickListener(tileListener);
        Formula formula = FormulaGenerator.generateRandomFormula(getFormulaDefinition());
        view.setText(formula.toString());
        hiddenPicture.addView(view, view.getLayoutParams());
    }

    private View.OnClickListener tileListener = new View.OnClickListener() {
        public void onClick(View view) {
            Log.d(logTag, "tileClicked()");
            hiddenPicture.removeView(view);
        }
    };

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
