package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Iterator;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.view.FormulaResultPair;
import lelisoft.com.lelimath.view.Tile;

/**
 * Puzzle view
 * Created by Leoš on 23.01.2016.
 */
public class PuzzleFragment extends Fragment {
    private static final String logTag = PuzzleFragment.class.getSimpleName();
    private PuzzleBridge callback;
    GridLayout puzzleGrid;
    AppCompatButton selectedButton;
    HandleClick clickHandler;
    Animation shake;
    int maxHorizontalTiles, maxVerticalTiles;
    ColorStateList colorsNormal, colorsSelected, colorsSolved;

    PuzzleLogic logic;
    int picture;

    public interface PuzzleBridge {
        void puzzleFinished();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(logTag, "onCreateView()");
        return inflater.inflate(R.layout.fragment_puzzle_views, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(logTag, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
        puzzleGrid = (GridLayout) getActivity().findViewById(R.id.puzzle_grid);
        clickHandler = new HandleClick();
        colorsNormal = ColorStateList.valueOf(getResources().getColor(R.color.tile_normal));
        colorsSelected = ColorStateList.valueOf(getResources().getColor(R.color.tile_selected));
        colorsSolved = ColorStateList.valueOf(getResources().getColor(R.color.tile_solved));

        // http://stackoverflow.com/questions/3591784/getwidth-and-getheight-of-view-returns-0
        puzzleGrid.getViewTreeObserver().addOnGlobalLayoutListener(new CalculateDimensions());
    }

    @NonNull
    private AppCompatButton inflateButton() {
        AppCompatButton button = (AppCompatButton) getActivity().getLayoutInflater().inflate(R.layout.template_puzzle, puzzleGrid, false);
        button.setSupportBackgroundTintList(colorsNormal);
        button.setOnClickListener(clickHandler);
        return button;
    }

    public void restartGame() {
        puzzleGrid.removeAllViews();
        generateTiles();
    }

    public void setLogic(PuzzleLogic logic) {
        this.logic = logic;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented the callback interface
        try {
            callback = (PuzzleBridge) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PuzzleBridge");
        }
    }

    public class HandleClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Tile currentTile = (Tile) view.getTag(R.id.button_tile);
            if (currentTile == null) {
                return;
            }
            AppCompatButton currentButton = (AppCompatButton) view;

            if (view == selectedButton) {
                currentButton.setSupportBackgroundTintList(colorsNormal);
                selectedButton = null;
            } else {
                if (selectedButton != null) {
                    Tile selectedTile = (Tile) selectedButton.getTag(R.id.button_tile);
                    if (selectedTile.matches(currentTile)) {
                        currentButton.setText("");
                        currentButton.setSupportBackgroundTintList(colorsSolved);
                        currentButton.setClickable(false);

                        selectedButton.setText("");
                        selectedButton.setSupportBackgroundTintList(colorsSolved);
                        selectedButton.setClickable(false);
                        selectedButton = null;
                    } else {
                        selectedButton.setSupportBackgroundTintList(colorsNormal);
                        selectedButton.startAnimation(shake);
                        selectedButton = null;
                        currentButton.startAnimation(shake);
                    }
                } else {
                    selectedButton = currentButton;
                    currentButton.setSupportBackgroundTintList(colorsSelected);
                }
            }
        }
    }

    public class CalculateDimensions implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            puzzleGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            AppCompatButton button = inflateButton();
            button.setText(logic.getSampleFormula());
            button.measure(500, 500);
            int tileWidth = button.getMeasuredWidth();
            Log.d(logTag, "CalculateDimensions: height=" + puzzleGrid.getHeight() + " width=" + puzzleGrid.getWidth() + ", tile=" + tileWidth);

            maxHorizontalTiles = Math.min((int) Math.floor(puzzleGrid.getWidth() / tileWidth), logic.getLevel().x);
            maxVerticalTiles = logic.getLevel().y;

            puzzleGrid.setColumnCount(maxHorizontalTiles);
            generateTiles();
        }
    }

    private void generateTiles() {
        AppCompatButton button;List<FormulaResultPair> tilesValues = logic.generateFormulaResultPairs(maxHorizontalTiles * maxVerticalTiles);
        Iterator<FormulaResultPair> iterator = tilesValues.iterator();

        for (int i = 0; i < maxVerticalTiles; i++) {
            for (int j = 0; j < maxHorizontalTiles; j++) {
                if (iterator.hasNext()) {
                    Tile tile = new Tile(iterator.next());
                    button = inflateButton();
                    button.setText(tile.getText());
//                        button.setText("A");
                    button.setTag(R.id.button_tile, tile);
                    puzzleGrid.addView(button);
                }
            }
        }
    }
}
