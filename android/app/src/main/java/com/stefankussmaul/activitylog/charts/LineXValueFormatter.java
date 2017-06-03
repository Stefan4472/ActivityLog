package com.stefankussmaul.activitylog.charts;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ValueFormatter to be used for formatting the x-axis of the line graph. Given a float equal to hours
 * since Epoch, converts this to a Date object and puts it in "DD/MM/YY" format
 */

public class LineXValueFormatter implements IAxisValueFormatter {

    private static final DateFormat format = SimpleDateFormat.getDateInstance();

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return format.format(new Date((long) (value * 3_600_000)));
    }
}
