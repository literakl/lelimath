package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.FormulaRecord;
import lelisoft.com.lelimath.fragment.CalcFragment;
import lelisoft.com.lelimath.fragment.PictureFragment;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.logic.CalcLogic;
import lelisoft.com.lelimath.logic.CalcLogicImpl;


public class CalcActivity extends BaseGameActivity implements CalcFragment.CalcBridge {
    private static final Logger log = LoggerFactory.getLogger(CalcActivity.class);

    CalcFragment calcFragment;
    PictureFragment pictureFragment;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_calc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCalc);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(CalcActivity.this);
                }
            });
        }

        setGameLogic(new CalcLogicImpl());
        initializeGameLogic();

        calcFragment = new CalcFragment();
        calcFragment.setLogic((CalcLogic) gameLogic);
        initializeCalcFragment(false);
    }

    @Override
    public void calcFinished() {
        log.debug("calcFinished()");
        pictureFragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putInt(PictureFragment.ARG_PICTURE, PuzzleActivity.pictures[Misc.getRandom().nextInt(PuzzleActivity.pictures.length)]);
        pictureFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.calc_content, pictureFragment);
        transaction.commit();
    }

    @Override
    public void saveFormulaRecord(FormulaRecord record) {
        storeFormulaRecord(record);
    }

    private void initializeCalcFragment(boolean replace) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (replace) {
            transaction.replace(R.id.calc_content, calcFragment);
        } else {
            transaction.add(R.id.calc_content, calcFragment);
        }
        transaction.commit();
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

    public void restartGame(View view) {
        log.debug("restartGame(), calcFragment " + ((calcFragment == null) ? "is not null" : "is null"));
        calcFragment = new CalcFragment();
        calcFragment.setLogic((CalcLogic) gameLogic);
        initializeCalcFragment(true);
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, CalcActivity.class));
    }
}
