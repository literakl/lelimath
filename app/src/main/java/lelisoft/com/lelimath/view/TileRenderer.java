package lelisoft.com.lelimath.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import lelisoft.com.lelimath.data.Tile;

/**
 * Renderer for a tile.
 * Created by Leoš on 12.12.2015.
 */
public class TileRenderer {
    Canvas canvas;
    Paint textPaint, borderPaint, eraserPaint, bgPaint, selectedBgPaint;

    public TileRenderer(Canvas canvas, Paint textPaint, Paint borderPaint, Paint bgPaint, Paint selectedBgPaint, Paint eraserPaint) {
        this.canvas = canvas;
        this.textPaint = textPaint;
        this.borderPaint = borderPaint;
        this.eraserPaint = eraserPaint;
        this.bgPaint = bgPaint;
        this.selectedBgPaint = selectedBgPaint;
    }

    public void render(Tile tile) {
        if (tile.isUncovered()) {
            canvas.drawRect(tile.getX(), tile.getY(), tile.getXx(), tile.getYy(), eraserPaint);
            return;
        } else if (tile.isSelected()) {
            canvas.drawRect(tile.getX(), tile.getY(), tile.getXx(), tile.getYy(), selectedBgPaint);
            return;
        }
        canvas.drawRect(tile.getX(), tile.getY(), tile.getXx(), tile.getYy(), bgPaint);
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
        textPaint.getTextBounds(mText, 0, mText.length(), rect);
        int textWidth = (int) textPaint.measureText(mText);
        int textHeight = rect.height();

        canvas.drawText(mText,
                tile.getX() + (tile.getXx() - tile.getX() - textWidth) / 2f,
                tile.getYy() - (tile.getYy() - tile.getY() - textHeight) / 2f,
                textPaint
        );
    }
}
