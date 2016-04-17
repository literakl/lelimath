package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.logic.CalcLogicImpl;


public class CalcActivity extends BaseGameActivity {
    private static final Logger log = LoggerFactory.getLogger(CalcActivity.class);

    private static final int SPEED_FAST = 5;
    private static final int SPEED_SLOW = 10;
    private static final int SPEED_MAX = 20000;

    ArrayList<Formula> formulas;
    Formula formula;
    TextView unknown;
    Animation shake;
    DonutProgress mProgress;
    long started, stopped, totalTimeSpent;
    int formulaPosition = 0;
    Drawable iconSlow, iconNormal, iconFast;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_calc);
        mProgress = (DonutProgress) findViewById(R.id.progressBar);
        shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        iconSlow = ContextCompat.getDrawable(this, R.drawable.ic_action_turtle);
        iconNormal = ContextCompat.getDrawable(this, R.drawable.ic_action_cat);
        iconFast = ContextCompat.getDrawable(this, R.drawable.ic_action_running_rabbit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCalc);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(CalcActivity.this);
            }
        });

        setGameLogic(new CalcLogicImpl());
        initializeGameLogic();
        formulas = gameLogic.generateFormulas();
        prepareNewFormula();
        mProgress.setMax(formulas.size());
        mProgress.setProgress(1);

        if (state != null) {
            formulas = state.getParcelableArrayList("formulas");
            formulaPosition = state.getInt("formulaPosition");
            formula = formulas.get(formulaPosition);
            started = state.getLong("started");
            stopped = state.getLong("stopped");
            totalTimeSpent = state.getLong("timeSpent");
        }
    }

    public void digitClicked(View view) {
        CharSequence digit = ((TextView)view).getText();
        log.debug("digitClicked(" + digit + ")");
        if (started == 0) { started = System.currentTimeMillis(); }
        if (formula.getUnknown() != FormulaPart.OPERATOR) {
            formula.append(digit);
            unknown.append(digit);
        }
    }

    public void deleteClicked(View view) {
        log.debug("deleteClicked()");
        formula.undoAppend();
        unknown.setText(formula.getUserInput());
    }

    public void operatorClicked(View view) {
        String operator = null;
        if (formula.getUnknown() == FormulaPart.OPERATOR) {
            switch (view.getId()) {
                case R.id.buttonPlus: operator = Operator.PLUS.toString(); break;
                case R.id.buttonMinus: operator = Operator.MINUS.toString(); break;
                case R.id.buttonMultiply: operator = Operator.MULTIPLY.toString(); break;
                case R.id.buttonDivide: operator = Operator.DIVIDE.toString(); break;
            }
            log.debug("operatorClicked(" + operator + ")");
            formula.setUserEntry(operator);
            unknown.setText(operator);
            if (started == 0) { started = System.currentTimeMillis(); }
        }
    }

    public void resultClicked(View view) {
        log.debug("resultClicked(" + formula + ", ? = " + formula.getUserInput() + ")");
        if (formula.isEntryCorrect()) {
            if (formulaPosition == formulas.size()) {
                Toast.makeText(this, "Konec", Toast.LENGTH_LONG).show();
                return;
            }

            prepareNewFormula();
            displayFormula();
            updateSpeedIndicator();
            mProgress.setProgress(formulaPosition);
        } else {
            unknown.startAnimation(shake);
            unknown.setText("");
            formula.clear();
        }
    }

    public void restartGameClicked(View view) {
        log.debug("restartGameClicked()");
        formulaPosition = 0;
        formulas = gameLogic.generateFormulas();
        prepareNewFormula();
        displayFormula();
        updateSpeedIndicator();
        mProgress.setProgress(1);
    }

    private void updateSpeedIndicator() {
        long now = System.currentTimeMillis();
        long spent = now - started;
        if (spent > SPEED_MAX) {
            spent = SPEED_MAX;
        }
        totalTimeSpent += spent;
        formula.setTimeSpent(spent);
        started = now;

        long averageTime = (totalTimeSpent) / (1000 * formulaPosition);
/*
        View speedIndicator = findViewById(R.id.speedIndicator);
        if (averageTime < SPEED_FAST) {
            ((ActionMenuItemView) speedIndicator).setIcon(iconFast);
        } else if (averageTime > SPEED_SLOW) {
            ((ActionMenuItemView) speedIndicator).setIcon(iconSlow);
        } else {
            ((ActionMenuItemView) speedIndicator).setIcon(iconNormal);
        }
*/
    }

    protected void initializeGameLogic() {
        super.initializeGameLogic();
        FormulaDefinition definition = gameLogic.getFormulaDefinition();
        definition.setUnknowns(null);

        Set<String> mValues = sharedPref.getStringSet(GamePreferenceActivity.KEY_UNKNOWN, null);
        if (mValues != null) {
            for (String value : mValues) {
                definition.addUnknown(FormulaPart.valueOf(value));
            }
        } else {
            definition.addUnknown(FormulaPart.RESULT);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        log.debug("onSaveInstanceState()");
        super.onSaveInstanceState(state);
        state.putParcelableArrayList("formulas", formulas);
        state.putInt("formulaPosition", formulaPosition);
        state.putLong("started", started);
        state.putLong("stopped", stopped);
        state.putLong("timeSpent", totalTimeSpent);
    }

    @Override
    protected void onPause() {
        log.debug("onPause()");
        stopped = System.currentTimeMillis();
        super.onPause();
    }

    @Override
    protected void onResume() {
        log.debug("onResume()");
        super.onResume();
        if (started > 0 && stopped > 0) {
            started = System.currentTimeMillis() - (stopped - started);
            stopped = 0;
        }
        displayFormula();
    }

    protected void displayFormula() {
        TextView view = getUnknownWidget(formula);
        LinearLayout parent = (LinearLayout) view.getParent();
        if (unknown == null) {
            replaceView((TextView) parent.getChildAt(4), R.layout.template_value, parent);
        } else if (unknown.getId() != view.getId()) {
            replaceView(unknown, R.layout.template_value, parent);
        }

        unknown = replaceView(view, R.layout.template_unknown_value, parent);
        unknown.setText(formula.getUserInput());

        if (unknown.getId() != R.id.operandFirst) {
            ((TextView)findViewById(R.id.operandFirst)).setText(formula.getFirstOperand().toString());
        }
        if (unknown.getId() != R.id.operator) {
            ((TextView)findViewById(R.id.operator)).setText(formula.getOperator().toString());
        }
        if (unknown.getId() != R.id.operandSecond) {
            ((TextView)findViewById(R.id.operandSecond)).setText(formula.getSecondOperand().toString());
        }
        if (unknown.getId() != R.id.result) {
            ((TextView)findViewById(R.id.result)).setText(formula.getResult().toString());
        }
    }

    private void prepareNewFormula() {
        formula = formulas.get(formulaPosition++);
    }

    private TextView replaceView(TextView view, int template, LinearLayout parent) {
        parent.removeView(view);
        TextView textView = (TextView)getLayoutInflater().inflate(template, null);
        textView.setId(view.getId());
        int index = getWidgetPosition(view.getId());
        parent.addView(textView, index, view.getLayoutParams());
        return textView;
    }

    private int getWidgetPosition(int rid) {
        switch (rid) {
            case R.id.operandFirst:
                return 0;
            case R.id.operator:
                return 1;
            case R.id.operandSecond:
                return 2;
            case R.id.result:
                return 4;
            default:
                return -1;
        }
    }

    private TextView getUnknownWidget(Formula formula) {
        switch (formula.getUnknown()) {
            case FIRST_OPERAND:
                return (TextView)findViewById(R.id.operandFirst);
            case OPERATOR:
                return (TextView)findViewById(R.id.operator);
            case SECOND_OPERAND:
                return (TextView)findViewById(R.id.operandSecond);
            default:
                return (TextView)findViewById(R.id.result);
        }
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, CalcActivity.class));
    }
}
