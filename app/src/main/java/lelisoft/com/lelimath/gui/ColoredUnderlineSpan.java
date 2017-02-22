package lelisoft.com.lelimath.gui;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

import java.lang.reflect.Method;

/**
 * Created by Leoš on 09.02.2017.
 * http://stackoverflow.com/questions/34614789/how-to-change-the-color-of-underline-in-android
 */
public final class ColoredUnderlineSpan extends CharacterStyle implements UpdateAppearance {
    private final int mColor;

    public ColoredUnderlineSpan(final int color) {
        mColor = color;
    }

    @Override
    public void updateDrawState(final TextPaint tp) {
        try {
            final Method method = TextPaint.class.getMethod("setUnderlineText", Integer.TYPE, Float.TYPE);
            method.invoke(tp, mColor, 8.0f);
        } catch (final Exception e) {
            tp.setUnderlineText(true);
        }
    }
}
