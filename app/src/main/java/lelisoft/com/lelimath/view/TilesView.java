package lelisoft.com.lelimath.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import lelisoft.com.lelimath.R;
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
    Paint canvasPaint;
    Rect tilesRect = new Rect(), origPictureRect, scaledPictureRect;
    TileRenderer tileRenderer;
    float w, h, tileHeight, tileWidth;
    int tilePadding, minTileSize, tileTouchMargin;

    public TilesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setupDrawing();
    }

    /**
     * Generates new game.
     * @param invalidateView it will invalidate view so it will be redrawn
     */
    public void setupGame(boolean invalidateView) {
        scaledPictureRect = Misc.centerHorizontally(tilesRect.width(), tilesRect.height(), fillBitmap.getWidth(), fillBitmap.getHeight());
        generateTiles();
        if (invalidateView) {
            invalidate();
        }
    }

    private void setupDrawing() {
        minTileSize = (int) getResources().getDimension(R.dimen.tile_size);
        tilePadding = (int) getResources().getDimension(R.dimen.tile_padding);
        tileTouchMargin = (int) getResources().getDimension(R.dimen.tile_touch_margin);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
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
        if (fillBitmap == null) {
            Log.e(logTag, "Picture bitmap is empty!");
            Toast.makeText(getContext(), "Picture bitmap is empty!", Toast.LENGTH_LONG).show();
            return;
        }

        this.w = w;
        this.h = h;
        tilesRect.set(0, 0, w, h);
        origPictureRect = new Rect(0, 0, fillBitmap.getWidth(), fillBitmap.getHeight());

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
        tileRenderer = new TileRenderer(getContext(), drawCanvas);

        setupGame(false);
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
            if (! tilesRect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
            int i = (int) (event.getY() / tileHeight);
            int j = (int) (event.getX() / tileWidth);
            if (i >= tiles.length || j >= tiles[0].length) {
                Log.e(logTag, "onTouchEvent() - touch out of box!");
                return true;
            }

            long start = System.currentTimeMillis();
            Tile tile = tiles[i][j];
            if (tile.isInside(event.getX(), event.getY(), tileTouchMargin)) {
                if (tile == selectedTile) {
                    tile.setSelected(false);
                    selectedTile = null;
                } else {
                    if (selectedTile != null) {
                        if (selectedTile.matches(tile)) {
                            tile.setUncovered(true);
                            selectedTile.setUncovered(true);
                            selectedTile.setSelected(false);
                            selectedTile = null;
                        } else {
                            selectedTile.setSelected(false);
                            selectedTile = null;
                        }
                    } else {
                        tile.setSelected(true);
                        selectedTile = tile;
                    }
                }

                // work around for antialiasing issue
                drawCanvas.drawColor(Color.WHITE);
                for (Tile[] tilesRow : tiles) {
                    for (Tile aTilesRow : tilesRow) {
                        tileRenderer.render(aTilesRow);
                    }
                }
                Log.d(logTag, "draw " + (System.currentTimeMillis() - start));
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

        calculateTileRequestedDimensions();
        int maxHorizontalTiles = Math.min((int) Math.floor(tilesRect.width() / tileWidth), logic.getLevel().x);
        int maxVerticalTiles = logic.getLevel().y;
        tileHeight = tilesRect.height() / maxVerticalTiles;
        tileWidth = tilesRect.width() / maxHorizontalTiles;

        List<FormulaResultPair> tilesValues = logic.generateFormulaResultPairs(maxHorizontalTiles * maxVerticalTiles);
        Iterator<FormulaResultPair> iterator = tilesValues.iterator();

        tiles = new Tile[maxVerticalTiles][maxHorizontalTiles];
        float pointerX, pointerY = tilesRect.top;
        for (int i = 0; i < maxVerticalTiles; i++) {
            pointerX = tilesRect.left;
            for (int j = 0; j < maxHorizontalTiles; j++) {
                tiles[i][j] = new Tile(pointerX, pointerY, pointerX + tileWidth, pointerY + tileHeight);
                if (iterator.hasNext()) {
                    tiles[i][j].setFormulaResultPair(iterator.next());
                } else {
                    tiles[i][j].setUncovered(true);
                }

                if (i == 0) {
                    tiles[i][j].setY(tiles[i][j].getY() + 1);
                }
                if (i == (maxVerticalTiles - 1)) {
                    tiles[i][j].setYy(tiles[i][j].getYy() - 1);
                }
                if (j == (maxHorizontalTiles - 1)) {
                    tiles[i][j].setXx(tiles[i][j].getXx() - 1);
                }
                tileRenderer.render(tiles[i][j]);
                pointerX += tileWidth;
            }
            pointerY += tileHeight;
        }

        Log.d(logTag, "draw " + (System.currentTimeMillis() - start));
    }

    /**
     * Finds neccessary space for tile/slate having longest possible formula including padding
     */
    private void calculateTileRequestedDimensions() {
        Rect rect = tileRenderer.measureText(getSampleFormula());
        tileWidth = Math.max(minTileSize, rect.width() + tilePadding);
        tileHeight = Math.max(minTileSize, rect.height() + tilePadding);
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
