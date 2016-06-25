package lelisoft.com.lelimath.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.helpers.LeliMathApp;

/**
 * Holder for allowed values. You can either define both minimum and maximum, or set of values.
 * It is not allowed to combine set of values with minimum and maximum.
 * Created by leos.literak on 26.2.2015.
 */
public class Values implements Serializable {
    public static final Values DEMO = new Values(0, 10);
    public static final Values UNDEFINED = new UndefinedValues();

    /** Smallest value */
    Integer minValue;
    /** Largest value */
    Integer maxValue;
    /** List of values */
    List<Integer> listing;

    public Values(Integer minValue, Integer maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Values(List<Integer> listing) {
        this.listing = listing;
    }

    public Values(Integer[] listing) {
        this.listing = Arrays.asList(listing);
    }

    public Values(int value) {
        minValue = value;
        maxValue = value;
    }

    public Values() {
    }

    /**
     * Parses string containing requested numbers. It can be either range (0-9) or list (1,2,3)
     * @param sequence string
     * @return Values initialized using given parameter
     * @throws IllegalArgumentException parsing error
     */
    public static Values parse(CharSequence sequence) throws IllegalArgumentException {
        if (sequence == null) {
            throw new IllegalArgumentException(LeliMathApp.resources.getString(R.string.error_empty_argument));
        }
        String data = sequence.toString().trim();
        if (data.length() == 0) {
            throw new IllegalArgumentException(LeliMathApp.resources.getString(R.string.error_empty_argument));
        }

        Values values = new Values();
        int position = data.indexOf('-');
        if (position != -1) {
            String sFirst = data.substring(0, position);
            String sSecond = data.substring(position + 1);
            values.minValue = parseNumber(sFirst.trim());
            String secondNumber = sSecond.trim();
            if (secondNumber.length() > 0) {
                values.maxValue = parseNumber(secondNumber);
            } else {
                throw new IllegalArgumentException(LeliMathApp.resources.getString(R.string.error_values_undefined_second));
            }
            if (values.minValue > values.maxValue) {
                throw new IllegalArgumentException(LeliMathApp.resources.getString(R.string.error_values_swapped));
            }
        } else {
            StringTokenizer stk = new StringTokenizer(data, ",");
            while(stk.hasMoreElements()) {
                String s = stk.nextToken().trim();
                if (s.length() > 0) {
                    values.add(parseNumber(s));
                }
            }
        }
        return values;
    }

    private static int parseNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(LeliMathApp.resources.getString(R.string.error_values_not_number, s));
        }
    }

    /**
     * Compares argument with this Values set.
     * @param value argument
     * @return true if value is listed or between minimum and maximum.
     */
    public boolean belongs(Integer value) {
        if (listing != null && ! listing.isEmpty()) {
            return listing.contains(value);
        }
        if (minValue == null || maxValue == null) {
            return false;
        }
        return value.compareTo(minValue) >= 0 && value.compareTo(maxValue) <= 0;
    }

    /**
     * Calculates number of potential values.
     * @return number of values belonging to this Values
     */
    public int getRange() {
        if (listing != null) {
            return listing.size();
        }
        return maxValue - minValue + 1;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        assert listing == null;
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        assert listing == null;
        this.maxValue = maxValue;
    }

    public List<Integer> getListing() {
        return listing;
    }

    public Values add(Integer value) {
        assert minValue == null;
        assert maxValue == null;
        if (listing == null) {
            listing = new ArrayList<>(5);
        }
        listing.add(value);
        return this;
    }

    public void setListing(List<Integer> listing) {
        assert minValue == null;
        assert maxValue == null;
        this.listing = listing;
    }

    public int getMaximumLength() {
        if (listing != null && listing.size() > 0) {
            int max = 0, size;
            for (Integer number : listing) {
                size = getNumberLength(number);
                if (size > max) {
                    max = size;
                }
            }
            return max;
        } else {
            int a = getNumberLength(minValue);
            int b = getNumberLength(maxValue);
            return Math.max(a, b);
        }
    }

    private int getNumberLength(int number) {
        if (number == 0) {
            return 1;
        }
        int size = 0;
        if (number < 0) {
            size += 1;
            number *= -1;
        }
        size += (int)(Math.log10(number) + 1);
        return size;
    }

    static class UndefinedValues extends Values {
        @Override
        public boolean belongs(Integer value) {
            return value >= 0;
        }

        @Override
        public int getRange() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Integer getMinValue() {
            return 0;
        }

        @Override
        public Integer getMaxValue() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaximumLength() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof UndefinedValues;
        }

        @Override
        public String toString() {
            return "UndefinedValues";
        }
    }

    public String toString() {
        if (listing != null) {
            return "Values{" +
                    "listing=" + listing +
                    '}';
        } else {
            return "Values{" + minValue + " - " + maxValue + '}';
        }
    }
}
