package lelisoft.com.lelimath.gui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.helpers.Misc;

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
    Rect viewRect = new Rect(), origPictureRect, scaledPictureRect;
    Target target;

    public FigureView(Context context) {
        super(context);
        loadImages();
    }

    public FigureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadImages();
    }

    public FigureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadImages();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FigureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadImages();
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
        viewRect.set(0, 0, w, h);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        log.debug("onDraw()");
        if (baseBitmap != null) {
            scaledPictureRect = Misc.centerHorizontally(viewRect.width(), viewRect.height(), baseBitmap.getWidth(), baseBitmap.getHeight());
            log.debug("widget {}", viewRect);
            log.debug("scaled {}", scaledPictureRect);
            canvas.drawBitmap(baseBitmap, origPictureRect, scaledPictureRect, canvasPaint);
        }
    }

    private void loadImages() {
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        target = new LoadPictureTarget();
        Picasso.with(getContext()).load(R.drawable.amalka).into(target);
//        Picasso.with(getContext()).load(R.drawable.amelia).resize(442,848).into(target);
    }

    @Override
    protected void onAttachedToWindow() {
        log.debug("onAttachedToWindow()");
        super.onAttachedToWindow();
    }

    public void takeScreenshot() {
        buildDrawingCache();
//        Bitmap cache = getDrawingCache();
//        saveBitmap(cache);
        destroyDrawingCache();
    }

    class LoadPictureTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            log.debug("Setting figure bitmap {} x {}", bitmap.getWidth(), bitmap.getHeight());
            baseBitmap = bitmap;
            origPictureRect = new Rect(0, 0, baseBitmap.getWidth(), baseBitmap.getHeight());
            invalidate();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            log.debug("onBitmapFailed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            log.debug("onPrepareLoad");
        }
    }
}
