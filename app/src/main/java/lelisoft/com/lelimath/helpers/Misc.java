package lelisoft.com.lelimath.helpers;

import android.content.Context;
import android.graphics.Rect;
import android.os.Environment;

import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Various view methods
 * Created by leos.literak on 31.10.2015.
 */
public class Misc {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LeliMathApp.class);

    static Float density = null;
    static Random random = new Random(System.currentTimeMillis());

    public static float dpFromPx(final Context context, final float px) {
        if (density == null) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return px / density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        if (density == null) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return dp * density;
    }

    /**
     * Calculate the bounds of an image to fit inside a view after scaling and keeping the aspect ratio.
     * http://stackoverflow.com/questions/2740059/ and Android for Busy coders (Mirror)
     * @param vw container view width
     * @param vh container view height
     * @param iw image width
     * @param ih image height
     * @param out Rect that is provided to receive the result
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
     * @return  that is provided to receive the result
     */
    public static Rect centerHorizontally(int vw, int vh, int iw, int ih) {
        Rect out = new Rect();
        double scale = Math.min((double) vw / (double) iw, (double) vh / (double) ih);
        int h = (int) (scale * ih);
        int w = (int) (scale * iw);
        int x = ((vw - w) >> 1);
        out.set(x, 0, x + w, h);
        return out;
    }

    public static boolean isNullOrEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    /**
     * Get string from {@code text}.
     * <ul>
     * <li>If text is null and {@code allowNull} is true, null is returned
     * <li>If text is null and {@code allowNull} is false, empty string ("") is returned
     * <li>If text is not null, return value of {@code toString()} is returned
     * </ul>
     * @param text CharSequence to made string from
     * @param allowNull if true null can be returned
     *
     * @return String that represents the CharSequence {@code text}
     */
    public static String toString(CharSequence text, boolean allowNull) {
        if (text == null) {
            if (allowNull) {
                return null;
            } else {
                return "";
            }
        }
        return text.toString();
    }

    /**
     * Copy one file to another
     * @param source input
     * @param dest output
     * @return result
     */
    public static boolean copyFile(File source, File dest) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new FileInputStream(source));
            out = new BufferedOutputStream(new FileOutputStream(dest, false));

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return true;
        } catch (IOException e) {
            log.warn("Failed to copy file {} to {}!", source.getAbsolutePath(), dest.getAbsolutePath(), e);
            return false;
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                log.warn("Failed to copy file {} to {}!", source.getAbsolutePath(), dest.getAbsolutePath(), e);
                //noinspection ReturnInsideFinallyBlock
                return false;
            }
        }
    }

    public static boolean isSDMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static Random getRandom() {
        return random;
    }
}
