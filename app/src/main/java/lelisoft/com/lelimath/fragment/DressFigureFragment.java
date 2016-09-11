package lelisoft.com.lelimath.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.DressPartAdapter;
import lelisoft.com.lelimath.event.DressPartSelectedEvent;
import lelisoft.com.lelimath.gui.FigureView;
import lelisoft.com.lelimath.helpers.BalanceHelper;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.view.DressPart;
import lelisoft.com.lelimath.view.Figure;

/**
 * User can buy new item to dress current item.
 * Created by Leoš on 26.08.2016.
 */
public class DressFigureFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(DressFigureFragment.class);

    public static final String KEY_BOUGHT_PARTS = "bought.parts.";

    Target target;
    Figure figure;
    TextView balanceView;
    FigureView figureView;
    DressPartAdapter adapter;
    RecyclerView recyclerView;
    BalanceHelper balanceHelper;
    SharedPreferences sharedPref;
    List<String> boughtParts = new ArrayList<>();
    List<DressPart> availableParts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        View view = inflater.inflate(R.layout.frg_dress_up_action, container, false);
        figureView = (FigureView) view.findViewById(R.id.figure_view);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        balanceHelper = LeliMathApp.getBalanceHelper();
//        balanceHelper.setDeveloperMode(true, 1000);
        int balance = balanceHelper.getBalance();
        balanceView.setText(getString(R.string.title_available_points, balance));

        recyclerView = (RecyclerView)view.findViewById(R.id.dress_parts_selection);
        recyclerView.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.dress_parts_columns);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
        recyclerView.setLayoutManager(layoutManager);

        setupResources(balance);
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

    private void setupResources(int balance) {
        try {
            Gson gson = new Gson();
            InputStream is = getContext().getAssets().open("dress/default.json");
            InputStreamReader reader = new InputStreamReader(is);
            figure = gson.fromJson(reader, Figure.class);
            figureView.setFigure(figure);

            String values = sharedPref.getString(getPrefKey(), "");
            StringTokenizer stk = new StringTokenizer(values, ",");
            while (stk.hasMoreTokens()) {
                boughtParts.add(stk.nextToken());
            }
            figureView.displayParts(boughtParts);

            availableParts = new ArrayList<>();
            for (int i = 0; i < figure.getParts().length; i++) {
                DressPart part = figure.getParts()[i];
                if (! boughtParts.contains(part.getId())) {
                    availableParts.add(part);
                }
            }

            if (availableParts.isEmpty()) {
                if (getActivity().getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                recyclerView.setVisibility(View.GONE);
            } else {
                adapter = new DressPartAdapter(getContext(), availableParts, balance);
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
            }

            target = new LoadPictureTarget();
            Picasso.with(getContext()).load(figure.getPath()).into(target);
        } catch (IOException e) {
            log.error("Failed to read JSON asset", e);
            Crashlytics.logException(e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDressPartSelected(final DressPartSelectedEvent event) {
        log.debug("onDressPartSelected");
        boughtParts.add(event.getPart().getId());
        figureView.displayParts(boughtParts);
        figureView.invalidate();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onDressPartBought(event.getPart());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onDressPartCancelled(event.getPart());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setTitle(R.string.dialog_buy_part);

        Window window = dialog.getWindow();
        window.setDimAmount(0.2f);
        window.getAttributes().x = (int) Misc.pxFromDp(getContext(), 5);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.LEFT;
        wlp.flags &= ~ WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
        window.setLayout((int) Misc.pxFromDp(getContext(), 200), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onDressPartBought(DressPart part) {
        log.debug("onDressPartBought");
        int balance = balanceHelper.add(-1 * part.getPrice());
        availableParts.remove(part);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getPrefKey(), Misc.toCSV(boughtParts));
        editor.apply();

        if (availableParts.isEmpty()) {
            if (getActivity().getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return; // fragment will be destroyed and recreated
            }
            recyclerView.setVisibility(View.GONE);
        } else {
            adapter.setBalance(balance);
            adapter.notifyDataSetChanged();
        }

        balanceView.setText(getString(R.string.title_available_points, balance));
        Metrics.saveFigureDressed(figure.getId(), part.getId());
    }

    public void onDressPartCancelled(DressPart part) {
        log.debug("onDressPartCancelled");
        boughtParts.remove(part.getId());
        figureView.displayParts(boughtParts);
        figureView.invalidate();
    }

    private String getPrefKey() {
//        return KEY_BOUGHT_PARTS + figure.getId();
        return KEY_BOUGHT_PARTS + figure.getId()+"uw";
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

    public void setBalanceView(TextView balanceView) {
        this.balanceView = balanceView;
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
