package com.stefankussmaul.activitylog.content;

import android.content.Context;
import android.util.Log;

import com.stefankussmaul.activitylog.R;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.stefankussmaul.activitylog.content.DBManager.AGGREGATE_KEYWORD;
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

    // name of activity being used for Activity Filter, if exists
    private String activityFilter = "";
    private Date minDate;
    private Date maxDate;

    // number of filters to be applied
    private int numClauses;

    public QueryBuilder() {

    }

    // creates and returns a deep copy of the given QueryBuilder
    public QueryBuilder(QueryBuilder toClone) {
        this();
        activityClause = toClone.activityClause;
        dateClause = toClone.dateClause;
        durationClause = toClone.durationClause;
        numClauses = toClone.numClauses;
    }

    public boolean hasActivityFilter() {
        return !activityClause.isEmpty();
    }

    public String getActivityFilter() {
        return activityFilter;
    }

    public void setActivityFilter(String activityName) {
        if (activityClause.isEmpty()) {
            numClauses++;
        }
        activityFilter = activityName;
        activityClause = LOG_COLUMN_ACTIVITY + " = '" + activityName + "'";
    }

    public void resetActivityFilter() {
        if (!activityClause.isEmpty()) {
            numClauses--;
            activityClause = "";
        }
    }

    public boolean hasDateFilter() {
        return !dateClause.isEmpty();
    }

    public boolean hasMinDate() {
        return !(minDate == null);
    }

    public boolean hasMaxDate() {
        return !(maxDate == null);
    }

    public void setDateFilter(Context context, String keyword, Date date1, Date date2)
            throws IllegalArgumentException {
        if (keyword.equals(context.getString(R.string.date_any))) {
            resetDateFilter();
        } else if (keyword.equals(context.getString(R.string.date_before))) {
            setDateBoundedMax(date1);
        } else if (keyword.equals(context.getString(R.string.date_after))) {
            setDateBoundedMin(date1);
        } else if (keyword.equals(context.getString(R.string.date_on))) {
            setDateOnDay(date1);
        } else if (keyword.equals(context.getString(R.string.date_between))) {
            setDateBoundedMinMax(date1, date2);
        } else {
            throw new IllegalArgumentException("Unrecognized Keyword '" + keyword + "'");
        }
    }

    // sets the dateClause to accept timeStamps only greater than/equal to the given Date's ms.
    // Cannot be combined with setDateBoundedMax! Instead use setDateBoundedMinMax(Date, Date)
    public void setDateBoundedMin(Date minDate) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        this.minDate = minDate;
        dateClause = LOG_COLUMN_TIMESTAMP + " >= " + minDate.getTime();
    }

    // sets the dateClause to accept timeStamps only less than/equal to the given maxDate's ms.
    // Cannot be combined with setDateBoundedMin! Instead use setDateBoundedMinMax(Date, Date)
    public void setDateBoundedMax(Date maxDate) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        this.maxDate = maxDate;
        dateClause = LOG_COLUMN_TIMESTAMP + " <= " + maxDate.getTime();
    }

    // sets the dateClause to accept timeStamps greater than/equal to the given minDate's ms and
    // less than/equal to the given maxDate's ms
    public void setDateBoundedMinMax(Date minDate, Date maxDate) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        this.minDate = minDate;
        this.maxDate = maxDate;
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

    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void resetDateFilter() {
        if (!dateClause.isEmpty()) {
            numClauses--;
            dateClause = "";
            minDate = null;
            maxDate = null;
        }
    }

    public boolean hasDurationFilter() {
        return !durationClause.isEmpty();
    }

    public void setDurationFilter(Context context, String keyword, int duration1, int duration2)
            throws IllegalArgumentException {
        if (keyword.equals(context.getString(R.string.duration_any))) {
            resetDurationFilter();
        } else if (keyword.equals(context.getString(R.string.duration_less))) {
            setDurationBoundedMax(duration1);
        } else if (keyword.equals(context.getString(R.string.duration_more))) {
            setDurationBoundedMin(duration1);
        } else if (keyword.equals(context.getString(R.string.duration_exactly))) {
            setDurationExactly(duration1);
        } else if (keyword.equals(context.getString(R.string.duration_between))) {
            setDurationBoundedMinMax(duration1, duration2);
        } else {
            throw new IllegalArgumentException("Unrecognized Keyword '" + keyword + "'");
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

    public void setDurationExactly(int val) {
        if (durationClause.isEmpty()) {
            numClauses++;
        }
        durationClause = LOG_COLUMN_DURATION + " = " + val;
    }

    public void resetDurationFilter() {
        if (!durationClause.isEmpty()) {
            numClauses--;
            durationClause = "";
        }
    }

    private String getWhereClause() {
        String clause = "";
        if (numClauses != 0) {
            // make a copy of numClauses
            int clauses_to_add = numClauses;
            clause += " WHERE ";
            if (!activityClause.isEmpty()) {
                clauses_to_add--;
                clause += activityClause;
                if (clauses_to_add > 0) {
                    clause += " AND ";
                }
            }
            if (clauses_to_add > 0 && !dateClause.isEmpty()) {
                clauses_to_add--;
                clause += dateClause;
                if (clauses_to_add > 0) {
                    clause += " AND ";
                }
            }
            if (clauses_to_add > 0 && !durationClause.isEmpty()) {
                clause += durationClause;
            }
        }
        return clause;
    }

    public String getQuery() {
        return "SELECT * FROM " + LOG_TABLE_NAME + getWhereClause() +
                " ORDER BY " + LOG_COLUMN_TIMESTAMP + " DESC";
    }

    public String getTimeSpentQuery() {
        return "SELECT " + LOG_COLUMN_ACTIVITY + ", SUM(" + LOG_COLUMN_DURATION
                + ") AS " + AGGREGATE_KEYWORD + " FROM " + LOG_TABLE_NAME + getWhereClause() +
                " GROUP BY (" + LOG_COLUMN_ACTIVITY + ") ORDER BY " + LOG_COLUMN_TIMESTAMP + " DESC";
    }

    public String getSessionCountQuery() {
        return "SELECT " + LOG_COLUMN_ACTIVITY + ", COUNT(" + LOG_COLUMN_ACTIVITY
                + ") AS " + AGGREGATE_KEYWORD + " FROM " + LOG_TABLE_NAME + getWhereClause() +
                " GROUP BY (" + LOG_COLUMN_ACTIVITY + ") ORDER BY " + LOG_COLUMN_TIMESTAMP + " DESC";
    }

    public String getXOldestQuery(int numOldest) {
        return "SELECT * FROM " + LOG_TABLE_NAME + getWhereClause() + " ORDER BY " +
                LOG_COLUMN_TIMESTAMP + " ASC LIMIT " + numOldest;
    }

    public String getXNewestQuery(int numNewest) {
        return "SELECT * FROM " + LOG_TABLE_NAME + getWhereClause() + " ORDER BY " +
                LOG_COLUMN_TIMESTAMP + " DESC LIMIT " + numNewest;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof QueryBuilder)) {
            return false;
        } else {
            return getQuery().equals(((QueryBuilder) o).getQuery());
        }
    }

    @Override
    public int hashCode() {
        return getQuery().hashCode();
    }
}
