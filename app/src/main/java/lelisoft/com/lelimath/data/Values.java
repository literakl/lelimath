package lelisoft.com.lelimath.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Holder for allowed values. You can either define both minimum and maximum, or set of values.
 * It is not allowed to combine set of values with minimum and maximum.
 * Created by leos.literak on 26.2.2015.
 */
public class Values {
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
            throw new IllegalArgumentException("Null is prohibited");
        }
        String data = sequence.toString().trim();
        if (data.length() == 0) {
            throw new IllegalArgumentException("No data specified!");
        }

        Values values = new Values();
        int position = data.indexOf('-');
        if (position != -1) {
            String sFirst = data.substring(0, position);
            String sSecond = data.substring(position + 1);
            values.minValue = Integer.parseInt(sFirst.trim());
            values.maxValue = Integer.parseInt(sSecond.trim());
        } else {
            StringTokenizer stk = new StringTokenizer(data, ",");
            while(stk.hasMoreElements()) {
                String s = stk.nextToken().trim();
                if (s.length() > 0) {
                    values.add(Integer.parseInt(s));
                }
            }
        }
        return values;
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

    public String toString() {
        return "Values{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", listing=" + listing +
                '}';
    }
}
