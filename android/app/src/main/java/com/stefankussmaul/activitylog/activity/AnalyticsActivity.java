package com.stefankussmaul.activitylog.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.charts.ChartConfig;
import com.stefankussmaul.activitylog.charts.SessionsValueFormatter;
import com.stefankussmaul.activitylog.content.ActivityAggregate;
import com.stefankussmaul.activitylog.charts.ChartUtil;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DBUtil;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.LogEntry;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 5/29/2017.
 */

public class AnalyticsActivity extends AppCompatActivity implements
        LogFilterFragment.OnFilterUpdatedListener, ChartConfigFragment.OnConfigChangeListener {

    // todo: better management of activity lifecycle?
    // used to switch between charts
    private ViewSwitcher chartSwitcher;
    // pie chart for displaying percentages of each activity
    private PieChart pieChart;
    // line chart for displaying aggregates over a date range for each activity
    private LineChart lineChart;
    // title displayed at top of screen
    private TextView chartTitle;
    // textview displaying total number of sessions and time spent in selected filter
    private TextView statsOverviewLabel;
    // handle to database
    private DBManager dbManager;

    private QueryBuilder currentQuery;
    private ChartConfig.ChartBy chartBy = ChartConfig.ChartBy.NUM_SESSIONS;
    private ChartConfig.ChartType chartType = ChartConfig.ChartType.PIE;

    List<ActivityAggregate> timeSpentAggregates;
    List<ActivityAggregate> numSessionAggregates;

    private long totalTimeSpent;
    private long totalNumSessions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Analytics Activity", "OnCreate");
        setContentView(R.layout.analytics_layout);
        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable up button to go back to MainActivity
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        dbManager = new DBManager(this);

        chartSwitcher = (ViewSwitcher) findViewById(R.id.chart_switcher);

        pieChart = (PieChart) findViewById(R.id.pie_chart);
//        pieChart.setDrawEntryLabels(false);
        // todo: programmatically with respect to screen dimensions
        pieChart.setMinimumHeight(650);
        pieChart.setMinimumWidth(650);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        lineChart = (LineChart) findViewById(R.id.line_chart);
        lineChart.setMinimumHeight(650);
        lineChart.setMinimumWidth(650);

        chartTitle = (TextView) findViewById(R.id.chart_title);
        statsOverviewLabel = (TextView) findViewById(R.id.stats_overview);

        // generate standard chart, no filter
        onFilterUpdated(null, new QueryBuilder());
    }

    @Override
    public void onFilterUpdated(LogFilterFragment logFilterFragment, QueryBuilder newQuery) {
        if (currentQuery != null) {
            Log.d("AnalyticsActivity", "Current Query is " + currentQuery.getQuery());
        }
        Log.d("AnalyticsActivity", "Received query " + newQuery.getQuery());
        if (!newQuery.equals(currentQuery)) {
            // run queries for both time spent and num sessions for the new query
            Cursor time_cursor = dbManager.runQuery(newQuery.getTimeSpentQuery());
            Cursor sessions_cursor = dbManager.runQuery(newQuery.getSessionCountQuery());
            // retrieve aggregates from each cursor
            timeSpentAggregates = DBUtil.getAggregatesFromCursor(time_cursor);
            numSessionAggregates = DBUtil.getAggregatesFromCursor(sessions_cursor);
            // calculate overall statistics
            totalTimeSpent = DBUtil.getTotalOfAggregates(timeSpentAggregates);
            totalNumSessions = DBUtil.getTotalOfAggregates(numSessionAggregates);
            // updates the title to reflect the new query
            chartTitle.setText(ChartUtil.getChartLabel(this, newQuery));
            // updates the overview stats to reflect the calculated overall stats
            statsOverviewLabel.setText(ChartUtil.getOverviewLabel(this, totalNumSessions, totalTimeSpent));
            // call a refresh of whatever chart is currently displayed
            refreshChart();
            // update currentQuery to a clone of the given query
            currentQuery = new QueryBuilder(newQuery);
        }
    }

    @Override
    public void onConfigChanged(ChartConfigFragment chartConfigFragment, ChartConfig config) {
        Log.d("AnalyticsActivity", "Received Chart Config Change to " + config);
        boolean switch_chart = false;
        if (config.getChartType() != chartType) {
            switch_chart = true;
        }
        chartType = config.getChartType();
        chartBy = config.getChartBy();
        refreshChart();
        if (switch_chart) {
            Log.d("AnalyticsActivity", "Switching Charts");
            chartSwitcher.showNext();
        }
    }

    private void refreshChart() {
        if (chartType == ChartConfig.ChartType.PIE) {
            if (chartBy == ChartConfig.ChartBy.NUM_SESSIONS) {
                drawPieChart(numSessionAggregates, getString(R.string.num_sessions));
            } else {
                drawPieChart(timeSpentAggregates, getString(R.string.time_spent));
                Log.d("Analytics Activity", "Drawing Time Spent " + ChartUtil.aggregatesToString(timeSpentAggregates));
            }
        } else {
            drawLineChart(currentQuery, "TestLineChart");
        }
    }

    public void drawPieChart(List<ActivityAggregate> data, String setLabel) {
        // if no data to display, set the Chart to blank data
        if (data.isEmpty()) { // todo: crashes
            pieChart.setData(new PieData());
            pieChart.invalidate();
        } else {
            PieDataSet data_set = new PieDataSet(ChartUtil.getPieChartEntries(data), setLabel);
            data_set.setColors(ColorTemplate.JOYFUL_COLORS);
            data_set.setValueFormatter(new SessionsValueFormatter());
            data_set.setValueTextColor(Color.WHITE);
            pieChart.setData(new PieData(data_set));
            pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            pieChart.invalidate();
        }
    }

    public void drawLineChart(QueryBuilder query, String setLabel) {
        int precision = Calendar.DAY_OF_MONTH;
        Date min_date, max_date;
        // we need to establish a date range for the graph
        if (query.hasMinDate()) { // todo: deep copy?
            min_date = query.getMinDate();
        } else {
            // retrieve oldest LogEntry in the query todo: could crash
            List<LogEntry> oldest = DBUtil.getLogsFromCursor(dbManager.runQuery(query.getXOldestQuery(1)));
            if (oldest.isEmpty()) {
                return; // todO
            }
            min_date = oldest.get(0).getDate();
        }
        if (query.hasMaxDate()) { // todo: deep copy?
            max_date = query.getMaxDate();
        } else {
            // retrieve newest LogEntry in the query todo: could crash
            LogEntry newest = DBUtil.getLogsFromCursor(dbManager.runQuery(query.getXNewestQuery(1))).get(0);
            max_date = newest.getDate();
        }
        // strip min/max dates to proper precision
        min_date = DateUtil.stripToPrecision(min_date, precision);
        max_date = DateUtil.stripToPrecision(max_date, precision);
        // generate list of dates for the intervals in the larger range
        List<Date> intervals = DateUtil.getIntervals(min_date, max_date, DateUtil.getMSInPrecision(precision));
        // use calculated intervals to get a list of queries with all filters the same but dates
        // modified to be between the intervals
        List<QueryBuilder> queries = DateUtil.getQueriesOverInterval(query, intervals);
        // run the queries to get a list of activity aggregates
        List<List<ActivityAggregate>> aggregates = DBUtil.runQueries(dbManager, queries, chartBy);
        // convert list of lists into a list of LineDataSets
        List<ILineDataSet> sets = new ArrayList<>();
        for (List<ActivityAggregate> l : aggregates) {
            sets.add(ChartUtil.aggsToLineData(l));
        }
        lineChart.setData(new LineData(sets));
        lineChart.invalidate();
//        Date min_date = (query.hasMinDate() ? query.getMinDate() : dbManager.run)
//            List<Entry> set_1_e = new LinkedList<>();
//            set_1_e.add(new Entry(100, 200));
//            set_1_e.add(new Entry(300, 50));
//            LineDataSet set_1 = new LineDataSet(set_1_e, "test1");
//
//            List<Entry> set_2_e = new LinkedList<>();
//            set_2_e.add(new Entry(100, 100));
//            set_2_e.add(new Entry(300, 170));
//            LineDataSet set_2 = new LineDataSet(set_2_e, "test2");
//
//            set_1.setColor(Color.GREEN);
//            set_1.setAxisDependency(YAxis.AxisDependency.LEFT);
//            set_2.setColor(Color.BLUE);
//            set_2.setAxisDependency(YAxis.AxisDependency.LEFT);
//            data_set.setValueFormatter(new SessionsValueFormatter());
//            data_set.setValueTextColor(Color.WHITE);
//            List<ILineDataSet> all_date = new LinkedList<>();
//            all_date.add(set_1);
//            all_date.add(set_2);
//            lineChart.setData(new LineData(all_date));
//            lineChart.invalidate();
//        }
    }
}
