package com.stefankussmaul.activitylog.content;

import java.util.Date;

/**
 * A Goal created by the user. Can track the number of times a user logs an activity over a given
 * period of time period, or the amount of time the user does logged activities over a period.
 * Meant to run over the course of a day, week, or month.
 * An SQL query is dynamically generated to measure progress. This is compared to the goal
 * parameter. So: running the query will result in a measure, which can be compared to the goal.
 * If it equals the goal, then the Goal has been satisfied.
 */

public class Goal {

    // date goal starts running and ends running
    private Date startDate, endDate;
    // SQL query used to find relevant activities and measure progress
    private String query;
    // numerical progress sought
    private int goal;
    // user-created note to go along with Goal
    private String note;

    public enum GoalType {
        GOAL_TIME, GOAL_REPETITIONS;
    }

    public Goal(String activity, Date startDate, Date endDate, GoalType goalType, int goal, String note) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.note = note;
        this.query = generateQuery(activity, startDate, endDate, goalType);
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

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // returns percentage of goal that has been completed
    public float getPercentCompletion() {
        return 0; // TODO
    }

    // generates query that assesses value of progress toward goal
    public static String generateQuery(String activity, Date startDate, Date endDate,
                                GoalType goalType) {
        String where_clause = " WHERE " + DBManager.LOG_COLUMN_ACTIVITY +
                " = '" + activity + "' AND " + DBManager.LOG_COLUMN_TIMESTAMP + " > " +
                startDate.getTime() + " AND " + DBManager.LOG_COLUMN_TIMESTAMP + " < " +
                endDate.getTime();

        if (goalType == GoalType.GOAL_TIME) {
            return "SELECT SUM(" + DBManager.LOG_COLUMN_DURATION + ") FROM " +
                    DBManager.LOG_TABLE_NAME + where_clause;
        } else if (goalType == GoalType.GOAL_REPETITIONS) {
            return "SELECT COUNT(*) FROM " + DBManager.LOG_TABLE_NAME + where_clause;
        } else {
            throw new IllegalArgumentException("goalType parameter must be of enum GoalType");
        }
    }

    @Override
    public String toString() {
        return "Goal(" + DateUtil.format(startDate) + ", " + DateUtil.format(endDate) + ", " +
                query + ", " + goal + ", " + note + "ms)";
    }
}
