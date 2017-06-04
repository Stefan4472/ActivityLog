package com.stefankussmaul.activitylog.content;

import android.content.Context;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.charts.ChartConfig;

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
        activityFilter = toClone.activityFilter;
        if (toClone.minDate != null) {
            minDate = new Date(toClone.minDate.getTime());
        }
        if (toClone.maxDate != null) {
            maxDate = new Date(toClone.maxDate.getTime());
        }
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
            setDateBoundedBtwn(date1, date2);
        } else {
            throw new IllegalArgumentException("Unrecognized Keyword '" + keyword + "'");
        }
    }

    // sets the dateClause to accept timeStamps only greater than/equal to the given Date's ms.
    // Cannot be combined with setDateBoundedMax! Instead use setDateBoundedBtwn(Date, Date)
    public void setDateBoundedMin(Date minDate) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        this.minDate = minDate;
        dateClause = LOG_COLUMN_TIMESTAMP + " >= " + minDate.getTime();
    }

    // sets the dateClause to accept timeStamps only less than/equal to the given maxDate's ms.
    // Cannot be combined with setDateBoundedMin! Instead use setDateBoundedBtwn(Date, Date)
    public void setDateBoundedMax(Date maxDate) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        this.maxDate = maxDate;
        dateClause = LOG_COLUMN_TIMESTAMP + " <= " + maxDate.getTime();
    }

    // sets the dateClause to accept timeStamps between the two given dates
    public void setDateBoundedBtwn(Date date1, Date date2) {
        if (dateClause.isEmpty()) {
            numClauses++;
        }
        if (date1.getTime() <= date2.getTime()) {
            this.minDate = date1;
            this.maxDate = date2;
        } else {
            this.minDate = date2;
            this.maxDate = date1;
        }

        dateClause = LOG_COLUMN_TIMESTAMP + " >= " + date1.getTime() + " AND " +
            LOG_COLUMN_TIMESTAMP + " <= " + date2.getTime();
    }

    // sets the dateClause to accept timeStamps that happened on the day given
    public void setDateOnDay(Date date) { // todo: needs to be tested! + clean up
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar = DateUtil.getMidnightVal(calendar);

        Date lower_bound = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        Date upper_bound = calendar.getTime();

        setDateBoundedBtwn(lower_bound, upper_bound);
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
            setDurationBoundedBtwn(duration1, duration2);
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

    public void setDurationBoundedBtwn(int duration1, int duration2) {
        int min, max;
        if (duration1 <= duration2) {
            min = duration1;
            max = duration2;
        } else {
            min = duration2;
            max = duration1;
        }
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

    // todo: use a subquery? we need to get values of zero even if where clause finds no entries
    public String getTimeSpentQuery() {
        return "SELECT " + LOG_COLUMN_ACTIVITY + ", TOTAL(" + LOG_COLUMN_DURATION
                + ") AS " + AGGREGATE_KEYWORD + " FROM " + LOG_TABLE_NAME + getWhereClause() +
                " GROUP BY (" + LOG_COLUMN_ACTIVITY + ") ORDER BY " + AGGREGATE_KEYWORD + " DESC";
    }

    public String getSessionCountQuery() {
        return "SELECT " + LOG_COLUMN_ACTIVITY + ", COUNT(" + LOG_COLUMN_ACTIVITY
                + ") AS " + AGGREGATE_KEYWORD + " FROM " + LOG_TABLE_NAME + getWhereClause() +
                " GROUP BY (" + LOG_COLUMN_ACTIVITY + ") ORDER BY " + AGGREGATE_KEYWORD + " DESC";
    }

    public String getActivityNamesQuery() {
        return "SELECT DISTINCT " + LOG_COLUMN_ACTIVITY + " FROM " + LOG_TABLE_NAME +
                getWhereClause() + " ORDER BY " + LOG_COLUMN_ACTIVITY + " DESC";
    }

    // given the chartBy type, returns the specific aggregate query (TimeSpent or SessionCount)
    public String getAggregateQuery(ChartConfig.ChartBy chartBy) {
        switch (chartBy) {
            case TIME_SPENT:
                return getTimeSpentQuery();
            case NUM_SESSIONS:
                return getSessionCountQuery();
            default:
                throw new IllegalArgumentException();
        }
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

    @Override
    public String toString() {
        return "Activity Filter: " + (hasActivityFilter() ? getActivityFilter() : "[NONE]") + ":" +
                "Date Filter: " + (hasMinDate() ? DateUtil.format(getMinDate()) : "[NONE]") + "/" +
                (hasMaxDate() ? DateUtil.format(getMaxDate()) : "[NONE]");
//                "Duration Filter: " + (hasDurationFilter() ? DateUtil.format(getDuration()) : "[NONE]");
    }
}
