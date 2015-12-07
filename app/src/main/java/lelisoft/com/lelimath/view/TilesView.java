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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.logic.PuzzleLogic;

/**
 * Interactive view that displays set of tiles that are erased when user selects correct answer.
 * Created by Leoš on 28.11.2015.
 */
public class TilesView extends View {
    private static final String logTag = TilesView.class.getSimpleName();

    PuzzleLogic logic;
    Canvas drawCanvas;
    Bitmap canvasBitmap, fillBitmap;
    Paint canvasPaint, eraserPaint, veilPaint, pencilPaint;
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
        pencilPaint = new TextPaint();
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.text_size_xxlarge));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(logTag, "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", " + MeasureSpec.toString(heightMeasureSpec) + ")");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // http://developer.android.com/training/custom-views/custom-drawing.html
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(logTag, "onSizeChanged(" + w + ", " + h + ", " + oldh + ", " + oldw + ")");
        super.onSizeChanged(w, h, oldw, oldh);

        getDrawingRect(scaledPictureRect);
        if (fillBitmap != null) {
            origPictureRect = new Rect(0, 0, fillBitmap.getWidth(), fillBitmap.getHeight());
            Misc.centerHorizontally(scaledPictureRect.width(), scaledPictureRect.height(),
                    fillBitmap.getWidth(), fillBitmap.getHeight(), scaledPictureRect);
        }

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawRect(0, 0, w, h, veilPaint);

        calculateFormulaDimensions(5, eraserPaint);
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.text_size_xxsmall));
        drawCanvas.drawText("xxsmall: 1 + 44 = 45", 100, 100, pencilPaint);
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.text_size_xsmall));
        drawCanvas.drawText("xsmall: 1 + 44 = 45", 100, 150, pencilPaint);
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.text_size_small));
        drawCanvas.drawText("small: 1 + 44 = 45", 100, 200, pencilPaint);
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.text_size_normal));
        drawCanvas.drawText("normal: 1 + 44 = 45", 100, 250, pencilPaint);
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.text_size_large));
        drawCanvas.drawText("large: 1 + 44 = 45", 100, 300, pencilPaint);
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.text_size_xlarge));
        drawCanvas.drawText("xlarge: 1 + 44 = 45", 100, 350, pencilPaint);
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.text_size_xxlarge));
        drawCanvas.drawText("xxlarge: 1 + 44 = 45", 100, 400, pencilPaint);
        pencilPaint.setTextSize(getResources().getDimension(R.dimen.tile_size));
        drawCanvas.drawText("tile: 1 + 44 = 45", 100, 480, pencilPaint);

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
        if (fillBitmap != null) {
            canvas.drawBitmap(fillBitmap, origPictureRect, scaledPictureRect, canvasPaint);
        }
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

    // https://chris.banes.me/2014/03/27/measuring-text/
    private Rect calculateFormulaDimensions(int length, Paint paint) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< length; i++) sb.append("9");
        String mText = sb.toString();

        Rect textBounds = new Rect();
        paint.getTextBounds(mText, 0, mText.length(), textBounds);
        int width = (int) paint.measureText(mText);
        int height = textBounds.height();
        Log.d(logTag, "calculateFormulaDimensions()" + textBounds.width() + " " + width);
        Log.d(logTag, "calculateFormulaDimensions()" + textBounds.height() + " " + height);
        return textBounds;
    }

    public void setBackgroundPicture(int backgroundPicture) {
        Log.d(logTag, "setBackgroundPicture()");
        fillBitmap = BitmapFactory.decodeResource(getContext().getResources(), backgroundPicture);
    }

    public void setLogic(PuzzleLogic logic) {
        this.logic = logic;
    }
}
