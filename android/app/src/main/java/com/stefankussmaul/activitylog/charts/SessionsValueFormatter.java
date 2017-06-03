package com.stefankussmaul.activitylog.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Formats the given sessions value (a float) to an int followed by the word "Session(s)"
 */

public class SessionsValueFormatter implements IValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return ((int) value) + " Session" + (value == 1f ? "" : "s");
    }
}
