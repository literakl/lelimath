package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(PuzzleFragment.class);

    private PuzzleBridge callback;
    GridLayout puzzleGrid;
    AppCompatButton selectedButton;
    HandleClick clickHandler;
    Animation shake;
    int maxHorizontalTiles, maxVerticalTiles, tilesToBeSolved;
    PuzzleLogic logic;

    public interface PuzzleBridge {
        void puzzleFinished();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.fragment_puzzle_views, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
        puzzleGrid = (GridLayout) getActivity().findViewById(R.id.puzzle_grid);
        clickHandler = new HandleClick();

        puzzleGrid.getViewTreeObserver().addOnGlobalLayoutListener(new CalculateDimensions());
    }

    @NonNull
    private AppCompatButton inflateButton(Tile tile, LayoutInflater inflater) {
        AppCompatButton button = (AppCompatButton) inflater.inflate(R.layout.template_puzzle_tile, puzzleGrid, false);
        button.setBackgroundResource((tile.getFormula() != null) ? R.drawable.tile_formula : R.drawable.tile_result);
        button.setOnClickListener(clickHandler);
        button.setText(tile.getText());
        button.setTag(R.id.button_tile, tile);
        return button;
    }

    private void generateTiles() {
        log.debug("generateTiles(horizontal = " + maxHorizontalTiles + ", vertical = " + maxVerticalTiles + ")");
        AppCompatButton button;List<FormulaResultPair> tilesValues = logic.generateFormulaResultPairs(maxHorizontalTiles * maxVerticalTiles);
        tilesToBeSolved = tilesValues.size();
        Iterator<FormulaResultPair> iterator = tilesValues.iterator();

        for (int i = 0; i < maxVerticalTiles; i++) {
            for (int j = 0; j < maxHorizontalTiles; j++) {
                if (iterator.hasNext()) {
                    Tile tile = new Tile(iterator.next());
                    button = inflateButton(tile, getActivity().getLayoutInflater());
                    puzzleGrid.addView(button);
                }
            }
        }
    }

    public class HandleClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Tile currentTile = (Tile) view.getTag(R.id.button_tile);
            if (currentTile == null) {
                return;
            }
            log.debug("onClick(" + currentTile + ")");
            AppCompatButton currentButton = (AppCompatButton) view;

            if (view == selectedButton) {
                currentButton.setBackgroundResource((currentTile.getFormula() != null) ? R.drawable.tile_formula : R.drawable.tile_result);
                selectedButton = null;
            } else {
                if (selectedButton != null) {
                    Tile selectedTile = (Tile) selectedButton.getTag(R.id.button_tile);
                    if (selectedTile.matches(currentTile)) {
                        log.debug(currentTile + " matches " + selectedTile);
                        currentButton.setText("");
                        currentButton.setBackgroundResource(R.drawable.tile_solved);
                        currentButton.setClickable(false);

                        selectedButton.setText("");
                        selectedButton.setBackgroundResource(R.drawable.tile_solved);
                        selectedButton.setClickable(false);
                        selectedButton = null;

                        tilesToBeSolved -= 2;
                        if (tilesToBeSolved <= 0) {
                            callback.puzzleFinished();
                        }
                    } else {
                        log.debug(currentTile + " does not match " + selectedTile);
                        selectedButton.setBackgroundResource((selectedTile.getFormula() != null) ? R.drawable.tile_formula : R.drawable.tile_result);
                        selectedButton.startAnimation(shake);
                        selectedButton = null;
                        currentButton.startAnimation(shake);
                    }
                } else {
                    selectedButton = currentButton;
                    currentButton.setBackgroundResource(R.drawable.tile_selected);
                }
            }
        }
    }

    public class CalculateDimensions implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            puzzleGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            AppCompatButton button = (AppCompatButton) inflater.inflate(R.layout.template_puzzle_tile, puzzleGrid, false);
            button.setText(logic.getSampleFormula());
            button.measure(500, 500);
            int tileWidth = button.getMeasuredWidth();
            log.debug("CalculateDimensions: height=" + puzzleGrid.getHeight() + " width=" + puzzleGrid.getWidth() + ", tile=" + tileWidth);

            maxHorizontalTiles = Math.min((int) Math.floor(puzzleGrid.getWidth() / tileWidth), logic.getLevel().x);
            maxVerticalTiles = logic.getLevel().y;

            puzzleGrid.setColumnCount(maxHorizontalTiles);
            generateTiles();
        }
    }

    public void setLogic(PuzzleLogic logic) {
        this.logic = logic;
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
}
