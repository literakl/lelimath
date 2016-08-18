package lelisoft.com.lelimath.helpers;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by Leoš on 18.08.2016.
 */
public class SliderPreferenceCompat extends DialogPreference {

    public SliderPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SliderPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SliderPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SliderPreferenceCompat(Context context) {
        super(context);
    }


}
