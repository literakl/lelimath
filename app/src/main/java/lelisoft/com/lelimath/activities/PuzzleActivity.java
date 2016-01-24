package lelisoft.com.lelimath.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.fragment.GamePreferenceFragment;
import lelisoft.com.lelimath.fragment.PuzzleFragment;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;
import lelisoft.com.lelimath.view.Misc;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends AppCompatActivity implements PuzzleFragment.PuzzleBridge {
    private static final String logTag = PuzzleActivity.class.getSimpleName();
    PuzzleLogic logic = new PuzzleLogicImpl();
    PuzzleFragment fragment;

    static int[] pictures = new int[] {
            R.drawable.pic_cat_kitten,
            R.drawable.pic_child_ball_on_head,
            R.drawable.pic_cute_girl,
            R.drawable.pic_girl_teddy_bear,
            R.drawable.pic_green_snake,
            R.drawable.pic_kid_airplane,
            R.drawable.pic_koalas_on_tree,
            R.drawable.pic_owl
    };

    @Override
    protected void onCreate(Bundle state) {
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_puzzle_fragments);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = new PuzzleFragment();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();

        logic.setFormulaDefinition(getDefaultFormulaDefinition());
        logic.setLevel(PuzzleLogic.Level.EASY);
    }

    @Override
    public PuzzleLogic getLogic() {
        return logic;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Toast.makeText(this, "Settings!", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.action_level: {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, new GamePreferenceFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
            default: {
                Toast.makeText(this, "Default", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public int getPicture() {
//        return R.drawable.pstros_500;
        return pictures[Misc.getRandom().nextInt(pictures.length)];
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        Log.d(logTag, "onSaveInstanceState()");
        super.onSaveInstanceState(state);
//        state.putParcelable("formula", formula);
    }

    @Override
    protected void onPause() {
        Log.d(logTag, "onPause()");
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.d(logTag, "onResume()");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d(logTag, "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.d(logTag, "onRestoreInstanceState()");
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onRestart() {
        Log.d(logTag, "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.d(logTag, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(logTag, "onDestroy()");
//        tilesView.setLogic(null);// todo find proper place
        super.onDestroy();
    }

    public FormulaDefinition getDefaultFormulaDefinition() {
        Values values = new Values(0, 30);
        return new FormulaDefinition(values, values, values)
                .addOperator(Operator.PLUS).addOperator(Operator.MINUS)
                .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                .addUnknown(FormulaPart.RESULT);
    }
}
