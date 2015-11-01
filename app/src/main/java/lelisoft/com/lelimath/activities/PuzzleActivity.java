package lelisoft.com.lelimath.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.view.TileGroupLayout;

/**
 * Guess picture type of activity
 * Author leos.literak on 18.10.2015.
 */
public class PuzzleActivity extends Activity {
    private static final String logTag = CalcActivity.class.getSimpleName();
    TileGroupLayout hiddenPicture;

    @Override
    protected void onCreate(Bundle state) {
        Log.d(logTag, "onCreate()");
        super.onCreate(state);

        setContentView(R.layout.activity_puzzle);
        hiddenPicture = (TileGroupLayout) findViewById(R.id.hidddenPictureArea);
        hiddenPicture.setPictureResource(R.drawable.pic_cute_girl);
        appendTile();
//        appendTile();
    }

    private void appendTile() {
        View view = getLayoutInflater().inflate(R.layout.template_tile, hiddenPicture, false);
        view.setId(View.generateViewId());
        view.setOnClickListener(tileListener);
        hiddenPicture.addView(view, view.getLayoutParams());
    }

    private View.OnClickListener tileListener = new View.OnClickListener() {
        public void onClick(View view) {
            Log.d(logTag, "tileClicked()");
            if (view.getAlpha() > 0) {
                view.setAlpha(0);
            } else {
                view.setAlpha(1);
            }
        }
    };
}
