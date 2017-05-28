package com.stefankussmaul.activitylog.content;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides a few useful methods for working with Dates and Calendars
 */

public class DateUtil {

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
}
