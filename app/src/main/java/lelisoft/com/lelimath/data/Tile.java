package lelisoft.com.lelimath.data;

/**
 * Tile contains formula and visual attributes
 * Created by Leoš on 12.12.2015.
 */
public class Tile {
    boolean selected, uncovered, left, right, top, bottom;
    Formula formula;
    float x, y, xx, yy;

    public Tile(float x, float y, float xx, float yy) {
        this.x = x;
        this.y = y;
        this.xx = xx;
        this.yy = yy;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public boolean isMiddle() {
        return ! (left || right || top || bottom);
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

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isTop() {
        return top;
    }

    public boolean isBottom() {
        return bottom;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }

    /**
     * Verifies whether given point is inside this tile and outside of inactive touch margin area
     * @param pointX pointer X position
     * @param pointY pointer Y position
     * @param margin size of inactive area around this tile
     * @return true if point is within active area
     */
    public boolean isInside(float pointX, float pointY, float margin) {
        return (x + margin <= pointX && pointX <= xx - margin && y + margin <= pointY && pointY <= yy - margin);
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