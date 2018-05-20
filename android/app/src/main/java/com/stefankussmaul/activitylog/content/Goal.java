package com.stefankussmaul.activitylog.content;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.Date;

/**
 * A Goal created by the user. Can track the number of times a user logs an activity over a given
 * period of time period, or the amount of time the user does logged activities over a period.
 * Meant to run over the course of a day, week, or month.
 * An SQL query is dynamically generated to measure progress. This is compared to the target
 * parameter. So: running the query will result in a measure, which can be compared to the target.
 * If it equals the target, then the Goal has been satisfied.
 */

public class Goal {

    // name of activity being targeted
    private String activity;
    // date target starts running and ends running
    private Date startDate, endDate;
    // SQL query used to find relevant activities and measure progress
    private String query;
    // numerical progress sought
    private int target;
    // current progress. This is not a percentage!
    private float progress;
    // user-created note to go along with Goal
    private String note;

    public enum GoalType {
        GOAL_TIME, GOAL_REPETITIONS;
    }

    public Goal(String activity, Date startDate, Date endDate, GoalType goalType, int target, String note) {
        this.activity = activity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.target = target;
        this.query = generateQuery(activity, startDate, endDate, goalType);
        this.note = note;
    }

    public Goal(String activity, Date startDate, Date endDate, int target, String query, String note) {
        this.activity = activity;  // todo: need goal type
        this.startDate = startDate;
        this.endDate = endDate;
        this.target = target;
        this.query = query;
        this.note = note;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public float getProgress() {
        return progress;
    }

    public float getPercentProgress() {
        return progress > target ? 100 : progress / target * 100.0f;
    }

    // runs query and sets progress to current actual progress
    public void updateProgress() {
        Cursor cursor = DBManager.runQuery(query);
        Log.d("Goal", DatabaseUtils.dumpCursorToString(cursor));
        cursor.moveToFirst();
//        progress = cursor.getInt(cursor.getColumnIndex(DBManager.GOAL_PROGRESS));
        progress = cursor.getInt(0);  // TODO: WHY ISN'T THIS STORED UNDER GOAL_PROGRESS?
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    // returns percentage of target that has been completed
    public float getPercentCompletion() {
        return 0; // TODO
    }

    // returns formatted target, e.g. "10 hours" or "15 Times"
    public String getTargetString() {
        return "No Target";
    }

    // generates query that assesses value of progress toward target
    public static String generateQuery(String activity, Date startDate, Date endDate,
                                GoalType goalType) {
        String where_clause = " WHERE " + DBManager.LOG_COLUMN_ACTIVITY +
                " = '" + activity + "' AND " + DBManager.LOG_COLUMN_TIMESTAMP + " > " +
                startDate.getTime() + " AND " + DBManager.LOG_COLUMN_TIMESTAMP + " < " +
                endDate.getTime();

        if (goalType == GoalType.GOAL_TIME) {
            return "SELECT SUM(" + DBManager.LOG_COLUMN_DURATION + ") FROM " +
                    DBManager.LOG_TABLE_NAME + " AS " + DBManager.GOAL_PROGRESS + where_clause;
        } else if (goalType == GoalType.GOAL_REPETITIONS) {
            return "SELECT COUNT(*) FROM " + DBManager.LOG_TABLE_NAME + " AS " +
                    DBManager.GOAL_PROGRESS + where_clause;
        } else {
            throw new IllegalArgumentException("goalType parameter must be of enum GoalType");
        }
    }

    @Override
    public String toString() {
        return "Goal(" + activity + ", " + DateUtil.format(startDate) + ", " + DateUtil.format(endDate)
                + ", " + query + ", " + target + "ms, note:'" + note + "')";
    }
}
