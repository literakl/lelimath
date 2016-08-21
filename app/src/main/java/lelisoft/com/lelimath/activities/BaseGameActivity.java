package lelisoft.com.lelimath.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.dao.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.OperatorDefinition;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.logic.GameLogic;
import lelisoft.com.lelimath.logic.PuzzleLogic;

import static lelisoft.com.lelimath.fragment.PracticeSimpleSettingsFragment.KEY_SIMPLE_PRACTICE_SETTINGS;
import static lelisoft.com.lelimath.fragment.PracticeSimpleSettingsFragment.KEY_FIRST_ARG;
import static lelisoft.com.lelimath.fragment.PracticeSimpleSettingsFragment.KEY_SECOND_ARG;
import static lelisoft.com.lelimath.fragment.PracticeSimpleSettingsFragment.KEY_RESULT;

/**
 * Base class for game activities. It has an action bar, it can load FormulaDefinition
 * from SharedPreferences and it can measure time.
 * Created by Leoš on 09.04.2016.
 */
public class BaseGameActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(BaseGameActivity.class);

    GameLogic gameLogic;
    SharedPreferences sharedPref;

    public static int[] pictures = new int[] {
            R.drawable.pic_dragon,
            R.drawable.pic_genie,
            R.drawable.pic_kitten,
            R.drawable.pic_knight_boy,
            R.drawable.pic_knight_girl,
            R.drawable.pic_lion,
            R.drawable.pic_little_witch,
            R.drawable.pic_orc,
            R.drawable.pic_pirate,
            R.drawable.pic_pirate_parrot,
            R.drawable.pic_pirate_ship,
            R.drawable.pic_pixie,
            R.drawable.pic_playing_tiger,
            R.drawable.pic_princess_in_yellow,
            R.drawable.pic_princess_on_horse,
            R.drawable.pic_puppy,
            R.drawable.pic_queen_on_throne,
            R.drawable.pic_sitting_panda,
            R.drawable.pic_treasure
    };

    protected void storePlayRecord(PlayRecord record) {
        try {
            if (record == null) {
                return;
            }

            Dao<PlayRecord, Integer> dao = getHelper().getPlayRecordDao();
            int result = dao.create(record);
            if (result == 1) {
                log.debug("Saved PlayRecord");
            } else {
                log.warn("Failed to store {}", record);
            }

            LeliMathApp.getInstance().setLastFormulaDate(record.getDate().getTime());
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Failed to store {}", record, e);
        }
    }

    protected void storePlay(Play play) {
        try {
            if (play == null) {
                return;
            }

            Dao<Play, Integer> dao = getHelper().getPlayDao();
            int result;
            if (play.getId() == null) {
                result = dao.create(play);
            } else {
                result = dao.update(play);
            }
            if (result == 1) {
                log.debug("Play stored");
            } else {
                log.warn("Failed to store {}", play);
            }
        } catch (SQLException e) {
            Crashlytics.logException(e);
            log.error("Failed to store {}", play, e);
        }
    }

    protected void initializeGameLogic() {
        String prefComplexity = sharedPref.getString(GamePreferenceActivity.KEY_COMPLEXITY, "EASY");
        gameLogic.setLevel(PuzzleLogic.Level.valueOf(prefComplexity));

        FormulaDefinition definition = new FormulaDefinition().addUnknown(FormulaPart.RESULT);
        gameLogic.setFormulaDefinition(definition);

        boolean easy = sharedPref.getBoolean(KEY_SIMPLE_PRACTICE_SETTINGS, true);
        if (easy) {
            initializeFromSimpleSettings(definition);
        } else {
            initializeFromAdvancedSettings(definition);
        }
        log.debug(definition.toString());
    }

    private void initializeFromSimpleSettings(FormulaDefinition definition) {
        OperatorDefinition operatorPlus = new OperatorDefinition(Operator.PLUS);
        definition.addOperator(operatorPlus);
        OperatorDefinition operatorMinus = new OperatorDefinition(Operator.MINUS);
        definition.addOperator(operatorMinus);
        OperatorDefinition operatorMultiply = new OperatorDefinition(Operator.MULTIPLY);
        definition.addOperator(operatorMultiply);
        OperatorDefinition operatorDivide = new OperatorDefinition(Operator.DIVIDE);
        definition.addOperator(operatorDivide);

        Values firstArgValues = readSimpleValues(KEY_FIRST_ARG);
        Values secondArgValues = readSimpleValues(KEY_SECOND_ARG);
        Values resultValues = readSimpleValues(KEY_RESULT);

        operatorPlus.setFirstOperand(firstArgValues);
        operatorPlus.setSecondOperand(secondArgValues);
        operatorPlus.setResult(resultValues);

        operatorMinus.setFirstOperand(resultValues);
        operatorMinus.setSecondOperand(secondArgValues);
        operatorMinus.setResult(firstArgValues);

        operatorMultiply.setFirstOperand(firstArgValues);
        operatorMultiply.setSecondOperand(secondArgValues);
        operatorMultiply.setResult(resultValues);

        operatorDivide.setFirstOperand(resultValues);
        operatorDivide.setSecondOperand(secondArgValues);
        operatorDivide.setResult(firstArgValues);
    }

    private void initializeFromAdvancedSettings(FormulaDefinition definition) {
        Map<String,OperatorDefinition> definitions = new HashMap<>();
        Map<String, String> dependencies = new HashMap<>();

        String[] operations = new String[]{"plus", "minus", "multiply", "divide"};
        for (String operation : operations) {
            OperatorDefinition operator = new OperatorDefinition(Operator.valueOf(operation.toUpperCase()));
            definitions.put(operation, operator);

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
            OperatorDefinition dependingOperatorDef = definitions.get(operation);
            String targetKey = dependencies.get(operation);
            OperatorDefinition targetOperatorDef = definitions.get(targetKey);
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
                definitions.remove(operation);
            }
        }

        definition.setOperatorDefinitions(new ArrayList<>(definitions.values()));
    }

    protected Values readValues(String key) {
        String sValues = sharedPref.getString(key, null);
        if (sValues != null && sValues.trim().length() > 0) {
            try {
                return Values.parse(sValues);
            } catch (IllegalArgumentException e) {
                Crashlytics.logException(e);
                log.warn("Wrong input for key " + key + ", was: " + sValues);
                return Values.DEMO;
            }
        }
        return Values.UNDEFINED;
    }

    protected Values readSimpleValues(String key) {
        int value = sharedPref.getInt(key, 10);
        return Values.fromRange(0, value);
    }

    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    protected int selectRandomPicture() {
        return pictures[Misc.getRandom().nextInt(pictures.length)];
    }

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
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

dashboard -> activity -> zpet

05-22 10:10:23.017 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: DashboardActivity.onPause()
05-22 10:10:23.027 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: BadgeAwardActivity.onCreate()
05-22 10:10:23.067 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: BadgeAwardActivity.onStart()
05-22 10:10:23.068 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: BadgeAwardActivity.onResume()
05-22 10:10:23.517 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: DashboardActivity.onSaveInstanceState()
05-22 10:10:23.534 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: DashboardActivity.onStop()
05-22 10:10:24.074 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: BadgeAwardActivity.onPause()
05-22 10:10:24.081 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: DashboardActivity.onRestart()
05-22 10:10:24.083 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: DashboardActivity.onStart()
05-22 10:10:24.083 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: DashboardActivity.onResume()
05-22 10:10:24.408 11939-11939/lelisoft.com.lelimath D/l.c.l.a.LeliBaseActivity: BadgeAwardActivity.onStop()

*/