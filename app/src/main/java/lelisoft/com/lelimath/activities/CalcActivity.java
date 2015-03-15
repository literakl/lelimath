package lelisoft.com.lelimath.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.logic.FormulaGenerator;


public class CalcActivity extends Activity {
    Formula formula;
    FormulaDefinition definition = getFormulaDefinition();
    TextView unknown;

    public void digitClicked(View view) {
        CharSequence digit = ((TextView)view).getText();
        formula.append(digit);
        unknown.append(digit);
    }

    public void resultClicked(View view) {
        if (formula.isEntryCorrect()) {
            prepareNewFormula();
        }
    }

    public void deleteClicked(View view) {
        formula.undoAppend();
        unknown.setText(formula.getUserEntry());
    }

    public void operatorClicked(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        if (formula == null && savedInstanceState == null) {
            prepareNewFormula();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(getClass().getSimpleName(), "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        outState.putParcelable("formula", formula);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.d(getClass().getSimpleName(), "onRestoreInstanceState()");
        super.onRestoreInstanceState(state);
        formula = state.getParcelable("formula");
    }

    @Override
    protected void onResume() {
        Log.d(getClass().getSimpleName(), "onResume()");
        super.onResume();

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
        formula = FormulaGenerator.generateRandomFormula(definition);
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
        Log.d(getClass().getSimpleName(), "onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calc, menu);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);setHomeButtonEnabled(true)
//        actionBar.setIcon(R.drawable.ic_launcher);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(getClass().getSimpleName(), "onOptionsItemSelected()");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        Log.d(getClass().getSimpleName(), "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(getClass().getSimpleName(), "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.d(getClass().getSimpleName(), "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(getClass().getSimpleName(), "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getSimpleName(), "onDestroy()");
        super.onDestroy();
    }

    public FormulaDefinition getFormulaDefinition() {
        Values left = new Values(0, 99);
//        Values right = new Values(3, 60);
//        Values result = new Values().add(10).add(11).add(12);

        FormulaDefinition definition = new FormulaDefinition();
        definition.setLeftOperand(left);
        definition.setRightOperand(left);
        definition.setResult(left);
        definition.addOperator(Operator.PLUS);
        definition.addUnknown(FormulaPart.FIRST_OPERAND);

        return definition;
    }
}
