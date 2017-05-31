package com.stefankussmaul.activitylog.content;

import android.content.Context;
import android.util.Log;

import com.stefankussmaul.activitylog.R;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_ACTIVITY;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_DURATION;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_TIMESTAMP;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_TABLE_NAME;

/**
 * Builds database queries based on defined filters.
 */

public class QueryBuilder {

//    public static final String DATE_ANY = "ANY";
//    public static final String DATE_ON = "ON";
//    public static final String DATE_BEFORE = "BEFORE";
//    public static final String DATE_AFTER = "AFTER";
//    public static final String DATE_BETWEEN = "BETWEEN";

    // generated clauses for each possible filter
    private String activityClause = "";
    private String dateClause = "";
    private String durationClause = "";

    // number of filters to be applied
    private int numClauses;

    public QueryBuilder() {

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

    public void setDateFilter(String keyword, Date date1, Date date2) {

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
    public void setDateOnDay(Date date) { // todo: needs to be tested! + clean up
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar = DateUtil.getMidnightVal(calendar);

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

    public void setDurationFilter(String keyword, int duration1, int duration2) {

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

    public String getQuery() {
        String query = "SELECT * FROM " + LOG_TABLE_NAME;

        if (numClauses != 0) {
            // make a copy of numClauses
            int clauses_to_add = numClauses;
            query += " WHERE ";
            if (!activityClause.isEmpty()) {
                clauses_to_add--;
                query += activityClause;
                if (clauses_to_add > 0) {
                    query += " AND ";
                }
            }
            if (clauses_to_add > 0 && !dateClause.isEmpty()) {
                clauses_to_add--;
                query += dateClause;
                if (clauses_to_add > 0) {
                    query += " AND ";
                }
            }
            if (clauses_to_add > 0 && !durationClause.isEmpty()) {
                query += durationClause;
            }
        }
        return query;
    }

    // returns list of keywords for configuring date filter. Loaded from R.string for consistency
    // across different languages
    public static List<String> getDateKeyWords(Context context) {
        List<String> key_words = new LinkedList<>();
        key_words.add(context.getString(R.string.date_any));
        key_words.add(context.getString(R.string.date_on));
        key_words.add(context.getString(R.string.date_before));
        key_words.add(context.getString(R.string.date_after));
        key_words.add(context.getString(R.string.date_between));
        return key_words;
    }

    // returns list of keywords for configuring duration filter. Loaded from R.string for consistency
    // across different languages
    public static List<String> getDurationKeyWords(Context context) {
        List<String> key_words = new LinkedList<>();
        key_words.add(context.getString(R.string.duration_any));
        key_words.add(context.getString(R.string.duration_exactly));
        key_words.add(context.getString(R.string.duration_less));
        key_words.add(context.getString(R.string.duration_more));
        key_words.add(context.getString(R.string.duration_between));
        return key_words;
    }
}
