package lelisoft.com.lelimath.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.OperatorDefinition;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.logic.GameLogic;
import lelisoft.com.lelimath.logic.PuzzleLogic;

/**
 * Base class for game activities. It has an action bar, it can load FormulaDefinition
 * from SharedPreferences and it can measure time.
 * Created by Leoš on 09.04.2016.
 */
public class BaseGameActivity extends AppCompatActivity {
    private static final Logger log = LoggerFactory.getLogger(BaseGameActivity.class);

    GameLogic gameLogic;

    SharedPreferences sharedPref;

    protected void initializeGameLogic() {
        String prefComplexity = sharedPref.getString(GamePreferenceActivity.KEY_COMPLEXITY, "EASY");
        gameLogic.setLevel(PuzzleLogic.Level.valueOf(prefComplexity));

        FormulaDefinition definition = new FormulaDefinition().addUnknown(FormulaPart.RESULT);
        gameLogic.setFormulaDefinition(definition);

        Map<String,OperatorDefinition> operatorDefinitions = new HashMap<>();
        Map<String, String> dependencies = new HashMap<>();

        String[] operations = new String[]{"plus", "minus", "multiply", "divide"};
        for (String operation : operations) {
            OperatorDefinition operator = new OperatorDefinition(Operator.valueOf(operation.toUpperCase()));
            operatorDefinitions.put(operation, operator);

            String dependsOn = sharedPref.getString("pref_game_" + operation + "_depends", "NONE");
            if ("NONE".equals(dependsOn)) {
                operator.setFirstOperand(readValues("pref_game_" + operation + "_first_arg"));
                operator.setSecondOperand(readValues("pref_game_" + operation + "_second_arg"));
                operator.setResult(readValues("pref_game_" + operation + "_result"));
            } else {
                dependencies.put(operation, dependsOn.toLowerCase());
            }
        }

/*

3+8=11 3*8=24 11-8=3 24/8=3

+ X Y Z   * X Y Z  - X Y Z   / X Y Z
--------  -------   -------  -------
- Z Y X   + X Y Z   + Z Y X  + Z Y X
* X Y Z   - Z Y X   * Z Y X  - X Y Z
/ Z Y X   / Z Y X   / X Y Z  * Z Y X

 */

        for (String operation : dependencies.keySet()) {
            OperatorDefinition dependingOperatorDef = operatorDefinitions.get(operation);
            String targetKey = dependencies.get(operation);
            OperatorDefinition targetOperatorDef = operatorDefinitions.get(targetKey);
            if (targetOperatorDef == null) {
                log.error("Dependency <" + operation + "," + targetKey + "> is missing!");
                continue;
            }

            dependingOperatorDef.setSecondOperand(targetOperatorDef.getSecondOperand());
            Operator targetOperator = targetOperatorDef.getOperator();
            switch (dependingOperatorDef.getOperator()) {
                case PLUS:
                case MULTIPLY:
                    if (targetOperator == Operator.MINUS || targetOperator == Operator.DIVIDE) {
                        dependingOperatorDef.setFirstOperand(targetOperatorDef.getResult());
                        dependingOperatorDef.setResult(targetOperatorDef.getFirstOperand());
                    } else {
                        dependingOperatorDef.setFirstOperand(targetOperatorDef.getFirstOperand());
                        dependingOperatorDef.setResult(targetOperatorDef.getResult());
                    }
                    break;
                default:
                    if (targetOperator == Operator.PLUS || targetOperator == Operator.MULTIPLY) {
                        dependingOperatorDef.setFirstOperand(targetOperatorDef.getResult());
                        dependingOperatorDef.setResult(targetOperatorDef.getFirstOperand());
                    } else {
                        dependingOperatorDef.setFirstOperand(targetOperatorDef.getFirstOperand());
                        dependingOperatorDef.setResult(targetOperatorDef.getResult());
                    }
            }
        }

        for (String operation : operations) {
            if (! sharedPref.getBoolean("pref_game_operation_" + operation, true)) {
                operatorDefinitions.remove(operation);
            }
        }

        definition.setOperatorDefinitions(new ArrayList<>(operatorDefinitions.values()));
        log.debug(definition.toString());
    }

    public Values readValues(String key) {
        String sValues = sharedPref.getString(key, null);
        if (sValues != null && sValues.trim().length() > 0) {
            try {
                return Values.parse(sValues);
            } catch (IllegalArgumentException e) {
                log.warn("Wrong input for key " + key + ", was: " + sValues);
                return Values.DEMO;
            }
        }
        return Values.UNDEFINED;
    }

    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        log.debug("onStart()");
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        log.debug("onSaveInstanceState()");
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onPause() {
        log.debug("onPause()");
        super.onPause();

    }

    @Override
    protected void onResume() {
        log.debug("onResume()");
        super.onResume();
    }

    @Override
    protected void onStop() {
        log.debug("onStop()");
        super.onStop();
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        log.debug("onRestoreInstanceState()");
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onRestart() {
        log.debug("onRestart()");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        log.debug("onDestroy()");
        super.onDestroy();
    }
}
/*
http://eigo.co.uk/labs/managing-state-in-an-android-activity/

start aplikace

03-15 11:21:09.020  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onCreate()
03-15 11:21:09.119  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onStart()
03-15 11:21:09.120  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onResume()
03-15 11:21:09.329  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onCreateOptionsMenu()
03-15 11:21:09.414      552-580/? I/ActivityManager﹕ Displayed lelisoft.com.lelimath/.activities.CalcActivity: +2s415ms

otoceni displeje

03-15 11:21:41.313  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onPause()
03-15 11:21:41.313  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onSaveInstanceState()
03-15 11:21:41.316  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onStop()
03-15 11:21:41.316  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onDestroy()
03-15 11:21:41.347  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onCreate()
03-15 11:21:41.397  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onStart()
03-15 11:21:41.397  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onRestoreInstanceState()
03-15 11:21:41.398  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onCreateOptionsMenu()
03-15 11:21:41.405  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onResume()
03-15 11:21:41.515      552-580/? I/WindowManager﹕ Screen frozen for +359ms due to Window{3b8a6545 u0 lelisoft.com.lelimath/lelisoft.com.lelimath.activities.CalcActivity}

prepnuti na seznam aplikaci

03-15 11:22:07.988  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onPause()
03-15 11:22:08.011  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onSaveInstanceState()
03-15 11:22:08.012  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onStop()

vraceni zpatky

03-15 11:22:14.303  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onRestart()
03-15 11:22:14.311  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onStart()
03-15 11:22:14.311  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onResume()

home screen

03-15 11:24:15.013  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onPause()
03-15 11:24:15.295  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onSaveInstanceState()
03-15 11:24:15.301  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onStop()

vraceni zpatky

03-15 11:24:54.134  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onRestart()
03-15 11:24:54.136  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onStart()
03-15 11:24:54.136  12499-12499/lelisoft.com.lelimath D/CalcActivity﹕ onResume()

*/