package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.charts.SessionsValueFormatter;
import com.stefankussmaul.activitylog.content.ActivityAggregate;
import com.stefankussmaul.activitylog.charts.ChartUtil;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DBUtil;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.util.List;

/**
 * Created by Stefan on 5/29/2017.
 */

public class AnalyticsActivity extends AppCompatActivity implements LogFilterFragment.OnFilterUpdatedListener {

    // todo: better management of activity lifecycle?
    private PieChart pieChart;

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

        DBManager logManager = new DBManager(this);
        QueryBuilder query_builder = new QueryBuilder();
        List<ActivityAggregate> counts = DBUtil.getAggregatesFromCursor(logManager.runQuery(query_builder.getActivityCountQuery()));
        pieChart = (PieChart) findViewById(R.id.pie_chart);

        PieDataSet data_set = new PieDataSet(ChartUtil.getPieChartEntries(counts), "Session Counts");
        data_set.setColors(ColorTemplate.JOYFUL_COLORS);
        data_set.setValueFormatter(new SessionsValueFormatter());
        PieData data = new PieData(data_set);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    @Override
    public void onFilterUpdated(LogFilterFragment logFilterFragment, QueryBuilder query) {
        Log.d("AnalyticsActivity", "Received query " + query.getQuery());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        pieChart.setMinimumWidth((int) (displaymetrics.widthPixels * 0.7f));
        pieChart.setMinimumHeight((int) (displaymetrics.widthPixels * 0.7f));
        pieChart.invalidate();
    }
}
