package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.helpers.Metrics;

/**
 * User can buy new item to dress current item.
 * Created by Leoš on 26.08.2016.
 */
public class DressFigureFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(DressFigureFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.frg_dress_up_action, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);
        Metrics.saveContentDisplayed("dress", "figure");
    }

    /*
    private Canvas canvas;
    private ImageView imageview;
    private Paint paint;
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inScaled = true;
        opt.inMutable = true;
        opt.inInputShareable = true;
        Bitmap figureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.amelia, opt);
        Bitmap dressBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tshirt, opt);
        Bitmap pantsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pants, opt);
//        Bitmap b = Bitmap.createBitmap(115, 441,  Bitmap.Config.ARGB_8888);
        canvas = new Canvas(figureBitmap);
        canvas.drawBitmap(pantsBitmap, 0, 0, null);
        canvas.drawBitmap(dressBitmap, 0, 0, null);
//        canvas.drawColor(Color.WHITE);
        imageview=(ImageView) findViewById(R.id.figureImage);
        imageview.setImageBitmap(figureBitmap);

 */
}
