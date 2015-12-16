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
import lelisoft.com.lelimath.data.Tile;
import lelisoft.com.lelimath.logic.PuzzleLogic;

/**
 * Interactive view that displays set of tiles that are erased when user selects correct answer.
 * Created by Leoš on 28.11.2015.
 */
public class TilesView extends View {
    private static final String logTag = TilesView.class.getSimpleName();

    PuzzleLogic logic;
    Tile[][] tiles;
    Canvas drawCanvas;
    Bitmap canvasBitmap, fillBitmap;
    Paint canvasPaint, eraserPaint, veilPaint, pencilPaint, tilePaint;
    Rect scaledPictureRect = new Rect(), origPictureRect;
    TileRenderer tileRenderer;
    float w, h, tileHeight, tileWidth, minTileSize, tilePadding, tileTouchMargin;
    int maxTilesHorizontal, maxTilesVertical;

    public TilesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setupDrawing();
    }

    private void setupDrawing() {
        minTileSize = getResources().getDimension(R.dimen.tile_size);
        tilePadding = getResources().getDimension(R.dimen.tile_padding);
        tileTouchMargin = getResources().getDimension(R.dimen.tile_touch_margin);
        eraserPaint = new Paint();
        eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        veilPaint = new Paint();
        veilPaint.setColor(Color.DKGRAY);
        tilePaint = new Paint();
        tilePaint.setColor(Color.LTGRAY);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        pencilPaint = new TextPaint();
        pencilPaint.setColor(Color.YELLOW);
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
        tileRenderer = new TileRenderer(drawCanvas, pencilPaint, tilePaint, eraserPaint);

        this.w = w;
        this.h = h;
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
        Log.d(logTag, "onTouchEvent(" + event.getX() + " " + event.getY() + " " + event.getSize() + ")");

        if (event.getAction() == MotionEvent.ACTION_UP) {
            int i = (int) (event.getY() / tileHeight);
            int j = (int) (event.getX() / tileWidth);
            if (i >= maxTilesVertical || j >= maxTilesHorizontal) {
                Log.d(logTag, "onTouchEvent() - touch out of box");
                return true;
            }

            if (tiles[i][j].isInside(event.getX(), event.getY(), tileTouchMargin)) {
                tiles[i][j].setUncovered(true);
                tileRenderer.render(tiles[i][j]);
                invalidate();
            } else {
                Log.d(logTag, "onTouchEvent() - touch inactive area");

            }
        }
        return true;
    }

    private void generateTiles() {
        Log.d(logTag, "generateTiles()");
        String mText = getSampleFormula();
        Rect rect = new Rect();
        pencilPaint.getTextBounds(mText, 0, mText.length(), rect);
        int maxWidth = (int) pencilPaint.measureText(mText);

        tileWidth = Math.max(minTileSize, maxWidth + tilePadding);
        tileHeight = Math.max(minTileSize, rect.height() + tilePadding);
        maxTilesVertical = (int) Math.floor(h / tileHeight);
        tileHeight = h / maxTilesVertical;
        maxTilesHorizontal = (int) Math.floor(w / tileWidth);
        tileWidth = w / maxTilesHorizontal;

        long start = System.currentTimeMillis();
        tiles = new Tile[maxTilesVertical][maxTilesHorizontal];
        float pointerX, pointerY = 0;
        for (int i = 0; i < maxTilesVertical; i++) {
            pointerX = 0;
            for (int j = 0; j < maxTilesHorizontal; j++) {
                tiles[i][j] = new Tile(pointerX, pointerY, pointerX + tileWidth, pointerY + tileHeight);
                if (i == 0) {
                    tiles[i][j].setTop(true);
                    tiles[i][j].setY(1);
                }
                if (j == 0) {
                    tiles[i][j].setLeft(true);
                }
                if (i == (maxTilesVertical - 1)) {
                    tiles[i][j].setBottom(true);
                    tiles[i][j].setYy(h - 1);
                }
                if (j == (maxTilesHorizontal - 1)) {
                    tiles[i][j].setRight(true);
                    tiles[i][j].setXx(w - 1);
                }
                tileRenderer.render(tiles[i][j]);
                pointerX += tileWidth;
            }
            pointerY += tileHeight;
        }

        Log.d(logTag, "draw " + (System.currentTimeMillis() - start));
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
