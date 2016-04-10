package lelisoft.com.lelimath.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
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

        Values defaultValues = new Values(0, 30), values = defaultValues;
        FormulaDefinition definition = new FormulaDefinition().addUnknown(FormulaPart.RESULT);
        gameLogic.setFormulaDefinition(definition);

        String sValues = sharedPref.getString(GamePreferenceActivity.KEY_FIRST_OPERAND, null);
        if (sValues != null) {
            values = Values.parse(sValues);
        }
        definition.setLeftOperand(values);

        sValues = sharedPref.getString(GamePreferenceActivity.KEY_SECOND_OPERAND, null);
        if (sValues != null) {
            values = Values.parse(sValues);
        } else {
            values = defaultValues;
        }
        definition.setRightOperand(values);

        sValues = sharedPref.getString(GamePreferenceActivity.KEY_RESULT, null);
        if (sValues != null) {
            values = Values.parse(sValues);
        } else {
            values = defaultValues;
        }
        definition.setResult(values);

        Set<String> mValues = sharedPref.getStringSet(GamePreferenceActivity.KEY_OPERATIONS, null);
        if (mValues != null) {
            for (String value : mValues) {
                definition.addOperator(Operator.valueOf(value));
            }
        } else {
            definition.addOperator(Operator.PLUS).addOperator(Operator.MINUS);
            definition.addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE);
        }
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