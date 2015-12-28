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
    Tile selectedTile;
    Canvas drawCanvas;
    Bitmap canvasBitmap, fillBitmap;
    Paint canvasPaint, eraserPaint, bgPaint, tileBgPaint, tileTextPaint, tileBorderPaint, selectedTileBgPaint;
    Rect tilesCardRect = new Rect();
    Rect origPictureRect, scaledPictureRect = new Rect();
    TileRenderer tileRenderer;
    float w, h, tileHeight, tileWidth, minTileSize;
    int padding, tilePadding, tileTouchMargin;
    int maxTilesHorizontal, maxTilesVertical;

    public TilesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setupDrawing();
    }

    private void setupDrawing() {
        padding = (int) getResources().getDimension(R.dimen.tiles_view_padding);
        minTileSize = getResources().getDimension(R.dimen.tile_size);
        tilePadding = (int) getResources().getDimension(R.dimen.tile_padding);
        tileTouchMargin = (int) getResources().getDimension(R.dimen.tile_touch_margin);

        eraserPaint = new Paint();
        eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);
        tileBgPaint = new Paint();
        tileBgPaint.setColor(Color.LTGRAY);
        selectedTileBgPaint = new Paint();
        selectedTileBgPaint.setColor(Color.YELLOW);
        tileBorderPaint = new Paint();
        tileBorderPaint.setColor(Color.DKGRAY);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        tileTextPaint = new TextPaint();
        tileTextPaint.setColor(Color.YELLOW);
        tileTextPaint.setTextSize(getResources().getDimension(R.dimen.text_size_xxlarge));
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

        this.w = w;
        this.h = h;
        tilesCardRect.set(padding, padding, w - padding, h - padding);

        if (fillBitmap == null) {
            Log.e(logTag, "picture bitmap is empty!");
            return;
        }

        origPictureRect = new Rect(0, 0, fillBitmap.getWidth(), fillBitmap.getHeight());
        Misc.centerHorizontally(tilesCardRect.width(), tilesCardRect.height(),
                                fillBitmap.getWidth(), fillBitmap.getHeight(), scaledPictureRect);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawRect(0, 0, w, h, bgPaint);
        tileRenderer = new TileRenderer(drawCanvas, tileTextPaint, tileBorderPaint, tileBgPaint, selectedTileBgPaint, eraserPaint);

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
            if (! tilesCardRect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
            int i = (int) ((event.getY() - padding) / tileHeight);
            int j = (int) ((event.getX() - padding) / tileWidth);
            if (i >= maxTilesVertical || j >= maxTilesHorizontal) {
                Log.e(logTag, "onTouchEvent() - touch out of box");
                return true;
            }

            Tile tile = tiles[i][j];
            if (tile.isInside(event.getX(), event.getY(), tileTouchMargin)) {
                if (tile == selectedTile) {
                    tile.setSelected(false);
                    selectedTile = null;
                } else {
                    if (selectedTile != null) {
                        selectedTile.setSelected(false);
                        tileRenderer.render(selectedTile);
                    }
                    tile.setSelected(true);
                    selectedTile = tile;
                }

                tileRenderer.render(tile);
                invalidate();
            } else {
                Log.d(logTag, "onTouchEvent() - inactive area touched " + tile);

            }
        }
        return true;
    }

    private void generateTiles() {
        Log.d(logTag, "generateTiles()");
        long start = System.currentTimeMillis();
        String mText = getSampleFormula();
        Rect rect = new Rect();
        tileTextPaint.getTextBounds(mText, 0, mText.length(), rect);
        int maxWidth = (int) tileTextPaint.measureText(mText);

        tileWidth = Math.max(minTileSize, maxWidth + tilePadding);
        tileHeight = Math.max(minTileSize, rect.height() + tilePadding);
        maxTilesVertical = (int) Math.floor(tilesCardRect.height() / tileHeight);
        tileHeight = tilesCardRect.height() / maxTilesVertical;
        maxTilesHorizontal = (int) Math.floor(tilesCardRect.width() / tileWidth);
        tileWidth = tilesCardRect.width() / maxTilesHorizontal;

        tiles = new Tile[maxTilesVertical][maxTilesHorizontal];
        float pointerX, pointerY = tilesCardRect.top;
        for (int i = 0; i < maxTilesVertical; i++) {
            pointerX = tilesCardRect.left;
            for (int j = 0; j < maxTilesHorizontal; j++) {
                tiles[i][j] = new Tile(pointerX, pointerY, pointerX + tileWidth, pointerY + tileHeight);
                if (i == 0) {
                    tiles[i][j].setTop(true);
                    tiles[i][j].setY(tiles[i][j].getY() + 1);
                }
                if (j == 0) {
                    tiles[i][j].setLeft(true);
                }
                if (i == (maxTilesVertical - 1)) {
                    tiles[i][j].setBottom(true);
                    tiles[i][j].setYy(tiles[i][j].getYy() - 1);
                }
                if (j == (maxTilesHorizontal - 1)) {
                    tiles[i][j].setRight(true);
                    tiles[i][j].setXx(tiles[i][j].getXx() - 1);
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
