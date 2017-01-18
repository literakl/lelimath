package lelisoft.com.lelimath.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.logic.CalcLogic;
import lelisoft.com.lelimath.provider.PlayProvider;

/**
 * Calc game view
 * Created by Leoš on 17.04.2016.
 */
public class CalcFragment extends LeliGameFragment {
    private static final Logger log = LoggerFactory.getLogger(CalcFragment.class);

    FragmentActivity activity;
    GameBridge callback;
    HandleClick clickHandler;
    ArrayList<Formula> formulas;
    Formula formula;
    TextView unknown;
    Animation shake;
    DonutProgress mProgress;
    int formulaPosition = 0;
    CalcLogic logic;
    Play play;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.frg_calc, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        log.debug("onActivityCreated({})", state);
        super.onActivityCreated(state);

        if (state != null) {
            log.debug("onActivityCreated() - initializing from the bundle");
            formulas = state.getParcelableArrayList("formulas");
            formulaPosition = state.getInt("formulaPosition");
            formula = formulas.get(formulaPosition);
            play = state.getParcelable("play");
            started = state.getLong("started");
            stopped = state.getLong("stopped");
        } else {
            setupPlay();
            prepareNewFormula();
            Metrics.saveGameStarted(Game.FAST_CALC);
        }

        activity = getActivity();
        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
        mProgress = (DonutProgress) activity.findViewById(R.id.progressBar);

        clickHandler = new HandleClick();
        attachClickListener();
        if (mProgress != null) {
            mProgress.setMax(formulas.size());
            mProgress.setProgress(0);
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
        startRecordingSpentTime();
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
            startRecordingSpentTime();
        }
    }

    public void resultClicked() {
        log.debug("resultClicked(" + formula + ", ? = " + formula.getUserInput() + ")");

        if (formula.isEntryCorrect()) {
            PlayRecord record = getPlayRecord(true);
            setPoints(record);
            updateSpentTime(record);
            if (mProgress != null) {
                mProgress.setProgress(formulaPosition);
            }

            if (formulaPosition == formulas.size()) {
                play.setFinished(true);
                callback.savePlayRecord(play, record);
                callback.gameFinished(play);
                Metrics.saveGameFinished(Game.FAST_CALC);
            } else {
                callback.savePlayRecord(play, record);
                LeliMathApp.getInstance().playSound(R.raw.correct);

                prepareNewFormula();
                displayFormula();
            }
        } else {
            PlayRecord record = getPlayRecord(false);
            updateSpentTime(record);
            callback.savePlayRecord(play, record);
            LeliMathApp.getInstance().playSound(R.raw.incorrect);

            unknown.startAnimation(shake);
            unknown.setText("");
            formula.clear();
        }
    }

    private PlayRecord getPlayRecord(boolean correct) {
        PlayRecord record = new PlayRecord();
        record.setPlay(play);
        record.setDate(new Date());
        record.setCorrect(correct);
        if (! correct) {
            record.setWrongValue(formula.getUserInput());
        }
        record.setFormula(formula);
        return record;
    }

    /**
     * Calculates points for correctly solved formula.
     */
    protected void setPoints(PlayRecord record) {
        StringBuilder sb = new StringBuilder().append(record.getFirstOperand());
        sb.append(record.getSecondOperand()).append(record.getResult());
        int points = Math.max(1, sb.length() - 3);
        record.setPoints(points);
        LeliMathApp.getBalanceHelper().add(points);
    }

    protected void updateSpentTime(PlayRecord playRecord) {
        super.updateSpentTime(playRecord);
        play.addTimeSpent(playRecord.getTimeSpent());
    }

    @SuppressLint("SetTextI18n")
    protected void displayFormula() {
        TextView view = getUnknownWidget(formula);
        LinearLayout parent = (LinearLayout) view.getParent();
        if (unknown == null) {
            replaceView((TextView) parent.getChildAt(4), R.layout.tmpl_value, parent);
        } else if (unknown.getId() != view.getId()) {
            replaceView(unknown, R.layout.tmpl_value, parent);
        }

        unknown = replaceView(view, R.layout.tmpl_unknown_value, parent);
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

    private void setupPlay() {
        formulas = logic.generateFormulas();
        if (formulas.isEmpty()) {
            formulas.add(new Formula(1, 1, 2, Operator.PLUS, FormulaPart.RESULT));
            Toast.makeText(getContext(), R.string.error_no_formula_generated, Toast.LENGTH_LONG).show();
        }

        play = new Play();
        play.setGame(Game.FAST_CALC);
        play.setLevel(logic.getLevel());
        play.setDate(new Date());
        play.setCount(formulas.size());

        PlayProvider provider = new PlayProvider(activity);
        provider.create(play);
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
        state.putParcelable("play", play);
        state.putLong("started", started);
        state.putLong("stopped", stopped);
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
            callback = (GameBridge) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement GameBridge");
        }
    }
}
