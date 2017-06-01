package com.stefankussmaul.activitylog.charts;

import android.app.DownloadManager;
import android.content.Context;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.ActivityAggregate;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.util.ArrayList;
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

    // converts a list of ActivityAggregates into data that can be understood and plotted on a PieChart
    public static List<PieEntry> getPieChartEntries(List<ActivityAggregate> dataPoints) {
        List<PieEntry> entries = new LinkedList<>();

        for (ActivityAggregate dp : dataPoints) {
            entries.add(new PieEntry(dp.getVal(), dp.getActivityName()));
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
    // as a value in hours, rounded down todo: what if less than one?
    public static String getOverviewLabel(Context context, long numSessions, long timeSpent) {
        return numSessions + " " + context.getString(R.string.sessions) + " / " +
                (int) (timeSpent / 3_600_000) + " " + context.getString(R.string.hours);
    }
}
