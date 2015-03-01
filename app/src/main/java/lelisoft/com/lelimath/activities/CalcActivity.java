package lelisoft.com.lelimath.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    public void operandClicked(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        prepareNewFormula();
    }

    private void prepareNewFormula() {
        formula = FormulaGenerator.generateRandomFormula(definition);
        ((TextView)findViewById(R.id.operandFirst)).setText(formula.getFirstOperand().toString());
        ((TextView)findViewById(R.id.operator)).setText(formula.getOperator().toString());
        ((TextView)findViewById(R.id.operandSecond)).setText(formula.getSecondOperand().toString());
        ((TextView)findViewById(R.id.result)).setText(formula.getResult().toString());

        unknown = getUnknownWidget(formula);
        unknown.setText("");
        unknown.setTextAppearance(this, R.style.FormulaUnknownValue);
//        unknown.setBackgroundColor(Color.rgb(0, 255, 0));
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calc, menu);
        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);setHomeButtonEnabled(true)
//        actionBar.setIcon(R.drawable.ic_launcher);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    public FormulaDefinition getFormulaDefinition() {
        Values left = new Values(0, 99);
//        Values right = new Values(3, 60);
//        Values result = new Values().add(10).add(11).add(12);

        FormulaDefinition definition = new FormulaDefinition();
        definition.setLeftOperand(left);
        definition.setRightOperand(left);
        definition.setResult(left);
        definition.addOperator(Operator.PLUS);
        definition.addUnknown(FormulaPart.RESULT);

        return definition;
    }
}
