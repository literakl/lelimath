package lelisoft.com.lelimath.logic;

/**
 * Puzzle preferences and calculations
 * Created by Leoš on 03.12.2015.
 */
public interface PuzzleLogic extends GameLogic {

    int getFirstOperandMaximumLength();

    int getSecondOperandMaximumLength();

    String getSampleFormula();
}
