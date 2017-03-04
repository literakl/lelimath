package lelisoft.com.lelimath.data;

import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import lelisoft.com.lelimath.R;

import static lelisoft.com.lelimath.helpers.LeliMathApp.resources;

/**
 * Holder for allowed values. You can either define both minimum and maximum, or set of values.
 * It is not allowed to combine set of values with minimum and maximum.
 * Created by leos.literak on 26.2.2015.
 */
public class Values implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Values.class);

    public static final Values DEMO = Values.fromRange(0, 10);
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final Values UNDEFINED = new UndefinedValues();

    private List<NumbersHolder> values;
    private Integer size;
    private SequenceOrder order;

    public static Values fromRange(Integer minValue, Integer maxValue) {
        Values result = new Values();
        result.values = Collections.singletonList((NumbersHolder) new RangeValues(minValue, maxValue));
        return result;
    }

    public static Values fromList(Integer... args) {
        Values result = new Values();
        result.values = Collections.singletonList((NumbersHolder) new ListValues(args));
        return result;
    }

    /**
     * Parses string containing requested numbers. It can be either range (0-9) or list (1,2,3)
     * @param sequence string
     * @param forceAscendingOrder if true every number must be bigger than the previous one
     * @return Values initialized using given parameter
     * @throws IllegalArgumentException parsing error
     */
    public static Values parse(CharSequence sequence, boolean forceAscendingOrder) throws IllegalArgumentException {
        if (sequence == null) {
            throw new IllegalArgumentException(resources.getString(R.string.error_empty_argument));
        }

        String data = sequence.toString().trim();
        if (data.length() == 0) {
            throw new IllegalArgumentException(resources.getString(R.string.error_empty_argument));
        }

        Values result = new Values();
        result.values = new ArrayList<>(5);
        StringTokenizer stk = new StringTokenizer(data, ",-", true);
        boolean rangeStarted = false;
        Integer unassignedNumber = null, previousNumber = null;
        ListValues currentListValues = new ListValues();

        while (stk.hasMoreTokens()) {
            String part = stk.nextToken();
            if ("-".equals(part)) {
                if (! forceAscendingOrder) {
                    throw new IllegalArgumentException(resources.getString(R.string.error_values_range_no_ascending_order));
                } else if (rangeStarted) {
                    throw new IllegalArgumentException(resources.getString(R.string.error_values_undefined_second));
                } else if (unassignedNumber == null) {
                    throw new IllegalArgumentException(resources.getString(R.string.error_values_undefined_first));
                } else {
                    if (currentListValues.size() > 0) {
                        result.values.add(currentListValues);
                        currentListValues = new ListValues();
                    }

                    rangeStarted = true;
                    continue;
                }
            }

            if (",".equals(part)) {
                if (rangeStarted) {
                    throw new IllegalArgumentException(resources.getString(R.string.error_values_undefined_second));
                }
                if (unassignedNumber != null) {
                    currentListValues.add(unassignedNumber);
                    unassignedNumber = null;
                }
                continue;
            }

            int value = parseNumber(part);
            if (previousNumber != null && previousNumber >= value && forceAscendingOrder) {
                throw new IllegalArgumentException(resources.getString(R.string.error_values_must_be_bigger));
            }
            previousNumber = value;

            if (rangeStarted) {
                result.values.add(new RangeValues(unassignedNumber, value));
                unassignedNumber = null;
                rangeStarted = false;
            } else {
                if (unassignedNumber != null) {
                    currentListValues.add(unassignedNumber);
                }
                unassignedNumber = value;
            }
        }

        if (rangeStarted) {
            throw new IllegalArgumentException(resources.getString(R.string.error_values_undefined_second));
        }

        if (unassignedNumber != null) {
            currentListValues.add(unassignedNumber);
        }

        if (currentListValues.size() > 0) {
            result.values.add(currentListValues);
        }

        return result;
    }

    private static int parseNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(resources.getString(R.string.error_values_not_number, s));
        }
    }

    /**
     * @param value argument
     * @return finds if the value is within a definition
     */
    public boolean belongs(Integer value) {
        for (NumbersHolder holder : values) {
            if (holder.contains(value))
                return true;
        }
        return false;
    }

    /**
     * Calculates number of potential values.
     * @return number of values belonging to this Values
     */
    public int getSize() {
        int size = 0;
        for (NumbersHolder holder : values) {
            size += holder.size();
        }
        return size;
    }

    public int getMaximumValue() {
        NumbersHolder holder = values.get(values.size() - 1);
        return holder.getMaximumValue();
    }

    /**
     * Randomly selects a number from given Values. It returns 1 for Undefined Values.
     * @return select random value from Values
     */
    public Integer getRandomValue(Random random) {
        if (this.equals(Values.UNDEFINED)) {
            log.warn("Generating value from Values.Undefined!", new Exception("Stacktrace"));
            return 1;
        }

        if (size == null) {
            size = getSize();
        }

        int position = random.nextInt(size);
        int i = 0;
        for (NumbersHolder holder : values) {
            int j = holder.size();
            if (i + j <= position) {
                i += j;
                continue;
            }

            return holder.getValueAtPosition(position - i);
        }

        throw new RuntimeException("Failed to generate random value, position=" + position + " i = " + i + ", values=" + values);
    }

    /**
     * Selects a number from given Values at given position. It returns 1 for Undefined Values or for position out of bounds.
     * @param position cursor
     * @return value at selected position
     */
    public Integer getValueAt(int position) {
        if (this.equals(Values.UNDEFINED)) {
            log.warn("Generating value from Values.Undefined!", new Exception("Stacktrace"));
            return 1;
        }

        if (size == null) {
            size = getSize();
        }

        if (position < 0 || position >= size) {
            log.warn("Cannot get value at position {}, size = {}", position, size, new Exception("Stacktrace"));
            return 1;
        }

        int i = 0;
        for (NumbersHolder holder : values) {
            int j = holder.size();
            if (i + j <= position) {
                i += j;
                continue;
            }

            return holder.getValueAtPosition(position - i);
        }

        throw new RuntimeException("Failed to get value, position=" + position + " i = " + i + ", values=" + values);
    }

    public SequenceOrder getOrder() {
        return order;
    }

    public Values setOrder(SequenceOrder order) {
        this.order = order;
        return this;
    }

    private static class UndefinedValues extends Values {
        @Override
        public boolean belongs(Integer value) {
            return value >= 0;
        }

        @Override
        public int getSize() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaximumValue() {
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

    /**
     * Defines a set of numbers
     */
    private static class ListValues implements NumbersHolder, Serializable {
        /** List of values */
        List<Integer> list;

        ListValues() {
            this.list = new ArrayList<>();
        }

        @SuppressWarnings("unused")
        public ListValues(List<Integer> listing) {
            this.list = new ArrayList<>(listing);
        }

        ListValues(Integer[] listing) {
            this.list = new ArrayList<>(listing.length);
            Collections.addAll(this.list, listing);
        }

        public void add(Integer value) {
            list.add(value);
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        public boolean contains(Integer value) {
            return list.contains(value);
        }

        @Override
        public Integer getMinimumValue() {
            return list.get(0);
        }

        @Override
        public Integer getMaximumValue() {
            return list.get(list.size() - 1);
        }

        @Override
        public Integer getValueAtPosition(int position) {
            return list.get(position);
        }

        @Override
        public String toString() {
            return "ListValues{" +
                    "list=" + list +
                    '}';
        }
    }

    /**
     * Defines a range value (min - max inclusive)
     */
    private static class RangeValues implements NumbersHolder, Serializable {
        /** Smallest value */
        Integer minValue;
        /** Largest value */
        Integer maxValue;

        @SuppressWarnings("unused")
        public RangeValues() {
        }

        RangeValues(Integer minValue, Integer maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public int size() {
            return maxValue - minValue + 1;
        }

        @Override
        public boolean contains(@NonNull Integer value) {
            return value >= minValue && value <= maxValue;
        }

        @Override
        public Integer getMinimumValue() {
            return minValue;
        }

        @Override
        public Integer getMaximumValue() {
            return maxValue;
        }

        @Override
        public Integer getValueAtPosition(int position) {
            return minValue + position;
        }

        @Override
        public String toString() {
            return "RangeValues{" +
                    "min=" + minValue +
                    ", max=" + maxValue +
                    '}';
        }
    }

    /**
     * Interface for different number holders
     */
    private interface NumbersHolder {
        /**
         * @return number of numbers
         */
        int size();

        /**
         * @param value searched number
         * @return true if value is part of this value holder
         */
        boolean contains(Integer value);

        /**
         * @return the smallest value
         */
        @SuppressWarnings("unused")
        Integer getMinimumValue();

        /**
         * @return the biggest value
         */
        Integer getMaximumValue();

        /**
         * @return value at specified position
         */
        Integer getValueAtPosition(int position);
    }

    @Override
    public String toString() {
        return "Values{" +
                "values=" + values +
                '}';
    }
}
