package lelisoft.com.lelimath.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextPaint;

import lelisoft.com.lelimath.R;

/**
 * Renderer for a tile.
 * Created by Leoš on 12.12.2015.
 */
public class TileRenderer {
    Canvas canvas;
    Paint textPaint, borderPaint, eraserPaint, bgPaint, selectedBgPaint;
    float roundRadius;

    public TileRenderer(Context context, Canvas canvas) {
        this.canvas = canvas;
        roundRadius = context.getResources().getDimension(R.dimen.tile_round_rect);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.LTGRAY);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.DKGRAY);

        selectedBgPaint = new Paint();
        selectedBgPaint.setAntiAlias(true);
        selectedBgPaint.setStyle(Paint.Style.FILL);
        selectedBgPaint.setColor(Color.YELLOW);

        eraserPaint = new Paint();
        eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size_xlarge));
    }

    public void render(Tile tile) {
        if (tile.isUncovered()) {
            canvas.drawRect(tile.getX(), tile.getY(), tile.getXx(), tile.getYy(), eraserPaint);
            return;
        }

        canvas.drawRoundRect(new RectF(tile.getX() + 1, tile.getY() + 1, tile.getXx() - 1, tile.getYy() - 1),
                roundRadius, roundRadius, borderPaint);

        if (tile.isSelected()) {
            canvas.drawRoundRect(new RectF(tile.getX() + 1, tile.getY() + 1, tile.getXx() - 1, tile.getYy() - 1),
                    roundRadius, roundRadius, selectedBgPaint);
        } else {
            canvas.drawRoundRect(new RectF(tile.getX() + 1, tile.getY() + 1, tile.getXx() - 1, tile.getYy() - 1),
                    roundRadius, roundRadius, bgPaint);
        }

        StringBuilder sb = new StringBuilder("23 + 36");
        String mText = sb.toString();

//        Formula formula = tile.getFormula();
//        sb.append(formula.getFirstOperand()).append(" ").append(formula.getOperator())
//                .append(" ").append(formula.getSecondOperand());

        Rect rect = measureText(mText);
        canvas.drawText(mText,
                tile.getX() + (tile.getXx() - tile.getX() - rect.width()) / 2f,
                tile.getYy() - (tile.getYy() - tile.getY() - rect.height()) / 2f,
                textPaint
        );
    }

    public Rect measureText(String mText) {
        Rect rect = new Rect();
        textPaint.getTextBounds(mText, 0, mText.length(), rect);
        int maxWidth = (int) textPaint.measureText(mText);
        rect.right = rect.left + maxWidth;
        return rect;
    }
}
