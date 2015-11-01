package lelisoft.com.lelimath.view;

import android.graphics.Rect;

/**
 * Various view methods
 * Created by leos.literak on 31.10.2015.
 */
public class Misc {

    /**
     * Calculate the bounds of an image to fit inside a view after scaling and keeping the aspect ratio.
     * http://stackoverflow.com/questions/2740059/ and Android for Busy coders (Mirror)
     * @param vw container view width
     * @param vh container view height
     * @param iw image width
     * @param ih image height
     * @param out Rect that is provided to receive the result. If <code>null</code> then a new rect will be created
     */
    public static void calcCenter(int vw, int vh, int iw, int ih, Rect out) {
        double scale = Math.min((double) vw / (double) iw, (double) vh / (double) ih);
        int h = (int) (scale * ih);
        int w = (int) (scale * iw);
        int x = ((vw - w) >> 1);
        int y = ((vh - h) >> 1);
        out.set(x, y, x + w, y + h);
    }

    /**
     * Calculate the bounds of an image to fit inside a view after scaling and keeping the aspect ratio.
     * http://stackoverflow.com/questions/2740059/ and Android for Busy coders (Mirror)
     * @param vw container view width
     * @param vh container view height
     * @param iw image width
     * @param ih image height
     * @param out Rect that is provided to receive the result. If <code>null</code> then a new rect will be created
     */
    public static void centerHorizontally(int vw, int vh, int iw, int ih, Rect out) {
        double scale = Math.min((double) vw / (double) iw, (double) vh / (double) ih);
        int h = (int) (scale * ih);
        int w = (int) (scale * iw);
        int x = ((vw - w) >> 1);
        out.set(x, 0, x + w, h);
    }
}
