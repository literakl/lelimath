package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.logic.CalcLogicImpl;
import lelisoft.com.lelimath.logic.FormulaGenerator;
import lelisoft.com.lelimath.view.FormulaResultPair;


public class CalcActivity extends BaseGameActivity {
    private static final Logger log = LoggerFactory.getLogger(CalcActivity.class);

    private static final int SPEED_FAST = 5;
    private static final int SPEED_SLOW = 10;
    private static final int SPEED_MAX = 20000;

    List<FormulaResultPair> formulaResultPairs;
    Formula formula;
    TextView unknown;
    Animation shake;
    long started, stopped, totalTimeSpent;
    int count, formulaPosition = 0;

    public void digitClicked(View view) {
        log.debug("digitClicked()");
        if (started == 0) { started = System.currentTimeMillis(); }
        if (formula.getUnknown() != FormulaPart.OPERATOR) {
            CharSequence digit = ((TextView)view).getText();
            formula.append(digit);
            unknown.append(digit);
        }
    }

    public void deleteClicked(View view) {
        log.debug("deleteClicked()");
        formula.undoAppend();
        unknown.setText(formula.getUserEntry());
    }

    public void operatorClicked(View view) {
        log.debug("operatorClicked()");
        if (started == 0) { started = System.currentTimeMillis(); }
        if (formula.getUnknown() == FormulaPart.OPERATOR) {
            // in future there may be mapping based on component's id
            CharSequence operator = ((TextView)view).getText();
            formula.setUserEntry(operator);
            unknown.setText(operator);
        }
    }

    public void resultClicked(View view) {
        log.debug("resultClicked()");
        if (formula.isEntryCorrect()) {
            prepareNewFormula();
            displayFormula();
            updateSpeedIndicator();
        } else {
            unknown.startAnimation(shake);
            unknown.setText("");
        }
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
        count++;

        long averageTime = (totalTimeSpent) / (1000 * count);
        if (averageTime < SPEED_FAST) {
            ((ImageView)findViewById(R.id.speedIndicator)).setImageResource(R.drawable.ic_action_running_rabbit);
        } else if (averageTime > SPEED_SLOW) {
            ((ImageView)findViewById(R.id.speedIndicator)).setImageResource(R.drawable.ic_action_turtle);
        } else {
            ((ImageView)findViewById(R.id.speedIndicator)).setImageResource(R.drawable.ic_action_cat);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        log.debug("onSaveInstanceState()");
        super.onSaveInstanceState(state);
        state.putParcelable("formula", formula);
        state.putLong("started", started);
        state.putLong("stopped", stopped);
        state.putLong("timeSpent", totalTimeSpent);
    }

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setGameLogic(new CalcLogicImpl());
        initializeGameLogic();
        formulaResultPairs = gameLogic.generateFormulaResultPairs(10);

        setContentView(R.layout.activity_calc);
        shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCalc);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(CalcActivity.this);
            }
        });

        if (formula == null && state == null) {
            prepareNewFormula();
        }

        if (state != null) {
            formula = state.getParcelable("formula");
            started = state.getLong("started");
            stopped = state.getLong("stopped");
            totalTimeSpent = state.getLong("timeSpent");
        }
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
        unknown.setText(formula.getUserEntry());

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
//        formula = formulaResultPairs.get(formulaPosition++);
        formula = FormulaGenerator.generateRandomFormula(gameLogic.getFormulaDefinition());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, CalcActivity.class));
    }
}
