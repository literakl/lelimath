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
    Paint canvasPaint, eraserPaint, veilPaint, pencilPaint, tilePaint;
    Rect scaledPictureRect = new Rect(), origPictureRect;
    float w, h;

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
        veilPaint.setColor(Color.DKGRAY);
        tilePaint = new Paint();
        tilePaint.setColor(Color.LTGRAY);
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

        this.w = w;
        this.h = h;
//        float xpad = (float)(getPaddingLeft() + getPaddingRight());
//        float ypad = (float)(getPaddingTop() + getPaddingBottom());
//        ww = (float)w - xpad;
//        hh = (float)h - ypad;

        generateTiles();
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

    private void generateTiles() {
        float minTileSize = getResources().getDimension(R.dimen.tile_size);
        float tilePadding = getResources().getDimension(R.dimen.tile_padding);
        Rect maxFormulaSize = calculateFormulaDimension();
        float tileWidth = Math.max(minTileSize, maxFormulaSize.width() + tilePadding);
        float tileHeight = Math.max(minTileSize, maxFormulaSize.height() + tilePadding);
        int maxTilesVertical = (int) Math.floor(h / tileHeight);
        tileHeight = h / maxTilesVertical;
        int maxTilesHorizontal = (int) Math.floor(w / tileWidth);
        tileWidth = w / maxTilesHorizontal;

        float pointer = 0;
        for (int i = 0; i < maxTilesHorizontal; i++) {
            drawCanvas.drawLine(pointer, 0, pointer, h, tilePaint);
            pointer += tileWidth;
        }

        pointer = 0;
        for (int i = 0; i < maxTilesVertical; i++) {
            drawCanvas.drawLine(0, pointer, w, pointer, tilePaint);
            pointer += tileHeight;
        }
    }

    /**
     * Calculates minimum size of longest formula.
     * https://chris.banes.me/2014/03/27/measuring-text/
     * @return Rect having no padding
     */
    private Rect calculateFormulaDimension() {
        String mText = getSampleFormula();
        Rect rect = new Rect();
        pencilPaint.getTextBounds(mText, 0, mText.length(), rect);
        int textWidth = (int) pencilPaint.measureText(mText);
        int textHeight = rect.height();
        Log.d(logTag, "calculateFormulaDimensions()" + rect.width() + " " + textWidth);
        Log.d(logTag, "calculateFormulaDimensions()" + rect.height() + " " + textHeight);
        return rect;
    }

    private String getSampleFormula() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < logic.getFirstOperandMaximumLength(); i++) sb.append("3");
        sb.append(" + ");
        for (int i = 0; i < logic.getSecondOperandMaximumLength(); i++) sb.append("3");
        return sb.toString();
    }

    public void setBackgroundPicture(int backgroundPicture) {
        Log.d(logTag, "setBackgroundPicture()");
        fillBitmap = BitmapFactory.decodeResource(getContext().getResources(), backgroundPicture);
    }

    public void setLogic(PuzzleLogic logic) {
        this.logic = logic;
    }
}
