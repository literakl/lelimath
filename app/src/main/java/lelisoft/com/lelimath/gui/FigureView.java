package lelisoft.com.lelimath.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import lelisoft.com.lelimath.view.DressPart;
import lelisoft.com.lelimath.view.Figure;

/**
 * View that displays a figure and its selected dress.
 * Created by Leoš on 28.08.2016.
 */
public class FigureView extends View {
    private static final Logger log = LoggerFactory.getLogger(FigureView.class);

    int w, h;
    Canvas drawCanvas;
    Bitmap canvasBitmap;
    Paint canvasPaint;
    Figure figure;
    Rect srcRect = new Rect(), destRect = new Rect();
    List<String> displayedParts;

    public FigureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        log.debug("onMeasure({}, {}", MeasureSpec.toString(widthMeasureSpec), MeasureSpec.toString(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // http://developer.android.com/training/custom-views/custom-drawing.html
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        log.debug("onSizeChanged({},{},{},{})", w, h, oldh, oldw);
        super.onSizeChanged(w, h, oldw, oldh);

        this.w = w;
        this.h = h;
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        log.debug("onDraw()");
        if (figure.isLoadingCompleted()) {
            int fw = figure.getMain().getBitmap().getWidth();
            int fh = figure.getMain().getBitmap().getHeight();
            double scale = Math.min((double) w / (double) fw, (double) h / (double) fh);
            int sw = (int) (scale * fw);
            int sh = (int) (scale * fh);
            int x = ((w - sw) >> 1);
            int y = ((h - sh) >> 1);
            log.debug("figure {} x {} px", fw, fh);
            log.debug("scaled {} x {} px", sw, sh);
            srcRect.set(0, 0, fw, fh);
            destRect.set(x, y, x + sw, y + sh);
//            destRect.set(figure.getX() + sdx, sdy, figure.getX() + sw + sdx, sh + sdy);

            canvas.drawBitmap(figure.getMain().getBitmap(), srcRect, destRect, canvasPaint);
            if (displayedParts != null) {
                for (DressPart dressPart : figure.getParts()) {
                    if (displayedParts.contains(dressPart.getId())) {
                        canvas.drawBitmap(dressPart.getBitmap(), srcRect, destRect, canvasPaint);
                    }
                }
            }
        }
    }

    private void setupResources() {
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setDisplayedParts(List<String> parts) {
        this.displayedParts = parts;
    }

    @Override
    protected void onAttachedToWindow() {
        log.debug("onAttachedToWindow()");
        setupResources();
        super.onAttachedToWindow();
    }

    @SuppressWarnings("unused")
    public void takeScreenshot() {
        buildDrawingCache();
//        Bitmap cache = getDrawingCache();
//        saveBitmap(cache);
        destroyDrawingCache();
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }
}
