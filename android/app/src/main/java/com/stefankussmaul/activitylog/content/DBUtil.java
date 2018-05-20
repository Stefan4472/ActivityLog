package com.stefankussmaul.activitylog.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.ContactsContract;
import android.util.Log;

import com.stefankussmaul.activitylog.charts.ChartConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.stefankussmaul.activitylog.content.DBManager.GOAL_ACTIVITY;
import static com.stefankussmaul.activitylog.content.DBManager.GOAL_END_TIME;
import static com.stefankussmaul.activitylog.content.DBManager.GOAL_NOTE;
import static com.stefankussmaul.activitylog.content.DBManager.GOAL_QUERY;
import static com.stefankussmaul.activitylog.content.DBManager.GOAL_START_TIME;
import static com.stefankussmaul.activitylog.content.DBManager.GOAL_TARGET;
import static com.stefankussmaul.activitylog.content.DBManager.GOAL_TYPE;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_ACTIVITY;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_DURATION;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_TIMESTAMP;

/**
 * Some utility methods for DBManager or other classes.
 */

public class DBUtil {

    // takes a cursor and extracts all the data from it, converting it into a list of LogEntries.
    //  Closes the Cursor! // todo: should it leave cursor open?
    public static List<LogEntry> getLogsFromCursor(Cursor cursor) {
        List<LogEntry> entries = new LinkedList<>();
        cursor.moveToFirst();
        // loop through data in the cursor, creating a new LogEntry for each set
        while (!cursor.isAfterLast()) {
            entries.add(
                    new LogEntry(
                            cursor.getString(cursor.getColumnIndex(LOG_COLUMN_ACTIVITY)),
                            cursor.getLong(cursor.getColumnIndex(LOG_COLUMN_TIMESTAMP)),
                            cursor.getInt(cursor.getColumnIndex(LOG_COLUMN_DURATION))
                    ));
            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    // given a cursor attempts to read off the AGGREGATE column and build a list of ActivityAggregates.

    public static List<ActivityAggregate> getAggregatesFromCursor(Cursor cursor) {
        List<ActivityAggregate> aggregates = new LinkedList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            aggregates.add(new ActivityAggregate(cursor.getString(cursor.getColumnIndex(LOG_COLUMN_ACTIVITY)),
                    cursor.getInt(cursor.getColumnIndex(DBManager.AGGREGATE_KEYWORD))));
            cursor.moveToNext();
        }
        cursor.close();
        if (aggregates.isEmpty()) {
            Log.d("DbUtil", "Empty");
            aggregates.add(new ActivityAggregate("", 0)); // todo: need to know activity name!!!
        }
        return aggregates;
    }

    public static List<Goal> getGoalsFromCursor(Cursor cursor) {
        List<Goal> goals = new LinkedList<>();
        cursor.moveToFirst();
        Log.d("DBUTilt", DatabaseUtils.dumpCursorToString(cursor));
        // loop through data in the cursor, creating a new Goal for each set
        while (!cursor.isAfterLast()) {
            goals.add(
                    new Goal(
                            cursor.getString(cursor.getColumnIndex(GOAL_ACTIVITY)),
                            new Date(cursor.getLong(cursor.getColumnIndex(GOAL_START_TIME))),
                            new Date(cursor.getLong(cursor.getColumnIndex(GOAL_END_TIME))),
                            cursor.getInt(cursor.getColumnIndex(GOAL_TARGET)),
                            Goal.GoalType.valueOf(cursor.getString(cursor.getColumnIndex(GOAL_TYPE))),
                            cursor.getString(cursor.getColumnIndex(GOAL_QUERY)),
                            cursor.getString(cursor.getColumnIndex(GOAL_NOTE))
                    ));
            cursor.moveToNext();
        }
        cursor.close();
        return goals;
    }

    // sums the values of each ActivityAggregate in the list and returns this sum
    public static long getTotalOfAggregates(List<ActivityAggregate> aggregates) {
        long total = 0;
        for (ActivityAggregate a : aggregates) {
            total += a.getVal();
        }
        return total;
    }

    // takes a LogEntry and correctly stores its data in a ContentValues object
    public static ContentValues getContentVals(LogEntry log) {
        ContentValues content_vals = new ContentValues();
        content_vals.put(LOG_COLUMN_ACTIVITY, log.getActivityName());
        content_vals.put(LOG_COLUMN_DURATION, log.getDuration());
        content_vals.put(LOG_COLUMN_TIMESTAMP, log.getDateInMS());
        return content_vals;
    }

    // takes a Goal and stores its data in a ContentValues object, which can be inserted
    // into a database
    public static ContentValues getContentVals(Goal goal) {
        ContentValues content_vals = new ContentValues();
        content_vals.put(GOAL_ACTIVITY, goal.getActivity());
        content_vals.put(GOAL_START_TIME, goal.getStartDate().getTime());
        content_vals.put(GOAL_END_TIME, goal.getEndDate().getTime());
        content_vals.put(GOAL_TARGET, goal.getTarget());
        content_vals.put(GOAL_TYPE, goal.getGoalType().name());
        content_vals.put(GOAL_QUERY, goal.getQuery());
        content_vals.put(GOAL_NOTE, goal.getNote());
        return content_vals;
    }

    // given an original QueryBuilder and a list of dates in order, creates and returns a list of
    // QueryBuilders whose minMaxDates span the intervals.
    public static List<QueryBuilder> getQueriesOverInterval(QueryBuilder origQuery, List<Date> intervals) {
        List<QueryBuilder> gen_queries = new ArrayList<>();
        QueryBuilder copy = new QueryBuilder(origQuery);
        for (int i = 0; i < intervals.size() - 1; i++) {
            copy.setDateBoundedBtwn(intervals.get(i), intervals.get(i + 1));
            gen_queries.add(new QueryBuilder(copy));
        }
        return gen_queries;
    }

    public static List<List<ActivityAggregate>> runQueries(DBManager db, List<QueryBuilder> queries,
                                                           ChartConfig.ChartBy chartBy) {
        List<List<ActivityAggregate>> results = new ArrayList<>();
        for (QueryBuilder q : queries) {
            if (chartBy == ChartConfig.ChartBy.NUM_SESSIONS) {
                results.add(DBUtil.getAggregatesFromCursor(db.runQuery(q.getSessionCountQuery())));
            } else if (chartBy == ChartConfig.ChartBy.TIME_SPENT) {
                results.add(DBUtil.getAggregatesFromCursor(db.runQuery(q.getTimeSpentQuery())));
            } else {
                throw new IllegalArgumentException("ChartBy not recognized");
            }
        }
        return results;
    }
}
