package com.stefankussmaul.activitylog.charts;

import android.content.Context;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.ActivityAggregate;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility classes for getting data into graphable form for use with MPAndroidChart library
 */

public class ChartUtil {

    // takes a list of ActivityAggregates and truncates to the given maxSize by combining the
    // values of all Aggregates past maxSize into one Aggregate. Sets this Aggregate's activityName
    // to otherLable (e.g., "other")
    public static void truncateAggregateList(List<ActivityAggregate> aggregates, int maxSize, String otherLabel) {
        if (aggregates.size() > maxSize) {
            ActivityAggregate sum_extra_vals = new ActivityAggregate(otherLabel, 0);
            while (aggregates.size() > maxSize) {
                aggregates.get(maxSize - 1).addToVal(aggregates.get(maxSize).getVal());
                aggregates.remove(maxSize);
            }
            aggregates.get(maxSize - 1).setName(otherLabel);
        }
    }

    // converts a list of ActivityAggregates into data that can be understood and plotted on a PieChart.
    // If ChartBy == TIME_SPENT, sets the value to the number of hours since Epoch
    public static List<PieEntry> getPieChartEntries(List<ActivityAggregate> dataPoints,
                                                    ChartConfig.ChartBy chartBy) {
        List<PieEntry> entries = new LinkedList<>();

        for (ActivityAggregate dp : dataPoints) {
            if (chartBy == ChartConfig.ChartBy.TIME_SPENT) {
                entries.add(new PieEntry((float) dp.getVal() / 3_600_000, dp.getActivityName()));
            } else if (chartBy == ChartConfig.ChartBy.NUM_SESSIONS){
                entries.add(new PieEntry(dp.getVal(), dp.getActivityName()));
            } else {
                throw new IllegalArgumentException();
            }
        }
        return entries;
    }

    // generates a chart label for the chart given the query charted. Requires context to access
    // R.string. Such a label will be: "All Sessions All Time" "All Sessions In Filter"
    // "[Activity Name] All Time" or "[Activity Name] In Filter", depending on the given Query
    public static String getChartLabel(Context context, QueryBuilder chartedQuery) {
        String title = context.getString(R.string.showing) + " ";
        title += chartedQuery.hasActivityFilter() ?
                chartedQuery.getActivityFilter() : context.getString(R.string.all_sessions);
        title += " ";
        title += (chartedQuery.hasDateFilter() || chartedQuery.hasDurationFilter()) ?
                context.getString(R.string.in_filter) : context.getString(R.string.all_time);
        return title;
    }

    // generates a String in the form "[numSessions] Sessions / [timeSpent] Hours"
    // uses Context to get the Sessions and Hours Strings in proper language. Displays timeSpent
    // as a value in hours, with one decimal place
    public static String getOverviewLabel(Context context, long numSessions, long timeSpent) {
        return numSessions + " " + context.getString(R.string.sessions) + " / " +
                (int) (timeSpent / 3_600_000) + "." + (int) (timeSpent / 36_000_000) + " " +
                context.getString(R.string.hours);
    }

    // takes a list of ActivityAggregates and enters them into a LineDataSet with their corresponding
    // dates (given by the dates list, which must have at least as many elements). So, each aggregate
    // will be paired with its corresponding date in the Dates list. Dates are converted to hours.
    // If ChartBy = TIME_SPENT, convert the millisecond aggregates to hours (float)
    public static LineDataSet aggsToLineData(List<ActivityAggregate> aggregates,
                                             List<Date> dates,
                                             ChartConfig.ChartBy chartBy) throws IllegalArgumentException {
        Log.d("ChartUtil", "a" + aggregates.size() + " d" + dates.size());
//        if (aggregates.size() != dates.size()) {
//            throw new IllegalArgumentException("Aggregates and Dates must be equal sizes");
//        }
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < aggregates.size(); i++) {
            if (chartBy == ChartConfig.ChartBy.TIME_SPENT) { // convert to hours
                entries.add(new Entry((float) dates.get(i).getTime() / 3_600_000,
                        (float) aggregates.get(i).getVal() / 3_600_000));
            } else {
                entries.add(new Entry((float) dates.get(i).getTime() / 3_600_000,
                        aggregates.get(i).getVal()));
            }
        }
        return new LineDataSet(entries, aggregates.get(0).getActivityName());
    }
}
