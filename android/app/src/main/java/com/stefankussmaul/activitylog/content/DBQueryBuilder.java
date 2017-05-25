package com.stefankussmaul.activitylog.content;

import java.util.Date;

import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_ACTIVITY;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_DURATION;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_TIMESTAMP;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_TABLE_NAME;

/**
 * Builds database queries based on defined filters.
 */

public class DBQueryBuilder {

    // available filters
    private String activityName;
    private long startDate, endDate;
    private int durationLow, durationHigh;

    public DBQueryBuilder() {

    }

    public void setNameFilter(String activityName) {
        this.activityName = activityName;
    }

    public void setDateFilter(Date startDate, Date endDate) {
        this.startDate = startDate.getTime();
        this.endDate = endDate.getTime();
    }

    public void setDurationFilter(int min, int max) {
        durationLow = min;
        durationHigh = max;
    }

    public String generateQuery() {
        String query = "SELECT * FROM " + LOG_TABLE_NAME;
        int clauses = 0;
        String activity_clause = "";
        if (!activityName.isEmpty()) {
            clauses++;
            activity_clause = LOG_COLUMN_ACTIVITY + " = " + activityName;
        }
        String date_clause = "";
        if (startDate != 0 && endDate != 0) {
            clauses++;
            date_clause = LOG_COLUMN_TIMESTAMP + " >= " + startDate + " AND " +
                    LOG_COLUMN_TIMESTAMP + " <= " + endDate;
        }
        String duration_clause = "";
        if (durationLow != 0 && durationHigh != 0) {
            clauses++;
            duration_clause = LOG_COLUMN_DURATION + " >= " + durationLow + " AND " +
                    LOG_COLUMN_DURATION + " <= " + durationHigh;
        }
        if (clauses != 0) {
            query += " WHERE ";
            if (!activity_clause.isEmpty()) {
                clauses--;
                query += activity_clause;
                if (clauses > 0) {
                    query += " AND ";
                }
            }
            if (clauses > 0 && !date_clause.isEmpty()) {
                clauses--;
                query += date_clause;
                if (clauses > 0) {
                    query += " AND ";
                }
            }
            if (clauses > 0 && !duration_clause.isEmpty()) {
                query += duration_clause;
            }
        }
        return query;
    }
}
