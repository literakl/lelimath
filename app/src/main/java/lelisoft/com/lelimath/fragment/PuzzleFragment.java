package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.view.TilesView;

/**
 * Puzzle view
 * Created by Leoš on 23.01.2016.
 */
public class PuzzleFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final String logTag = PuzzleFragment.class.getSimpleName();
    private PuzzleBridge callback;
    TilesView tilesView;

    public interface PuzzleBridge {
        int getPicture();
        FormulaDefinition getDefaultFormulaDefinition();
        PuzzleLogic getLogic();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(logTag, "onCreateView()");
        return inflater.inflate(R.layout.fragment_puzzle_navigation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(logTag, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tilesView = (TilesView) getActivity().findViewById(R.id.tiles);
        tilesView.setBackgroundPicture(callback.getPicture());
        tilesView.setLogic(callback.getLogic());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (PuzzleBridge) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PuzzleBridge");
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FormulaDefinition definition = null;
        switch (item.getItemId()) {
            case R.id.nav_level_A10: {
                Values values = new Values(0, 10);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_S10: {
                Values values = new Values(0, 10);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_A20: {
                Values values = new Values(0, 20);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_S20: {
                Values values = new Values(0, 20);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_AS50: {
                Values values = new Values(0, 50);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS).addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_AS100: {
                Values values = new Values(0, 100);
                definition = new FormulaDefinition(values, values, values)
                        .addOperator(Operator.PLUS).addOperator(Operator.MINUS)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_M3: {
                Values left = new Values(3), right = new Values(0, 10), result = new Values(0, 30);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_M4: {
                Values left = new Values(4), right = new Values(0, 10), result = new Values(0, 40);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD5: {
                Values left = new Values(5), right = new Values(0, 10), result = new Values(0, 50);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD6: {
                Values left = new Values(6), right = new Values(0, 10), result = new Values(0, 60);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD7: {
                Values left = new Values(7), right = new Values(0, 10), result = new Values(0, 70);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD8: {
                Values left = new Values(8), right = new Values(0, 10), result = new Values(0, 80);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            case R.id.nav_level_MD9: {
                Values left = new Values(9), right = new Values(0, 10), result = new Values(0, 90);
                definition = new FormulaDefinition(left, right, result)
                        .addOperator(Operator.MULTIPLY).addOperator(Operator.DIVIDE)
                        .addUnknown(FormulaPart.RESULT);
                break;
            }
            default: {
                Toast.makeText(getContext(), "Chybi handler!", Toast.LENGTH_LONG).show();
            }
        }

        if (definition != null) {
            callback.getLogic().setFormulaDefinition(definition);
            tilesView.setBackgroundPicture(callback.getPicture());
            tilesView.setupGame(true);
        }

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
