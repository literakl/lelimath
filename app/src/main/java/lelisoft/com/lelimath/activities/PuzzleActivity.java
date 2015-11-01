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
import lelisoft.com.lelimath.view.TileGroupLayout;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends Activity {
    private static final String logTag = CalcActivity.class.getSimpleName();
    TileGroupLayout hiddenPicture;

    public void tileClicked(View view) {
        Log.d(logTag, "tileClicked()");
        if (view.getAlpha() > 0) {
            view.setAlpha(0);
        } else {
            view.setAlpha(1);
        }
    }

    @Override
    protected void onCreate(Bundle state) {
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_puzzle);
        hiddenPicture = (TileGroupLayout) findViewById(R.id.hidddenPictureArea);
        if (hiddenPicture == null) {
            Log.d(logTag, "onCreate() hiddenPicture not ready");
            return;
        }
        hiddenPicture.setPictureResource(R.drawable.pic_cute_girl);
    }
}
