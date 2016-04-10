package lelisoft.com.lelimath.logic;

/**
 * Implementation for handling all Puzzle calculations
 * Created by Leoš on 03.12.2015.
 */
public class PuzzleLogicImpl extends GameLogicImpl implements PuzzleLogic {

    public int getFirstOperandMaximumLength() {
        return definition.getValuesMaximumLength(definition.getFirstOperand());
    }

    public int getSecondOperandMaximumLength() {
        return definition.getValuesMaximumLength(definition.getSecondOperand());
    }

    @Override
    public String getSampleFormula() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getFirstOperandMaximumLength(); i++) {
            sb.append("3");
        }
        sb.append(" + ");
        for (int i = 0; i < getSecondOperandMaximumLength(); i++) {
            sb.append("3");
        }
        return sb.toString();
    }
}
