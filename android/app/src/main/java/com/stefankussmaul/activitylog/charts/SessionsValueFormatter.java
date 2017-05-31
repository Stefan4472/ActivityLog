package com.stefankussmaul.activitylog.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * ValueFormatter to be used for formatting num session data. Converts the float to an integer and
 * adds the word "Session(s)" after
 */

public class SessionsValueFormatter implements IValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return Integer.toString((int) value) + " Session" + (value != 1f ? "s" : "");
    }
}
