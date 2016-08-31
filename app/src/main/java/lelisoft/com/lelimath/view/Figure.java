package lelisoft.com.lelimath.view;

import java.io.Serializable;

/**
 * Configuration for dress up activity
 * Created by Leoš on 30.08.2016.
 */
public class Figure implements Serializable {
    String id, path;
    int w, h, x;
    double scaleRatio;
    DressPart main;
    DressPart[] parts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Holder for the figure
     */
    public DressPart getMain() {
        return main;
    }

    public void setMain(DressPart main) {
        this.main = main;
    }

    /**
     * @return maximum height of dressed figure
     */
    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    /**
     * @return maximum width of dressed figure
     */
    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    /**
     * @return X position of completely dressed figure
     */
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return scaling ratio to be used for all parts
     */
    public double getScaleRatio() {
        return scaleRatio;
    }

    public void setScaleRatio(double scaleRatio) {
        this.scaleRatio = scaleRatio;
    }

    public DressPart[] getParts() {
        return parts;
    }

    public void setParts(DressPart[] parts) {
        this.parts = parts;
    }

    /**
     * @return path to image with all parts
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
