package com.stefankussmaul.activitylog.content;

import java.util.Calendar;
import java.util.Date;

import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_ACTIVITY;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_DURATION;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_TIMESTAMP;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_TABLE_NAME;

/**
 * Builds database queries based on defined filters.
 */

public class DBQueryBuilder {

    // generated clauses for each possible filter
    private String activityClause;
    private String dateClause;
    private String durationClause;

    // number of filters to be applied
    private int numClauses;

    public DBQueryBuilder() {

    }

    public void setActivityFilter(String activityName) {
        if (activityClause.isEmpty()) {
            numClauses++;
        }
        activityClause = LOG_COLUMN_ACTIVITY + " = '" + activityName + "'";
    }

    public void resetActivityFilter() {
        if (!activityClause.isEmpty()) {
            numClauses--;
            activityClause = "";
        }
    }

    // sets the dateClause to accept timeStamps only greater than/equal to the given Date's ms.
    // Cannot be combined with setDateBoundedMax! Instead use setDateBoundedMinMax(Date, Date)
    public void setDateBoundedMin(Date minDate) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        dateClause = LOG_COLUMN_TIMESTAMP + " >= " + minDate.getTime();
    }

    // sets the dateClause to accept timeStamps only less than/equal to the given maxDate's ms.
    // Cannot be combined with setDateBoundedMin! Instead use setDateBoundedMinMax(Date, Date)
    public void setDateBoundedMax(Date maxDate) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        dateClause = LOG_COLUMN_TIMESTAMP + " <= " + maxDate.getTime();
    }

    // sets the dateClause to accept timeStamps greater than/equal to the given minDate's ms and
    // less than/equal to the given maxDate's ms
    public void setDateBoundedMinMax(Date minDate, Date maxDate) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        dateClause = LOG_COLUMN_TIMESTAMP + " >= " + minDate.getTime() + " AND " +
            LOG_COLUMN_TIMESTAMP + " <= " + maxDate.getTime();
    }

    // sets the dateClause to accept timeStamps that happened on the day given
    public void setDateOnDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // set the fields to get midnight of today
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date lower_bound = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        Date upper_bound = calendar.getTime();

        setDateBoundedMinMax(lower_bound, upper_bound);
    }

    public void resetDateFilter() {
        if (!dateClause.isEmpty()) {
            numClauses--;
            dateClause = "";
        }
    }

    public void setDurationBoundedMin(int min) {
        if (durationClause.isEmpty()) {
            numClauses++;
        }
        durationClause = LOG_COLUMN_DURATION + " >= " + min;
    }

    public void setDurationBoundedMax(int max) {
        if (durationClause.isEmpty()) {
            numClauses++;
        }
        durationClause = LOG_COLUMN_DURATION + " <= " + max;
    }

    public void setDurationBoundedMinMax(int min, int max) {
        if (durationClause.isEmpty()) {
            numClauses++;
        }
        durationClause = LOG_COLUMN_DURATION + " >= " + min + " AND " + LOG_COLUMN_DURATION +
                " <= " + max;
    }

    public void resetDurationFilter() {
        if (!durationClause.isEmpty()) {
            numClauses--;
            durationClause = "";
        }
    }

    public String generateQuery() {
        String query = "SELECT * FROM " + LOG_TABLE_NAME;

        if (numClauses != 0) {
            query += " WHERE ";
            if (!activityClause.isEmpty()) {
                numClauses--;
                query += activityClause;
                if (numClauses > 0) {
                    query += " AND ";
                }
            }
            if (numClauses > 0 && !dateClause.isEmpty()) {
                numClauses--;
                query += dateClause;
                if (numClauses > 0) {
                    query += " AND ";
                }
            }
            if (numClauses > 0 && !durationClause.isEmpty()) {
                query += durationClause;
            }
        }
        return query;
    }
}
