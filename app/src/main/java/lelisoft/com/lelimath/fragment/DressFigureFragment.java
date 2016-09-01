package lelisoft.com.lelimath.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.gui.FigureView;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.provider.PlayRecordProvider;
import lelisoft.com.lelimath.view.Figure;

/**
 * User can buy new item to dress current item.
 * Created by Leoš on 26.08.2016.
 */
public class DressFigureFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(DressFigureFragment.class);

    Target target;
    Figure figure;
    FigureView figureView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        View view = inflater.inflate(R.layout.frg_dress_up_action, container, false);
        figureView = (FigureView) view.findViewById(R.id.figureView);
        TextView textView = (TextView) view.findViewById(R.id.header_points_count);
        PlayRecordProvider provider = new PlayRecordProvider(getContext());
        int points = provider.getPoints();
        textView.setText(getString(R.string.title_available_points, points));
        setupResources();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);
        figureView.post(new Runnable() {
            @Override
            public void run() {
                log.debug("size {} {}", figureView.getWidth(), figureView.getHeight());
            }
        });
        Metrics.saveContentDisplayed("dress", "figure");
    }

    private void setupResources() {
        try {
            Gson gson = new Gson();
            InputStream is = getContext().getAssets().open("dress_up.json");
            InputStreamReader reader = new InputStreamReader(is);
            figure = gson.fromJson(reader, Figure.class);
            figureView.setFigure(figure);

            target = new LoadPictureTarget();
            Picasso.with(getContext()).load(figure.getPath()).into(target);
        } catch (IOException e) {
            log.error("Failed to read JSON asset", e);
        }
    }

    class LoadPictureTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            log.debug("Loaded figure bitmap {} x {}", bitmap.getWidth(), bitmap.getHeight());
            figureView.setBitmap(bitmap);
            figureView.invalidate();
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
