package lelisoft.com.lelimath.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import lelisoft.com.lelimath.R;

/**
 * Dynamically stylable component.
 * Created by Leoš on 08.09.2016.
 * Cudos to http://ptrprograms.blogspot.cz/2015/01/implementing-and-using-custom-drawable.html
 */
public class DressPartPriceView extends TextView {
    private static final int[] PRICE_STATE = {R.attr.state_price_enabled};
    boolean mIsEnabled;

    public DressPartPriceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DressPartPriceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            if ("state_price_enabled".equals(attrs.getAttributeName(i))) {
                mIsEnabled = attrs.getAttributeBooleanValue(i, false);
                return;
            }
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mIsEnabled) {
            mergeDrawableStates(drawableState, PRICE_STATE);
        }
        return drawableState;
    }

    public void setEnabled(boolean enabled) {
        if (mIsEnabled != enabled) {
            mIsEnabled = enabled;
            refreshDrawableState();
        }
    }
}
