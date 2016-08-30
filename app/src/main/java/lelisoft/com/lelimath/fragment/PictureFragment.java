package lelisoft.com.lelimath.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.helpers.LeliMathApp;

/**
 * Fragment displaying picture
 * Created by Leoš on 27.02.2016.
 */
public class PictureFragment extends Fragment implements View.OnClickListener {
    private static final Logger log = LoggerFactory.getLogger(PictureFragment.class);

    public static final String ARG_PICTURE = "picture";

    PictureBridge callback;

    public interface PictureBridge {
        void restartGame();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.frg_picture, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        TextView button = (TextView) getActivity().findViewById(R.id.buttonSubmit);
        button.setOnClickListener(this);

        ImageView view = (ImageView) getActivity().findViewById(R.id.picture);
        int picture = getArguments().getInt(ARG_PICTURE);
        if (picture != 0) {
            Picasso.with(getContext()).load(picture).into(view);
        }

        LeliMathApp.getInstance().playSound(R.raw.victory);
    }

    @Override
    public void onClick(View v) {
        log.debug("onClick()");
        callback.restartGame();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented the callback interface
        try {
            callback = (PictureBridge) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PictureBridge");
        }
    }
}
