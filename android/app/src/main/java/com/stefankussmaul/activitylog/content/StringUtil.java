package com.stefankussmaul.activitylog.content;

import android.database.Cursor;

import java.util.List;

/**
 * Usually toString utility methods
 */

public class StringUtil {

    public static String aggregatesToString(List<ActivityAggregate> aggregates) {
        String to_str = "";
        int i = 0;
        for (ActivityAggregate a : aggregates) {
            i++;
            to_str += i + ". " + a.toString() + "\n";
        }
        return to_str;
    }

    // toString for a list of LogEntry objects
    public static String logListToString(List<LogEntry> logs) {
        String to_str = "";
        for (LogEntry log : logs) {
            to_str += log.toString() + "\n";
        }
        return to_str;
    }

    public static String cursorToString(Cursor cursor) {
        return logListToString(DBUtil.getLogsFromCursor(cursor));
    }
}
