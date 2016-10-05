package lelisoft.com.lelimath.view;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Configuration for dress up activity.
 * Created by Leoš on 30.08.2016.
 */
public class DressPart implements Serializable {
    String  id, icon, path, depends;
    String[] alternatives;
    int price;
    Bitmap bitmap;

    @SuppressWarnings("unused")
    public DressPart() {
    }

    public DressPart(String id, int price) {
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * @return path to icon in parts selection
     */
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return path to this part picture
     */
    public String getPath() {
        return path;
    }

    @SuppressWarnings("unused")
    public void setPath(String path) {
        this.path = path;
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
     * @return id of mandatory DressPart that must be bought before this one
     */
    @SuppressWarnings("unused")
    public String getDepends() {
        return depends;
    }

    @SuppressWarnings("unused")
    public void setDepends(String depends) {
        this.depends = depends;
    }

    @SuppressWarnings("unused")
    public void setAlternatives(String[] alternatives) {
        this.alternatives = alternatives;
    }

    /**
     * @return id of other DressParts that are mutually exclusive with this part
     */
    @SuppressWarnings("unused")
    public String[] getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        return "DressPart{" +
                "id='" + id + '\'' +
                '}';
    }
}
