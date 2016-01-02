package lelisoft.com.lelimath.view;

import lelisoft.com.lelimath.data.Formula;

/**
 * Tile contains formula and visual attributes
 * Created by Leoš on 12.12.2015.
 */
public class Tile {
    boolean selected, uncovered;
    FormulaResultPair pair;
    float x, y, xx, yy;

    public Tile(float x, float y, float xx, float yy) {
        this.x = x;
        this.y = y;
        this.xx = xx;
        this.yy = yy;
    }

    public Formula getFormula() {
        return pair.formula;
    }

    public Integer getResult() {
        return pair.result;
    }

    public String getText() {
        if (pair.formula != null) {
            Formula formula = pair.formula;
            StringBuilder sb = new StringBuilder();
            sb.append(formula.getFirstOperand()).append(" ").append(formula.getOperator())
                    .append(" ").append(formula.getSecondOperand());
            return sb.toString();
        } else {
            return pair.result.toString();
        }
    }

    public void setFormulaResultPair(FormulaResultPair formula) {
        this.pair = formula;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isUncovered() {
        return uncovered;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setUncovered(boolean uncovered) {
        this.uncovered = uncovered;
    }

    /**
     * Verifies whether given point is inside this tile and outside of inactive touch margin area
     * @param pointX pointer X position
     * @param pointY pointer Y position
     * @param margin size of inactive area around this tile
     * @return true if point is within active area
     */
    public boolean isInside(float pointX, float pointY, float margin) {
        return ! uncovered && (x + margin <= pointX && pointX <= xx - margin && y + margin <= pointY && pointY <= yy - margin);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getXx() {
        return xx;
    }

    public float getYy() {
        return yy;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setXx(float xx) {
        this.xx = xx;
    }

    public void setYy(float yy) {
        this.yy = yy;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", xx=" + xx +
                ", yy=" + yy +
                '}';
    }
}
