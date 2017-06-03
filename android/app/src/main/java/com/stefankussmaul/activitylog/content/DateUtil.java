package com.stefankussmaul.activitylog.content;

import com.stefankussmaul.activitylog.charts.ChartConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a few useful methods for working with Dates and Calendars
 */

public class DateUtil {

    // used to format dates
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy"); // todo: correct locale

    // takes the given calendar and sets all values below DAY_OF_MONTH to zero. Returns a Date object
    // with the Calendar's ms value to get the exact date the day started in ms
    public static Calendar getMidnightVal(Calendar calendar) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(calendar.getTime());
        // set the fields to get midnight of today
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        return calendar1;
    }

    // formats and returns the Date in a consistent manner
    public static String format(Date date) {
        return dateFormat.format(date);
    }

    // formats and returns the duration (given in ms) into hours and minutes
    public static String format(long ms) {
        String formatted = "";
        int hours = (int) ms / 3_600_000;
        formatted += hours + "h";
        int min = (int) ((ms % 3_600_000) / 60_000);
        formatted += min + "m";
        return formatted;
    }

    // converts given value in milliseconds to a float of hours
    public static float msToHours(long ms) {
        return (float) ms / 3_600_000;
    }

    // takes a date and a precision, which must be a Calendar field. Sets vals of any more-precise
    // fields to zero
    public static Date stripToPrecision(Date date, int precision) throws IllegalArgumentException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (precision) {
            case Calendar.SECOND:
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            case Calendar.MINUTE:
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                return cal.getTime();
            case Calendar.HOUR:
            case Calendar.HOUR_OF_DAY:
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                return cal.getTime();
            case Calendar.DAY_OF_WEEK:
            case Calendar.DAY_OF_MONTH:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
            case Calendar.DAY_OF_YEAR:
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                return cal.getTime();
            case Calendar.WEEK_OF_MONTH:
            case Calendar.WEEK_OF_YEAR:
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.DAY_OF_WEEK, 0);
                return cal.getTime();
            default:
                throw new IllegalArgumentException("Precision hasn't been accounted for");
        }
    }

    // generates a list of subsequent dates from minDate up to and including maxDate spaced out
    // by precisionMS milliseconds.
    public static List<Date> getIntervals(Date minDate, Date maxDate, long precisionMS) {
        List<Date> intervals = new ArrayList<>();
        long cur_ms = minDate.getTime();
        while (cur_ms < maxDate.getTime()) {
            intervals.add(new Date(cur_ms));
            cur_ms += precisionMS;
        }
        intervals.add(maxDate);
        return intervals;
    }

    // returns the millisecond interval for the given precision (a Calendar constant)
    public static long getMSInPrecision(int precision) {
        switch (precision) {
            case Calendar.MILLISECOND:
                return 1;
            case Calendar.SECOND:
                return 1_000;
            case Calendar.MINUTE:
                return 60_000;
            case Calendar.HOUR:
            case Calendar.HOUR_OF_DAY:
                return 3_600_000;
            case Calendar.DAY_OF_WEEK:
            case Calendar.DAY_OF_MONTH:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
            case Calendar.DAY_OF_YEAR:
                return 86_400_000;
            case Calendar.WEEK_OF_MONTH:
            case Calendar.WEEK_OF_YEAR:
                return 604_800_000;
            default:
                throw new IllegalArgumentException("Precision hasn't been accounted for");
        }
    }

    public static String datesToString(List<Date> dates) {
        String str = "";
        for (Date d : dates) {
            str += d.toString() + "\n";
        }
        return str;
    }
}
