package lelisoft.com.lelimath.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridLayout;

import lelisoft.com.lelimath.R;

/**
 * Grid holding individual tiles.
 * Created by leos.literak on 31.10.2015.
 */
public class TileGridLayout extends GridLayout {
    private static final String logTag = TileGridLayout.class.getSimpleName();

    Rect layoutRect = new Rect(), pictureRect;
    private Drawable mCustomImage;

    public TileGridLayout(Context context) {
        this(context, null);
        Log.d(logTag, "TileGridLayout(ctx)");
    }

    public TileGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(logTag, "TileGridLayout(ctx, atrs)");
        mCustomImage = context.getResources().getDrawable(R.drawable.pic_cute_girl);
        pictureRect = mCustomImage.getBounds();
        setWillNotDraw(false);
    }

    public TileGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(logTag, "TileGridLayout(ctx, atrs,style)");
        mCustomImage = context.getResources().getDrawable(R.drawable.pic_cute_girl);
        pictureRect = mCustomImage.getBounds();
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        getDrawingRect(layoutRect);
        Misc.calcCenter(layoutRect.width(), layoutRect.height(), mCustomImage.getMinimumWidth(), mCustomImage.getMinimumHeight(), layoutRect);
        mCustomImage.setBounds(layoutRect);
        mCustomImage.draw(canvas);
    }
}
