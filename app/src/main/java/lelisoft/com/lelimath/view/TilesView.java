package lelisoft.com.lelimath.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import lelisoft.com.lelimath.R;

/**
 * Interactive view that displays set of tiles that are erased when user selects correct answer.
 * Created by Leoš on 28.11.2015.
 */
public class TilesView extends View {
    private static final String logTag = TilesView.class.getSimpleName();

    Paint eraserPaint, veilPaint;
    float xpad, ypad, w, h, ww, hh, posX, posY;

    public TilesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(logTag, "constructor()");
        setFocusable(true);
        eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFFFF);
        eraserPaint.setStyle(Paint.Style.FILL);
        veilPaint = new Paint();
        veilPaint.setColor(Color.GREEN);
    }

    // http://developer.android.com/training/custom-views/custom-drawing.html
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(logTag, "onSizeChanged()");
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
        Log.d(logTag, "onDraw()");
        super.onDraw(canvas);
        canvas.drawRect(0, 0, w, h, veilPaint);
        canvas.drawCircle(posX, posY, 250, eraserPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            posX = event.getX();
            posY = event.getY();
            invalidate();
        }
        return true;
    }

    public void setBackgroundPicture(int backgroundPicture) {
        Log.d(logTag, "setBackgroundPicture()");
        Bitmap fillBMP = BitmapFactory.decodeResource(getContext().getResources(), backgroundPicture);
        BitmapShader fillBmpShader = new BitmapShader(fillBMP, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        eraserPaint.setShader(fillBmpShader);
    }
}
