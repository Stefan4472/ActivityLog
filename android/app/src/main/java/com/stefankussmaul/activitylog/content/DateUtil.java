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

    // millisecond values
    public static final int SECOND_MS = 1_000;
    public static final int MINUTE_MS = 60_000;
    public static final int HOUR_MS = 3_600_000;
    public static final int DAY_MS = 84_400_000;
    public static final int WEEK_MS = 604_800_000;

    public static long timeToMs(int hours, int minutes, int seconds) {
        return hours * HOUR_MS + minutes * MINUTE_MS + seconds * SECOND_MS;
    }

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

    public static Date getMidnightToday() {
        return stripToPrecision(new Date(System.currentTimeMillis()), Calendar.DAY_OF_YEAR);
    }

    public static Date getMidnightTomorrow() {
        Date today = stripToPrecision(new Date(System.currentTimeMillis()), Calendar.DAY_OF_YEAR);
        return new Date(today.getTime() + DAY_MS);
    }

    // formats and returns the Date in a consistent manner
    public static String format(Date date) {
        return dateFormat.format(date);
    }

    // formats and returns the duration (given in ms) into hours and minutes e.g. 5h43m
    public static String format(long ms) {
        String formatted = "";
        int hours = (int) ms / HOUR_MS;
        formatted += hours + "h";
        int min = (int) ((ms % HOUR_MS) / MINUTE_MS);
        formatted += min + "m";
        return formatted;
    }

    // converts given value in milliseconds to a float of hours
    public static float msToHours(long ms) {
        return (float) ms / HOUR_MS;
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

    // returns a new Date that is the given date plus num * the given field value (which must be
    // a Calendar constant)
    public static Date addToDate(Date date, int num, int field) {
        return new Date(date.getTime() + num * getMSInPrecision(field));
    }

    // returns the millisecond interval for the given precision (a Calendar constant)
    public static long getMSInPrecision(int precision) {
        switch (precision) {
            case Calendar.MILLISECOND:
                return 1;
            case Calendar.SECOND:
                return SECOND_MS;
            case Calendar.MINUTE:
                return MINUTE_MS;
            case Calendar.HOUR:
            case Calendar.HOUR_OF_DAY:
                return HOUR_MS;
            case Calendar.DAY_OF_WEEK:
            case Calendar.DAY_OF_MONTH:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
            case Calendar.DAY_OF_YEAR:
                return DAY_MS;
            case Calendar.WEEK_OF_MONTH:
            case Calendar.WEEK_OF_YEAR:
                return WEEK_MS;
            default:
                throw new IllegalArgumentException("Precision hasn't been accounted for");
        }
    }
}
