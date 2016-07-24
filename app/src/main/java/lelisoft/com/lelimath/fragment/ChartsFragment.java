package lelisoft.com.lelimath.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.TimePeriod;
import lelisoft.com.lelimath.helpers.DayAxisValueFormatter;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.provider.PlayRecordProvider;

/**
 * Displays points chart
 * Created by Leoš on 11.07.2016.
 */
public class ChartsFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(ChartsFragment.class);

    SimpleDateFormat dayFormat = new SimpleDateFormat("y-M-d");
    LineChart mChart;
    FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        return inflater.inflate(R.layout.fragment_dashboard_charts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);

        activity = getActivity();
        mChart = (LineChart) activity.findViewById(R.id.points_chart);
        mChart.setDrawGridBackground(true);

        mChart.setDescription("");
        mChart.setNoDataTextDescription(activity.getString(R.string.meesage_chart_no_data));

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(false);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
//        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);

        long startTime = setData();
        xAxis.setValueFormatter(new DayAxisValueFormatter(mChart, startTime));
//        mChart.animateX(500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        mChart.setVisibleXRangeMaximum(60);
//        mChart.setVisibleXRangeMinimum(7);
        mChart.moveViewToX(mChart.getXChartMax());

        Metrics.saveContentDisplayed("dashboard", "charts");
    }

    //    http://www.truiton.com/2015/04/android-chart-example-mp-android-chart-library/
    private long setData() {
        PlayRecordProvider provider = new PlayRecordProvider(activity);
        List<String[]> rawResults = provider.getPlayRecordsPointSums(System.currentTimeMillis(), TimePeriod.DAY, 60);
        ArrayList<Entry> values = new ArrayList<>(rawResults.size());
        long startTime = System.currentTimeMillis();

        if (! rawResults.isEmpty()) {
            String dateStr = rawResults.get(0)[0];
            try {
                Date date = dayFormat.parse(dateStr);
                startTime = date.getTime();
                for (String[] row : rawResults) {
                    dateStr = row[0];
                    date = dayFormat.parse(dateStr);
                    long days = TimeUnit.MILLISECONDS.toDays(date.getTime() - startTime);
                    int value = Integer.parseInt(row[1]);
                    values.add(new Entry(days, value, date));
                }
            } catch (ParseException e) {
                log.error("Failed to parse {} as date!", dateStr, e);
            }
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        // set the line to be drawn like this "- - - - - -"
//        set1.enableDashedLine(10f, 5f, 0f);
//        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        mChart.setData(data);

        return startTime;
    }
}
