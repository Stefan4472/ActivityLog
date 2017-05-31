package com.stefankussmaul.activitylog.content;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}
