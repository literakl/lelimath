package lelisoft.com.lelimath.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Interactive view that displays set of tiles that are erased when user selects correct answer.
 * Created by Leoš on 28.11.2015.
 */
public class TilesView extends View {
    Paint eraserPaint, veilPaint;
    float xpad, ypad, w, h, ww, hh;

    public TilesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        eraserPaint = new Paint();
        eraserPaint.setColor(Color.TRANSPARENT);
//        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        veilPaint = new Paint();
        veilPaint.setColor(Color.GRAY);
    }

    // http://developer.android.com/training/custom-views/custom-drawing.html
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;

        // Account for padding
        xpad = (float)(getPaddingLeft() + getPaddingRight());
        ypad = (float)(getPaddingTop() + getPaddingBottom());

        // Account for the label
//        if (mShowText) xpad += mTextWidth;
        ww = (float)w - xpad;
        hh = (float)h - ypad;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawRect(0, 0, w, h, veilPaint);
        canvas.drawOval(new RectF(20, 20, 220, 220), eraserPaint);
    }
}
