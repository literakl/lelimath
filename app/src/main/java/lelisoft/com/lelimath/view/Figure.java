package lelisoft.com.lelimath.view;

import java.io.Serializable;

/**
 * Configuration for dress up activity
 * Created by Leoš on 30.08.2016.
 */
public class Figure implements Serializable {
    String id;
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

    public DressPart[] getParts() {
        return parts;
    }

    public void setParts(DressPart[] parts) {
        this.parts = parts;
    }

    /**
     * @return true when all bitmaps are set
     */
    public boolean isLoadingCompleted() {
        if (main.getBitmap() == null) {
            return false;
        }
        for (DressPart part : parts) {
            if (part.getBitmap() == null) {
                return false;
            }
        }

        return true;
    }
}
