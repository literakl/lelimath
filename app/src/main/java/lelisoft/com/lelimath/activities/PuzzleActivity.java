package lelisoft.com.lelimath.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import lelisoft.com.lelimath.R;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends Activity {
    private static final String logTag = CalcActivity.class.getSimpleName();
    private Bitmap bg;
    private Bitmap photo;
    ImageView imgView;

    private int bg_width;
    private int bg_height;

    private final View.OnTouchListener changeColorListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
            int color = 0;
            try {
                color = bmp.getPixel((int) event.getX(), (int) event.getY());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (color == Color.TRANSPARENT) {
                Log.d(logTag, "onTouch() transparent");
                return false;
            } else {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(logTag, "onTouch() down");
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        Log.d(logTag, "onTouch() outside");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.d(logTag, "onTouch() cancel");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(logTag, "onTouch() move");
                        break;
                    case MotionEvent.ACTION_SCROLL:
                        Log.d(logTag, "onTouch() scroll");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(logTag, "onTouch() up");
                        Bitmap drawingCache = imgView.getDrawingCache();
                        break;
                    default:
                        break;
                }
                return true;

            }
        }
    };

    @Override
    protected void onCreate(Bundle state) {
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_puzzle);
        imgView = (ImageView) findViewById(R.id.overlayPicture);
        if (imgView == null) {
            Log.d(logTag, "onCreate() imgView not ready");
            return;
        }
        imgView.setDrawingCacheEnabled(true);
        imgView.setOnTouchListener(changeColorListener);
    }

    private void scaleImage(){
        if (photo != null) {

            int width = photo.getWidth();
            int height = photo.getHeight();

            int new_width = 0;
            int new_height = 0;

            if (width != height) {
                if (width > height) {
                    new_height = bg_height;
                    new_width = width * new_height / height;
                } else {
                    new_width = bg_width;
                    new_height = height * new_width / width;
                }
            } else {
                new_width = bg_width;
                new_height = bg_height;
            }
            photo = Bitmap.createScaledBitmap(photo, new_width, new_height, true);
        }
    }
}
