package lelisoft.com.lelimath.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.DressPartAdapter;
import lelisoft.com.lelimath.event.DressPartSelectedEvent;
import lelisoft.com.lelimath.gui.FigureView;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.view.Figure;

/**
 * User can buy new item to dress current item.
 * Created by Leoš on 26.08.2016.
 */
public class DressFigureFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(DressFigureFragment.class);

    int balance;
    Target target;
    Figure figure;
    FigureView figureView;
    RecyclerView recyclerView;
    Set<String> parts = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        View view = inflater.inflate(R.layout.frg_dress_up_action, container, false);
        figureView = (FigureView) view.findViewById(R.id.figure_view);

        recyclerView = (RecyclerView)view.findViewById(R.id.dress_parts_selection);
        recyclerView.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.dress_parts_columns);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
        recyclerView.setLayoutManager(layoutManager);

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
            InputStream is = getContext().getAssets().open("dress/default.json");
            InputStreamReader reader = new InputStreamReader(is);
            figure = gson.fromJson(reader, Figure.class);
            figureView.setFigure(figure);

            target = new LoadPictureTarget();
            Picasso.with(getContext()).load(figure.getPath()).into(target);

            DressPartAdapter adapter = new DressPartAdapter(getContext(), figure.getParts(), balance);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
        } catch (IOException e) {
            log.error("Failed to read JSON asset", e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDressPartSelected(DressPartSelectedEvent event) {
        log.debug("onDressPartSelected");
        if (! parts.remove(event.getPart().getId())) {
            parts.add(event.getPart().getId());
        }
        figureView.displayParts(parts);
        figureView.invalidate();
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
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
