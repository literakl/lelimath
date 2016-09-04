package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.logic.PuzzleLogic;
import lelisoft.com.lelimath.provider.PlayProvider;
import lelisoft.com.lelimath.view.FormulaResultPair;
import lelisoft.com.lelimath.view.Tile;

/**
 * Puzzle view
 * Created by Leoš on 23.01.2016.
 */
public class PuzzleFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(PuzzleFragment.class);

    PuzzleBridge callback;
    HandleClick clickHandler;
    GridLayout puzzleGrid;
    AppCompatButton selectedButton;
    Animation shake;
    int maxHorizontalTiles, maxVerticalTiles, tilesToBeSolved;
    PuzzleLogic logic;
    Play play;
    long started, stopped;

    public interface PuzzleBridge {
        void puzzleFinished();
        void savePlayRecord(Play play, PlayRecord record);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.frg_puzzle, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        log.debug("onActivityCreated({})", state);
        super.onActivityCreated(state);

        if (state != null) {
            logic = (PuzzleLogic) state.getSerializable("logic");
            started = state.getLong("started");
            stopped = state.getLong("stopped");
            play = state.getParcelable("play");
        } else {
            Metrics.saveGameStarted(Game.PUZZLE, logic.getLevel());
        }

        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
        puzzleGrid = (GridLayout) getActivity().findViewById(R.id.puzzle_grid);
        clickHandler = new HandleClick();

        puzzleGrid.getViewTreeObserver().addOnGlobalLayoutListener(new CalculateDimensions());
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        log.debug("onSaveInstanceState()");
        super.onSaveInstanceState(state);
        state.putSerializable("logic", logic);
        state.putParcelable("play", play);
        state.putLong("started", started);
        state.putLong("stopped", stopped);
        state.putLong("stoped", stopped);
    }

    @NonNull
    private AppCompatButton inflateButton(Tile tile, LayoutInflater inflater) {
        AppCompatButton button = (AppCompatButton) inflater.inflate(R.layout.tmpl_puzzle_tile, puzzleGrid, false);
        button.setBackgroundResource((tile.getFormula() != null) ? R.drawable.bg_tile_formula : R.drawable.bg_tile_result);
        button.setOnClickListener(clickHandler);
        button.setText(tile.getText());
        button.setTag(R.id.button_tile, tile);
        return button;
    }

    private void generateTiles() {
        log.debug("generateTiles(horizontal = {}, vertical = {})", maxHorizontalTiles, maxVerticalTiles);
        List<FormulaResultPair> tilesValues = logic.generateFormulaResultPairs(maxHorizontalTiles * maxVerticalTiles);
        if (tilesValues.isEmpty()) {
            tilesValues.add(new FormulaResultPair(new Formula(1, 1, 2, Operator.PLUS, FormulaPart.RESULT)));
            tilesValues.add(new FormulaResultPair(2));
            Toast.makeText(getContext(), R.string.error_no_formula_generated, Toast.LENGTH_LONG).show();
        }
        tilesToBeSolved = tilesValues.size();
        setupPlay();

        Iterator<FormulaResultPair> iterator = tilesValues.iterator();
        for (int i = 0; i < maxVerticalTiles; i++) {
            for (int j = 0; j < maxHorizontalTiles; j++) {
                if (iterator.hasNext()) {
                    Tile tile = new Tile(iterator.next());
                    AppCompatButton button = inflateButton(tile, getActivity().getLayoutInflater());
                    puzzleGrid.addView(button);
                }
            }
        }
    }

    private void setupPlay() {
        play = new Play();
        play.setGame(Game.PUZZLE);
        play.setLevel(logic.getLevel());
        play.setDate(new Date());
        play.setCount(tilesToBeSolved / 2);

        PlayProvider provider = new PlayProvider(getActivity());
        provider.create(play);
    }

    public class HandleClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Tile currentTile = (Tile) view.getTag(R.id.button_tile);
            if (currentTile == null) {
                return;
            }
            log.debug("onClick({})", currentTile);
            AppCompatButton currentButton = (AppCompatButton) view;
            startRecordingSpentTime();

            if (view == selectedButton) {
                currentButton.setBackgroundResource((currentTile.getFormula() != null) ? R.drawable.bg_tile_formula : R.drawable.bg_tile_result);
                selectedButton = null;
            } else {
                if (selectedButton != null) {
                    Tile selectedTile = (Tile) selectedButton.getTag(R.id.button_tile);
                    if (selectedTile.matches(currentTile)) {
                        log.debug("{} matches {}", currentTile, selectedTile);
                        PlayRecord record = getPlayRecord(true, selectedTile, currentTile);
                        setPoints(record, play);
                        updateSpentTime(record);

                        tilesToBeSolved -= 2;
                        if (tilesToBeSolved <= 0) {
                            play.setFinished(true);
                            callback.savePlayRecord(play, record);
                            callback.puzzleFinished();
                            Metrics.saveGameFinished(Game.PUZZLE, logic.getLevel());
                        } else {
                            callback.savePlayRecord(play, record);
                            LeliMathApp.getInstance().playSound(R.raw.correct);

                            currentButton.setText("");
                            currentButton.setBackgroundResource(R.drawable.bg_tile_solved);
                            currentButton.setClickable(false);

                            selectedButton.setText("");
                            selectedButton.setBackgroundResource(R.drawable.bg_tile_solved);
                            selectedButton.setClickable(false);
                            selectedButton = null;
                        }
                    } else {
                        log.debug(currentTile + " does not match " + selectedTile);
                        PlayRecord record = getPlayRecord(false, selectedTile, currentTile);
                        if (record != null) {
                            updateSpentTime(record);
                            callback.savePlayRecord(play, record);
                        }
                        LeliMathApp.getInstance().playSound(R.raw.incorrect);

                        selectedButton.setBackgroundResource((selectedTile.getFormula() != null) ? R.drawable.bg_tile_formula : R.drawable.bg_tile_result);
                        selectedButton.startAnimation(shake);
                        selectedButton = null;
                        currentButton.startAnimation(shake);
                    }
                } else {
                    selectedButton = currentButton;
                    currentButton.setBackgroundResource(R.drawable.bg_tile_selected);
                }
            }
        }
    }

    private PlayRecord getPlayRecord(boolean correct, @NonNull Tile first, @NonNull Tile second) {
        if ((first.getFormula() != null && second.getFormula() != null) || (first.getResult() != null && second.getResult() != null)) {
            return null; // chyba v ovladani, ne ve vysledku
        }

        PlayRecord record = new PlayRecord();
        record.setPlay(play);
        record.setDate(new Date());
        record.setCorrect(correct);
        record.setUnknown(FormulaPart.RESULT);

        if (first.getFormula() != null) {
            record.setFormula(first.getFormula());
            if (!correct) {
                record.setWrongValue(second.getResult().toString());
            }
        } else if (second.getFormula() != null) {
            record.setFormula(second.getFormula());
            if (!correct) {
                record.setWrongValue(first.getResult().toString());
            }
        }
        return record;
    }

    /**
     * Calculates points for correctly solved formula. Puzzle is easy so we divide number of digits
     * by three for easy games and by two for hard games.
     */
    protected void setPoints(PlayRecord record, Play play) {
        StringBuilder sb = new StringBuilder().append(record.getFirstOperand());
        sb.append(record.getSecondOperand()).append(record.getResult());
        int length = sb.length();
        if (play.getCount() >= 10) {
            record.setPoints(Math.max(1, length / 2 - 1));
            log.debug("{} za {} hard", record.getPoints(), record.getFormulaString());
        } else {
            record.setPoints(Math.max(1, length / 3 - 2));
            log.debug("{} za {} eas", record.getPoints(), record.getFormulaString());
        }
    }

    protected void updateSpentTime(PlayRecord playRecord) {
        super.updateSpentTime(playRecord);
        play.addTimeSpent(playRecord.getTimeSpent());
    }

    public class CalculateDimensions implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            puzzleGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            AppCompatButton button = (AppCompatButton) inflater.inflate(R.layout.tmpl_puzzle_tile, puzzleGrid, false);
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
