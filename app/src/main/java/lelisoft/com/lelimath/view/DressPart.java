package lelisoft.com.lelimath.view;

import android.graphics.Rect;

import java.io.Serializable;

/**
 * Configuration for dress up activity.
 * Created by Leoš on 30.08.2016.
 */
public class DressPart implements Serializable {
    String  id;
    String[] equivalents;
    int price;
    int x, y, w, h, destX, destY;

    @SuppressWarnings("unused")
    public DressPart() {
    }

    public DressPart(String id, int price) {
        this.id = id;
        this.price = price;
    }

    public void setCoordinates(int x, int y,int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * @return height of this component
     */
    @SuppressWarnings("unused")
    public int getH() {
        return h;
    }

    @SuppressWarnings("unused")
    public void setH(int h) {
        this.h = h;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public int getPrice() {
        return price;
    }

    @SuppressWarnings("unused")
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * @return width of this component
     */
    @SuppressWarnings("unused")
    public int getW() {
        return w;
    }

    @SuppressWarnings("unused")
    public void setW(int w) {
        this.w = w;
    }

    /**
     * @return starting X position within the parts picture
     */
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return starting Y position within the parts picture
     */
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return X offset against 0,0 of complete picture
     */
    public int getDestX() {
        return destX;
    }

    public void setDestX(int destX) {
        this.destX = destX;
    }

    public int getDestY() {
        return destY;
    }

    /**
     * @return Y offset against 0,0 of complete picture
     */
    public void setDestY(int destY) {
        this.destY = destY;
    }

    public Rect getRect() {
        return new Rect(x, y, x + w, y + h);
    }

    public void setEquivalents(String[] equivalents) {
        this.equivalents = equivalents;
    }

    /**
     * @return id of other DressParts that are mutually exclusive with this part
     */
    @SuppressWarnings("unused")
    public String[] getEquivalents() {
        return equivalents;
    }
}
