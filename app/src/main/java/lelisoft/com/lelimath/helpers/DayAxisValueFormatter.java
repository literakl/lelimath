package lelisoft.com.lelimath.helpers;


import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Formats date where value is a index in days from starting point.
 * Created by Leoš on 11.07.2016.
 */
public class DayAxisValueFormatter implements AxisValueFormatter {
    DateFormat monthYear, dayMonth;

    private BarLineChartBase<?> chart;
    long startTime;

    public DayAxisValueFormatter(BarLineChartBase<?> chart, long startTime) {
        this.chart = chart;
        this.startTime = startTime;
        String monthFormat = "MMM yyyy", dayFormat = "dd MM";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            monthFormat = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "MM.y");
            dayFormat = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "dd.MM.");
        }
        monthYear = new SimpleDateFormat(monthFormat);
        dayMonth = new SimpleDateFormat(dayFormat);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        calendar.add(Calendar.DAY_OF_YEAR, (int)value);

//        Logger log = LoggerFactory.getLogger(DayAxisValueFormatter.class);
//        log.debug("{}", dayMonth.format(calendar.getTime()));
//        log.debug("{}", monthYear.format(calendar.getTime()));

        if (chart.getVisibleXRange() > 30 * axis.getLabelCount()) {
            return monthYear.format(calendar.getTime());
        } else {
            return dayMonth.format(calendar.getTime());
        }
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
