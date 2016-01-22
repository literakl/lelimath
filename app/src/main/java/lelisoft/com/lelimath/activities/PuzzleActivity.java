package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.logic.PuzzleLogicImpl;
import lelisoft.com.lelimath.view.Misc;
import lelisoft.com.lelimath.view.TilesView;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String logTag = PuzzleActivity.class.getSimpleName();
    TilesView tilesView;
    PuzzleLogic logic = new PuzzleLogicImpl();

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

        setContentView(R.layout.activity_puzzle);

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        logic.setFormulaDefinition(getDefaultFormulaDefinition());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String complexityPref = sharedPref.getString(GamePreferenceActivity.KEY_COMPLEXITY, "EASY");
        logic.setLevel(PuzzleLogic.Level.valueOf(complexityPref));

        tilesView = (TilesView) findViewById(R.id.tiles);
        tilesView.setBackgroundPicture(getPicture());
        tilesView.setLogic(logic);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_level_A10: {
                Values values = new Values(0, 10);
                FormulaDefinition definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_S10: {
                Values values = new Values(0, 10);
                FormulaDefinition definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_A20: {
                Values values = new Values(0, 20);
                FormulaDefinition definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_S20: {
                Values values = new Values(0, 20);
                FormulaDefinition definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_AS50: {
                Values values = new Values(0, 50);
                FormulaDefinition definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS).addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_AS100: {
                Values values = new Values(0, 100);
                FormulaDefinition definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS).addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_M3: {
                Values left = new Values(3), right = new Values(0, 10), result = new Values(0, 30);
                FormulaDefinition definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_M4: {
                Values left = new Values(4), right = new Values(0, 10), result = new Values(0, 40);
                FormulaDefinition definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_MD5: {
                Values left = new Values(5), right = new Values(0, 10), result = new Values(0, 50);
                FormulaDefinition definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_MD6: {
                Values left = new Values(6), right = new Values(0, 10), result = new Values(0, 60);
                FormulaDefinition definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_MD7: {
                Values left = new Values(7), right = new Values(0, 10), result = new Values(0, 70);
                FormulaDefinition definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_MD8: {
                Values left = new Values(8), right = new Values(0, 10), result = new Values(0, 80);
                FormulaDefinition definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            case R.id.nav_level_MD9: {
                Values left = new Values(9), right = new Values(0, 10), result = new Values(0, 90);
                FormulaDefinition definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                restartGame(definition);
                break;
            }
            default: {
                Toast.makeText(this, "Chybi handler!", Toast.LENGTH_LONG).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(GamePreferenceActivity.KEY_COMPLEXITY)) {
            String value = sharedPreferences.getString(key, "EASY");
            logic.setLevel(PuzzleLogic.Level.valueOf(value));
            restartGame(null);
        }
    }

    private void restartGame(FormulaDefinition definition) {
        if (definition != null) {
            logic.setFormulaDefinition(definition);
        }
        tilesView.setBackgroundPicture(getPicture());
        tilesView.setupGame(true);
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
                Intent intent = new Intent();
                intent.setClass(this, GamePreferenceActivity.class);
                startActivity(intent);
                return true;
/*
                if (! logic.getLevel().equals(level)) {
                    logic.setLevel(level);
                    restartGame(null);
                }
                break;
*/
            }
            default: {
                Toast.makeText(this, "Default", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private int getPicture() {
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onResume() {
        Log.d(logTag, "onResume()");
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
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
        tilesView.setLogic(null);// todo find proper place
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
