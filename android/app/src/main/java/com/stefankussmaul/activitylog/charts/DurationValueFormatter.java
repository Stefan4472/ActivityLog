package com.stefankussmaul.activitylog.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Formats the given value, which is in hours, into the format __h__m
 */

public class DurationValueFormatter implements IValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // todo: internationalize? double check
        return ((int) value) + "h" + ((int) (value - (int) value) * 60) + "m";
    }
}
