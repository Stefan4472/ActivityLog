package com.stefankussmaul.activitylog.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_ACTIVITY;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_DURATION;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_TIMESTAMP;

/**
 * Some utility methods for DBManager or other classes.
 */

public class DBUtil {

    // takes a cursor and extracts all the data from it, converting it into a list of LogEntries.
    //  does not close the Cursor!
    public static List<LogEntry> getLogsFromCursor(Cursor cursor) {
        List<LogEntry> entries = new LinkedList<>();
        cursor.moveToFirst();
        // loop through data in the cursor, creating a new LogEntry for each set
        while (!cursor.isAfterLast()) {
            entries.add(
                    new LogEntry(
                            cursor.getString(cursor.getColumnIndex(LOG_COLUMN_ACTIVITY)),
                            cursor.getInt(cursor.getColumnIndex(LOG_COLUMN_TIMESTAMP)),
                            cursor.getInt(cursor.getColumnIndex(LOG_COLUMN_DURATION))
                    ));
            cursor.moveToNext();
        }
        return entries;
    }

    // takes a LogEntry and correctly stores its data in a ContentValues object
    public static ContentValues getContentVals(LogEntry log) {
        ContentValues content_vals = new ContentValues();
        content_vals.put(LOG_COLUMN_ACTIVITY, log.getActivityName());
        content_vals.put(LOG_COLUMN_DURATION, log.getDuration());
        content_vals.put(LOG_COLUMN_TIMESTAMP, log.getTimeStamp());
        return content_vals;
    }
}
