package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

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
    Button selectedButton;
    Animation shake;

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

        // http://stackoverflow.com/questions/3591784/getwidth-and-getheight-of-view-returns-0
        puzzleGrid.getViewTreeObserver().addOnGlobalLayoutListener(new CalculateDimensions());
        restartGame();
    }

    @NonNull
    private Button inflateButton() {
        Button button = (Button) getActivity().getLayoutInflater().inflate(R.layout.template_puzzle, puzzleGrid, false);
        button.setOnClickListener(new HandleClick());
        return button;
    }

    public void restartGame() {
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
//ViewCompat.setBackgroundTintList(view, colorList)
            if (view == selectedButton) {
                currentTile.setSelected(false);
                view.setBackground(getResources().getDrawable(R.drawable.unknown_box));
//                ViewCompat.setBackgroundTintList(view, getResources().getColorStateList(R.color.gray_215));
                selectedButton = null;
            } else {
                if (selectedButton != null) {
                    Tile selectedTile = (Tile) selectedButton.getTag(R.id.button_tile);
                    if (selectedTile.matches(currentTile)) {
                        currentTile.setUncovered(true);
                        ((Button)view).setText("");
                        view.setBackground(getResources().getDrawable(R.drawable.solved_tile));
//                        ViewCompat.setBackgroundTintList(view, getResources().getColorStateList(R.color.green_215));
                        view.setClickable(false);
                        selectedTile.setUncovered(true);
                        selectedTile.setSelected(false);
                        selectedButton.setText("");
                        selectedButton.setBackground(getResources().getDrawable(R.drawable.solved_tile));
//                        ViewCompat.setBackgroundTintList(selectedButton, getResources().getColorStateList(R.color.green_215));
                        selectedButton.setClickable(false);
                        selectedButton = null;
                    } else {
                        selectedTile.setSelected(false);
                        selectedButton.setBackground(getResources().getDrawable(R.drawable.unknown_box));
                        selectedButton.startAnimation(shake);
//                        ViewCompat.setBackgroundTintList(selectedButton, getResources().getColorStateList(R.color.gray_215));
                        selectedButton.startAnimation(shake);
                        selectedButton = null;
                        view.startAnimation(shake);
                    }
                } else {
                    currentTile.setSelected(true);
                    selectedButton = (Button) view;
                    view.setBackground(getResources().getDrawable(R.drawable.selected_tile));
//                    ViewCompat.setBackgroundTintList(selectedButton, getResources().getColorStateList(R.color.colorAccent));
                }
            }
        }
    }

    public class CalculateDimensions implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            puzzleGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            Button button = inflateButton();
            button.setText(logic.getSampleFormula());
            button.measure(500, 500);
            int tileWidth = button.getMeasuredWidth();
            Log.d(logTag, "CalculateDimensions: height=" + puzzleGrid.getHeight() + " width=" + puzzleGrid.getWidth() + ", tile=" + tileWidth);

            int maxHorizontalTiles = Math.min((int) Math.floor(puzzleGrid.getWidth() / tileWidth), logic.getLevel().x);
            int maxVerticalTiles = logic.getLevel().y;

            puzzleGrid.setColumnCount(maxHorizontalTiles);

            List<FormulaResultPair> tilesValues = logic.generateFormulaResultPairs(maxHorizontalTiles * maxVerticalTiles);
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
}
