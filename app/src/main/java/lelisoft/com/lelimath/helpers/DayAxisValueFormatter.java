package lelisoft.com.lelimath.helpers;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.util.Calendar;

/**
 * Formats date where value is a index in days from starting point.
 * Created by Leoš on 11.07.2016.
 */
public class DayAxisValueFormatter implements AxisValueFormatter {

    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    private BarLineChartBase<?> chart;
    long startTime;

    public DayAxisValueFormatter(BarLineChartBase<?> chart, long startTime) {
        this.chart = chart;
        this.startTime = startTime;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        calendar.add(Calendar.DAY_OF_YEAR, (int)value);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        String monthName = mMonths[month % mMonths.length];
        String yearName = String.valueOf(year);

        if (chart.getVisibleXRange() > 30 * axis.getLabelCount()) {
            return monthName + " " + yearName;
        } else {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            String appendix = "th";
            switch (dayOfMonth) {
                case 1:
                    appendix = "st";
                    break;
                case 2:
                    appendix = "nd";
                    break;
                case 3:
                    appendix = "rd";
                    break;
                case 21:
                    appendix = "st";
                    break;
                case 22:
                    appendix = "nd";
                    break;
                case 23:
                    appendix = "rd";
                    break;
                case 31:
                    appendix = "st";
                    break;
            }

            return dayOfMonth == 0 ? "" : dayOfMonth + appendix + " " + monthName;
        }
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
