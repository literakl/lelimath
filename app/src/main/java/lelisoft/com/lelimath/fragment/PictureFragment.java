package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;

/**
 * Fragment displaying picture
 * Created by Leoš on 27.02.2016.
 */
public class PictureFragment extends Fragment {
    private static final Logger log = LoggerFactory.getLogger(PictureFragment.class);

    public static final String ARG_PICTURE = "picture";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.debug("onCreateView()");
        ImageView view = (ImageView) inflater.inflate(R.layout.template_picture, container, false);
        int picture = getArguments().getInt(ARG_PICTURE);
        if (picture != 0) {
            // TODO scaling? reading in backgound?
            view.setImageResource(picture);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }
}
