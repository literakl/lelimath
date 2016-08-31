package lelisoft.com.lelimath.gui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lelisoft.com.lelimath.view.DressPart;
import lelisoft.com.lelimath.view.Figure;

/**
 * View that displays a figure and its selected dress.
 * Created by Leoš on 28.08.2016.
 */
public class FigureView extends View {
    private static final Logger log = LoggerFactory.getLogger(FigureView.class);

    Bitmap baseBitmap;
    int w, h;
    Canvas drawCanvas;
    Bitmap canvasBitmap;
    Paint canvasPaint;
    Rect scaledRect = new Rect();
    Target target;
    Figure figure;

    public FigureView(Context context) {
        super(context);
        log.debug("FigureView");
    }

    public FigureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        log.debug("FigureView");
    }

    public FigureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        log.debug("FigureView");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public FigureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        log.debug("FigureView");
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
//        drawCanvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        log.debug("onDraw()");
        if (baseBitmap != null) {
            double scale = Math.min((double) w / (double) figure.getW(), (double) h / (double) figure.getH());
            figure.setScaleRatio(scale);
            int sw = (int) (scale * figure.getW());
            int x = ((w - sw) >> 1);
            figure.setX(x);
            paintDressPart(canvas, figure, figure.getMain());
            paintDressPart(canvas, figure, figure.getParts()[0]);
            paintDressPart(canvas, figure, figure.getParts()[1]);
            paintDressPart(canvas, figure, figure.getParts()[2]);
        }
    }

    private void paintDressPart(Canvas canvas, Figure figure, DressPart part) {
        double scale = figure.getScaleRatio();
        int sh = (int) (scale * part.getH());
        int sw = (int) (scale * part.getW());
        int sdx = (int) (scale * part.getDestX());
        int sdy = (int) (scale * part.getDestY());
        scaledRect.set(figure.getX() + sdx, sdy, figure.getX() + sw + sdx, sh + sdy);
        Rect partRect = part.getRect();
        log.debug("part {}", partRect);
        log.debug("scaled {}", scaledRect);
        canvas.drawBitmap(baseBitmap, partRect, scaledRect, canvasPaint);
    }

    private void setupResources() {
        target = new LoadPictureTarget();
        Picasso.with(getContext()).load("file:///android_asset/amalka.png").into(target);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        try {
            Gson gson = new Gson();
            InputStream is = getContext().getAssets().open("dress_up.json");
            InputStreamReader reader = new InputStreamReader(is);
            figure = gson.fromJson(reader, Figure.class);
        } catch (IOException e) {
            log.error("Failed to read JSON asset", e);
        }
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

    class LoadPictureTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            log.debug("Loaded figure bitmap {} x {}", bitmap.getWidth(), bitmap.getHeight());
            baseBitmap = bitmap;
            invalidate();
            target = null;
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            log.debug("onBitmapFailed");
            target = null;
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            log.debug("onPrepareLoad");
        }
    }
}
