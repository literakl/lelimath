package lelisoft.com.lelimath.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.helpers.Metrics;

/**
 * User can buy new item to dress current item.
 * Created by Leoš on 26.08.2016.
 */
public class DressFigureFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(DressFigureFragment.class);
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.frg_dress_up_action, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);

        final ImageView imageView = (ImageView) getActivity().findViewById(R.id.figureView);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                log.debug("imageView post {} x {}", imageView.getWidth(), imageView.getHeight());
            }
        });

        Picasso.with(getContext()).load(R.drawable.amelia).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                log.debug("Setting figure bitmap {} x {}", bitmap.getWidth(), bitmap.getHeight());
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                log.debug("onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                log.debug("onPrepareLoad");
            }
        });

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
    public static BadgeListFragment newInstance() {
        return new BadgeListFragment();
    }
}
