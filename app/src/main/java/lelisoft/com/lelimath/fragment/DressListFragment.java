package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import lelisoft.com.lelimath.R;

/**
 * Lists available figures.
 * Created by Leoš on 09.10.2016.
 */

public class DressListFragment extends LeliBaseFragment implements View.OnClickListener {
    private static final Logger log = LoggerFactory.getLogger(DressListFragment.class);

    ImageButton buttonVilma, buttonPirate;
    FragmentSwitcher callback;

    public interface FragmentSwitcher {
        void startDressingFragment(String figureDefinition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        log.debug("onCreateView()");
        View view = inflater.inflate(R.layout.frg_dress_up_list, container, false);
        buttonVilma = (ImageButton) view.findViewById(R.id.buttonVilma);
        buttonVilma.setOnClickListener(this);
        buttonPirate = (ImageButton) view.findViewById(R.id.buttonPirate);
        buttonPirate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        loadFigurePicture("Vilma", R.drawable.drs_vilma_base, buttonVilma);
        loadFigurePicture("Pirate", R.drawable.drs_pirate_base, buttonPirate);
    }

    private void loadFigurePicture(String figure, int resource, ImageView view) {
        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), figure + ".png");
        if (file.exists()) {
            Picasso.with(getContext()).load(file).into(view);
        } else {
            Picasso.with(getContext()).load(resource).into(view);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonVilma) {
            callback.startDressingFragment("dress/vilma/default.json");
        }
        if (v.getId() == R.id.buttonPirate) {
            callback.startDressingFragment("dress/pirate/default.json");
        }
    }

    @Override
    public void onAttach(Context context) {
        log.debug("onAttach()");
        super.onAttach(context);

        // This makes sure that the container activity has implemented the callback interface
        try {
            callback = (FragmentSwitcher) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FragmentSwitcher");
        }
    }
}
