package lelisoft.com.lelimath.helpers;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Filter that checks valid characters for Values data class.
 * Created by Leoš on 06.02.2016.
 */
public class ValuesInputFilter implements InputFilter {

    // http://stackoverflow.com/questions/3349121/how-do-i-use-inputfilter-to-limit-characters-in-an-edittext-in-android
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean keepOriginal = true;
        StringBuilder sb = new StringBuilder(end - start);
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (isCharAllowed(c) && contractVerified(c, dest.toString())) {
                sb.append(c);
            } else {
                keepOriginal = false;
            }
        }

        if (keepOriginal) {
            return null;
        } else {
            if (source instanceof Spanned) {
                SpannableString sp = new SpannableString(sb);
                TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                return sp;
            } else {
                return sb;
            }
        }
    }

    private boolean isCharAllowed(char c) {
        return Character.isDigit(c) || c == '-' || c == ',';
    }

    private boolean contractVerified(char c, String dest) {
        if (c == '-') {
            return dest.indexOf('-') == -1 && dest.indexOf(',') == -1;
        }
        if (c == ',') {
            return dest.indexOf('-') == -1;
        }
        return true;
    }
}
