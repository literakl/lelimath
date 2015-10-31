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
    View hiddenPicture;

    public void tileClicked(View view) {
        Log.d(logTag, "tileClicked()");
        view.setAlpha(0);
    }

    @Override
    protected void onCreate(Bundle state) {
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_puzzle);
        hiddenPicture = findViewById(R.id.hidddenPictureArea);
        if (hiddenPicture == null) {
            Log.d(logTag, "onCreate() hiddenPicture not ready");
            return;
        }
        hiddenPicture.setBackgroundResource(R.drawable.pic_cute_girl);
    }
}
