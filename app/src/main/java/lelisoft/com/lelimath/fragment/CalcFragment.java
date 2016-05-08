package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.FormulaRecord;
import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.logic.CalcLogic;

/**
 * Calc game view
 * Created by Leoš on 17.04.2016.
 */
public class CalcFragment extends Fragment {
    private static final Logger log = LoggerFactory.getLogger(CalcFragment.class);

    private static final int SPEED_FAST = 5;
    private static final int SPEED_SLOW = 10;
    private static final int SPEED_MAX = 20000;

    FragmentActivity activity;
    CalcBridge callback;
    HandleClick clickHandler;
    ArrayList<Formula> formulas;
    Formula formula;
    TextView unknown;
    Animation shake;
    DonutProgress mProgress;
    long started, stopped, totalTimeSpent;
    int formulaPosition = 0;
    Drawable iconSlow, iconNormal, iconFast;
    private CalcLogic logic;

    public interface CalcBridge {
        void calcFinished();
        void saveFormulaRecord(FormulaRecord record);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.fragment_calc, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);

        activity = getActivity();
        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
        mProgress = (DonutProgress) activity.findViewById(R.id.progressBar);
//        iconSlow = ContextCompat.getDrawable(this, R.drawable.ic_action_turtle);
//        iconNormal = ContextCompat.getDrawable(this, R.drawable.ic_action_cat);
//        iconFast = ContextCompat.getDrawable(this, R.drawable.ic_action_running_rabbit);

        clickHandler = new HandleClick();
        attachClickListener();

        formulas = logic.generateFormulas();
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

    public class HandleClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.digit0: digitClicked("0"); break;
                case R.id.digit1: digitClicked("1"); break;
                case R.id.digit2: digitClicked("2"); break;
                case R.id.digit3: digitClicked("3"); break;
                case R.id.digit4: digitClicked("4"); break;
                case R.id.digit5: digitClicked("5"); break;
                case R.id.digit6: digitClicked("6"); break;
                case R.id.digit7: digitClicked("7"); break;
                case R.id.digit8: digitClicked("8"); break;
                case R.id.digit9: digitClicked("9"); break;
                case R.id.buttonBackspace: deleteClicked(); break;
                case R.id.buttonPlus: operatorClicked(Operator.PLUS); break;
                case R.id.buttonMinus: operatorClicked(Operator.MINUS); break;
                case R.id.buttonMultiply: operatorClicked(Operator.MULTIPLY); break;
                case R.id.buttonDivide: operatorClicked(Operator.DIVIDE); break;
                case R.id.buttonSubmit: resultClicked(); break;
            }
        }
    }

    void digitClicked(String digit) {
        log.debug("digitClicked(" + digit + ")");
        if (started == 0) { started = System.currentTimeMillis(); }
        if (formula.getUnknown() != FormulaPart.OPERATOR) {
            formula.append(digit);
            unknown.append(digit);
        }
    }

    void deleteClicked() {
        log.debug("deleteClicked()");
        formula.undoAppend();
        unknown.setText(formula.getUserInput());
    }

    void operatorClicked(Operator operator) {
        if (formula.getUnknown() == FormulaPart.OPERATOR) {
            log.debug("operatorClicked(" + operator + ")");
            formula.setUserEntry(operator.toString());
            unknown.setText(operator.toString());
            if (started == 0) { started = System.currentTimeMillis(); }
        }
    }

    public void resultClicked() {
        log.debug("resultClicked(" + formula + ", ? = " + formula.getUserInput() + ")");

        updateSpeedIndicator();
        if (formula.isEntryCorrect()) {
            FormulaRecord record = getFormulaRecord(true);
            callback.saveFormulaRecord(record);

            if (formulaPosition == formulas.size()) {
                callback.calcFinished();
                return;
            }

            prepareNewFormula();
            displayFormula();
            mProgress.setProgress(formulaPosition);
        } else {
            FormulaRecord record = getFormulaRecord(false);
            callback.saveFormulaRecord(record);

            unknown.startAnimation(shake);
            unknown.setText("");
            formula.clear();
        }
    }

    private FormulaRecord getFormulaRecord(boolean correct) {
        FormulaRecord record = new FormulaRecord();
        record.setGame(Game.FAST_CALC);
        record.setUser(((LeliMathApp)getActivity().getApplication()).getCurrentUser());
        record.setDate(new Date());
        record.setCorrect(correct);
        if (! correct) {
            record.setWrongValue(formula.getUnknownValue());
        }
        record.setFormula(formula);
        return record;
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
            ((TextView) activity.findViewById(R.id.operandFirst)).setText(formula.getFirstOperand().toString());
        }
        if (unknown.getId() != R.id.operator) {
            ((TextView) activity.findViewById(R.id.operator)).setText(formula.getOperator().toString());
        }
        if (unknown.getId() != R.id.operandSecond) {
            ((TextView) activity.findViewById(R.id.operandSecond)).setText(formula.getSecondOperand().toString());
        }
        if (unknown.getId() != R.id.result) {
            ((TextView) activity.findViewById(R.id.result)).setText(formula.getResult().toString());
        }
    }

    private void prepareNewFormula() {
        formula = formulas.get(formulaPosition++);
    }

    private TextView replaceView(TextView view, int template, LinearLayout parent) {
        parent.removeView(view);
        TextView textView = (TextView) activity.getLayoutInflater().inflate(template, null);
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
                return (TextView) activity.findViewById(R.id.operandFirst);
            case OPERATOR:
                return (TextView) activity.findViewById(R.id.operator);
            case SECOND_OPERAND:
                return (TextView) activity.findViewById(R.id.operandSecond);
            default:
                return (TextView) activity.findViewById(R.id.result);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        log.debug("onSaveInstanceState()");
        super.onSaveInstanceState(state);
        state.putParcelableArrayList("formulas", formulas);
        state.putInt("formulaPosition", formulaPosition);
        state.putLong("started", started);
        state.putLong("stopped", stopped);
        state.putLong("timeSpent", totalTimeSpent);
    }

    public void setLogic(CalcLogic logic) {
        this.logic = logic;
    }

    @Override
    public void onPause() {
        log.debug("onPause()");
        stopped = System.currentTimeMillis();
        super.onPause();
    }

    @Override
    public void onResume() {
        log.debug("onResume()");
        super.onResume();
        if (started > 0 && stopped > 0) {
            started = System.currentTimeMillis() - (stopped - started);
            stopped = 0;
        }
        displayFormula();
    }

    private void attachClickListener() {
        activity.findViewById(R.id.buttonPlus).setOnClickListener(clickHandler);
        activity.findViewById(R.id.buttonMinus).setOnClickListener(clickHandler);
        activity.findViewById(R.id.buttonMultiply).setOnClickListener(clickHandler);
        activity.findViewById(R.id.buttonDivide).setOnClickListener(clickHandler);
        activity.findViewById(R.id.buttonBackspace).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit0).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit1).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit2).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit3).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit4).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit5).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit6).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit7).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit8).setOnClickListener(clickHandler);
        activity.findViewById(R.id.digit9).setOnClickListener(clickHandler);
        activity.findViewById(R.id.buttonSubmit).setOnClickListener(clickHandler);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented the callback interface
        try {
            callback = (CalcBridge) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CalcBridge");
        }
    }
}
