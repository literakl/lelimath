package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.fragment.CalcFragment;
import lelisoft.com.lelimath.fragment.PictureFragment;
import lelisoft.com.lelimath.logic.BadgeEvaluationTask;
import lelisoft.com.lelimath.logic.CalcLogic;
import lelisoft.com.lelimath.logic.CalcLogicImpl;


public class CalcActivity extends BaseGameActivity implements CalcFragment.CalcBridge, PictureFragment.PictureBridge {
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
        new BadgeEvaluationTask(this).execute();

        pictureFragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putInt(PictureFragment.ARG_PICTURE, selectRandomPicture());
        pictureFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.calc_content, pictureFragment);
        transaction.commit();
    }

    @Override
    public void savePlayRecord(Play play, PlayRecord record) {
        // todo in background bug #42
        storePlay(play);
        storePlayRecord(record);
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

    @Override
    public void restartGame() {
        log.debug("restartGame()");
        calcFragment = new CalcFragment();
        calcFragment.setLogic((CalcLogic) gameLogic);
        initializeCalcFragment(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initializeGameLogic();
        restartGame();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.practice_settings:
                Intent intent = new Intent(this, PracticeSettingsActivity.class);
                startActivityForResult(intent, 1);
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice, menu);
        return true;
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, CalcActivity.class));
    }
}
