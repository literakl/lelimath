package lelisoft.com.lelimath.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Interactive view that displays set of tiles that are erased when user selects correct answer.
 * Created by Leoš on 28.11.2015.
 */
public class TilesView extends View {
    private static final String logTag = TilesView.class.getSimpleName();

    Canvas drawCanvas;
    Bitmap canvasBitmap, fillBitmap;
    Paint canvasPaint, eraserPaint, veilPaint;
    Rect scaledPictureRect = new Rect(), origPictureRect;
    float xpad, ypad, w, h, ww, hh;

    public TilesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setupDrawing();
    }

    private void setupDrawing() {
        eraserPaint = new Paint();
        eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        veilPaint = new Paint();
        veilPaint.setColor(Color.GREEN);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    // http://developer.android.com/training/custom-views/custom-drawing.html
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(logTag, "onSizeChanged()");
        super.onSizeChanged(w, h, oldw, oldh);

        origPictureRect = new Rect(0, 0, fillBitmap.getWidth(), fillBitmap.getHeight());
        getDrawingRect(scaledPictureRect);
        Misc.centerHorizontally(scaledPictureRect.width(), scaledPictureRect.height(),
                fillBitmap.getWidth(), fillBitmap.getHeight(), scaledPictureRect);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawRect(0, 0, w, h, veilPaint);

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
//        mCustomImage.setBounds(scaledPictureRect);
//        mCustomImage.draw(canvas);
        canvas.drawBitmap(fillBitmap, origPictureRect, scaledPictureRect, canvasPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.d(logTag, "onTouchEvent()");
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            drawCanvas.drawCircle(event.getX(), event.getY(), 150, eraserPaint);
//            points.add(new Pair<>(event.getX(), event.getY()));
            invalidate();
        }
        return true;
    }

    public void setBackgroundPicture(int backgroundPicture) {
        Log.d(logTag, "setBackgroundPicture()");
//        InputStream is = context.getResources().openRawResource(backgroundPicture);
//        mBitmap = BitmapFactory.decodeStream(is);
        fillBitmap = BitmapFactory.decodeResource(getContext().getResources(), backgroundPicture);
    }
}
