package lelisoft.com.lelimath.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.Tile;

/**
 * Renderer for a tile.
 * Created by Leoš on 12.12.2015.
 */
public class TileRenderer {
    Canvas canvas;
    Paint pencilPaint, borderPaint, eraserPaint, veilPaint, highlightPaint;

    public TileRenderer(Canvas canvas, Paint pencilPaint, Paint borderPaint, Paint eraserPaint, Paint veilPaint, Paint highlightPaint) {
        this.canvas = canvas;
        this.pencilPaint = pencilPaint;
        this.borderPaint = borderPaint;
        this.eraserPaint = eraserPaint;
        this.veilPaint = veilPaint;
        this.highlightPaint = highlightPaint;
    }

    public void render(Tile tile) {
        if (tile.isUncovered()) {
            canvas.drawRect(tile.getX(), tile.getY(), tile.getXx(), tile.getYy(), eraserPaint);
            return;
        } else if (tile.isSelected()) {
            canvas.drawRect(tile.getX(), tile.getY(), tile.getXx(), tile.getYy(), highlightPaint);
            return;
        }
        canvas.drawRect(tile.getX(), tile.getY(), tile.getXx(), tile.getYy(), veilPaint);
        // top line
        canvas.drawLine(tile.getX(), tile.getY(), tile.getXx(), tile.getY(), borderPaint);
        // left line
        canvas.drawLine(tile.getX(), tile.getY(), tile.getX(), tile.getYy(), borderPaint);
        // right line
        if (tile.isRight()) {
            canvas.drawLine(tile.getXx(), tile.getY(), tile.getXx(), tile.getYy(), borderPaint);
        }
        // bottom line
        if (tile.isBottom()) {
            canvas.drawLine(tile.getX(), tile.getYy(), tile.getXx(), tile.getYy(), borderPaint);
        }

        StringBuilder sb = new StringBuilder("23 + 36");
        String mText = sb.toString();

//        Formula formula = tile.getFormula();
//        sb.append(formula.getFirstOperand()).append(" ").append(formula.getOperator())
//                .append(" ").append(formula.getSecondOperand());

        Rect rect = new Rect();
        pencilPaint.getTextBounds(mText, 0, mText.length(), rect);
        int textWidth = (int) pencilPaint.measureText(mText);
        int textHeight = rect.height();

        canvas.drawText(mText,
                tile.getX() + (tile.getXx() - tile.getX() - textWidth) / 2f,
                tile.getYy() - (tile.getYy() - tile.getY() - textHeight) / 2f,
                pencilPaint
        );
    }
}
