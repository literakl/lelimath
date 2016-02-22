package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

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
        button.setText("38");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(shake);
            }
        });
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

    public class CalculateDimensions implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            Log.d(logTag, "CalculateDimensions, height: " + puzzleGrid.getHeight() + " width: " + puzzleGrid.getWidth());
            puzzleGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            // http://stackoverflow.com/questions/13719103/how-to-retrieve-style-attributes-programatically-from-styles-xml
            int[] attrs = {android.R.attr.textSize, android.R.attr.padding};
            TypedArray ta = getActivity().obtainStyledAttributes(R.style.PuzzleTileButton, attrs);
            int textSize = ta.getDimensionPixelSize(0, -1);
            int tilePadding = ta.getDimensionPixelSize(1, 0);
            ta.recycle();

            String sampleFormula = logic.getSampleFormula();
            Paint textPaint = new TextPaint();
            textPaint.setTextSize(textSize);

            int tileWidth = (int) textPaint.measureText(sampleFormula) + tilePadding;
            int maxHorizontalTiles = Math.min((int) Math.floor(puzzleGrid.getWidth() / tileWidth) - 2, logic.getLevel().x);
            Log.d(logTag, "maxHorizontalTiles = " + maxHorizontalTiles);
            int maxVerticalTiles = logic.getLevel().y;

            puzzleGrid.setColumnCount(maxHorizontalTiles);

            List<FormulaResultPair> tilesValues = logic.generateFormulaResultPairs(maxHorizontalTiles * maxVerticalTiles);
            for (FormulaResultPair pair : tilesValues) {
                Button button = inflateButton();
                Tile tile = new Tile(pair);
                button.setText(tile.getText());
                puzzleGrid.addView(button);
            }
        }
    }
}
