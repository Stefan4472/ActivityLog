package com.stefankussmaul.activitylog.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.stefankussmaul.activitylog.content.ActivityAggregate;

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
        List<PieEntry> entries = new ArrayList<>(dataPoints.size());

        for (ActivityAggregate dp : dataPoints) {
            entries.add(new PieEntry(dp.getVal(), dp.getActivityName()));
        }
        return entries;
    }
}
