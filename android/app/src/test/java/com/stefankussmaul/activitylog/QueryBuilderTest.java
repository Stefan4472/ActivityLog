package com.stefankussmaul.activitylog;

import com.stefankussmaul.activitylog.content.QueryBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_ACTIVITY;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_DURATION;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_COLUMN_TIMESTAMP;
import static com.stefankussmaul.activitylog.content.DBManager.LOG_TABLE_NAME;
import static org.junit.Assert.*;

/**
 * Tests QueryBuilder
 */

public class QueryBuilderTest {

    private QueryBuilder builder;
    // base of query
    private static final String BASE = "SELECT * FROM " + LOG_TABLE_NAME;

    @Before
    public void init() {
        builder = new QueryBuilder();
    }

    @Test
    public void testNoFilter() {
        assertEquals(BASE, builder.getQuery());
    }

    @Test
    public void testActivityFilter() {
        builder.setActivityFilter("Test");
        assertEquals(BASE + " WHERE " + LOG_COLUMN_ACTIVITY + " = 'Test'", builder.getQuery());

        builder.setActivityFilter("Test2");
        assertEquals(BASE + " WHERE " + LOG_COLUMN_ACTIVITY + " = 'Test2'", builder.getQuery());

        builder.resetActivityFilter();
        assertEquals(BASE, builder.getQuery());

        builder.resetActivityFilter();
        assertEquals(BASE, builder.getQuery());
    }

    @Test
    public void testDateFilter() {
        Date now = new Date(System.currentTimeMillis());
        Date now_2 = new Date(now.getTime() + 10_0000);

        builder.setDateBoundedMin(now);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_TIMESTAMP + " >= " + now.getTime(), builder.getQuery());

        builder.setDateBoundedMin(now_2);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_TIMESTAMP + " >= " + now_2.getTime(), builder.getQuery());

        builder.setDateBoundedMax(now);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_TIMESTAMP + " <= " + now.getTime(), builder.getQuery());

        builder.setDateBoundedMax(now_2);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_TIMESTAMP + " <= " + now_2.getTime(), builder.getQuery());

        builder.setDateBoundedBtwn(now, now);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_TIMESTAMP + " >= " + now.getTime() +
                " AND " + LOG_COLUMN_TIMESTAMP + " <= " + now.getTime(), builder.getQuery());

        builder.setDateBoundedBtwn(now, now_2);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_TIMESTAMP + " >= " + now.getTime() +
                " AND " + LOG_COLUMN_TIMESTAMP + " <= " + now_2.getTime(), builder.getQuery());

        builder.resetDateFilter();
        assertEquals(BASE, builder.getQuery());

        builder.resetDateFilter();
        assertEquals(BASE, builder.getQuery());
    }

    @Test
    public void testDurationFilter() {
        int d_1 = 1_000, d_2 = 10_001;

        builder.setDurationBoundedMin(d_1);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_DURATION + " >= " + d_1, builder.getQuery());

        builder.setDurationBoundedMin(d_2);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_DURATION + " >= " + d_2, builder.getQuery());

        builder.setDurationBoundedMax(d_1);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_DURATION + " <= " + d_1, builder.getQuery());

        builder.setDurationBoundedMax(d_2);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_DURATION + " <= " + d_2, builder.getQuery());

        builder.setDurationBoundedBtwn(d_1, d_1);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_DURATION + " >= " + d_1 +
                " AND " + LOG_COLUMN_DURATION + " <= " + d_1, builder.getQuery());

        builder.setDurationBoundedBtwn(d_1, d_2);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_DURATION + " >= " + d_1 +
                " AND " + LOG_COLUMN_DURATION + " <= " + d_2, builder.getQuery());

        builder.resetDurationFilter();
        assertEquals(BASE, builder.getQuery());

        builder.resetDurationFilter();
        assertEquals(BASE, builder.getQuery());
    }

    @Test
    public void testTwoFilters() {
        String activity = "Activity";
        Date time = new Date(System.currentTimeMillis());
        int duration = 100;

        builder.setActivityFilter(activity);
        builder.setDateBoundedMin(time);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_ACTIVITY + " = 'Activity' AND " +
            LOG_COLUMN_TIMESTAMP + " >= " + time.getTime(), builder.getQuery());

        builder.resetDateFilter();

        builder.setDurationBoundedMin(100);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_ACTIVITY + " = 'Activity' AND " +
            LOG_COLUMN_DURATION + " >= 100", builder.getQuery());

        builder.resetActivityFilter();
        builder.setDateBoundedMin(time);
        builder.setDurationBoundedMin(1000);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_TIMESTAMP + " >= " + time.getTime() + " AND " +
                LOG_COLUMN_DURATION + " >= 1000", builder.getQuery());
    }

    @Test
    public void testThreeFilters() {
        String activity = "Activity";
        Date time = new Date(System.currentTimeMillis());
        int duration = 100;

        builder.setActivityFilter(activity);
        builder.setDateBoundedMin(time);
        builder.setDurationBoundedMin(100);

        assertEquals(BASE + " WHERE " + LOG_COLUMN_ACTIVITY + " = 'Activity' AND " + LOG_COLUMN_TIMESTAMP +
            " >= " + time.getTime() + " AND " + LOG_COLUMN_DURATION + " >= 100", builder.getQuery());

        builder.setActivityFilter("Activity2");

        assertEquals(BASE + " WHERE " + LOG_COLUMN_ACTIVITY + " = 'Activity2' AND " + LOG_COLUMN_TIMESTAMP +
                " >= " + time.getTime() + " AND " + LOG_COLUMN_DURATION + " >= 100", builder.getQuery());

        time = new Date(System.currentTimeMillis() + 10_000);
        builder.setDateBoundedMin(time);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_ACTIVITY + " = 'Activity2' AND " + LOG_COLUMN_TIMESTAMP +
                " >= " + time.getTime() + " AND " + LOG_COLUMN_DURATION + " >= 100", builder.getQuery());

        builder.setDurationBoundedMin(200);
        assertEquals(BASE + " WHERE " + LOG_COLUMN_ACTIVITY + " = 'Activity2' AND " + LOG_COLUMN_TIMESTAMP +
                " >= " + time.getTime() + " AND " + LOG_COLUMN_DURATION + " >= 200", builder.getQuery());

        builder.resetActivityFilter();
        assertEquals(BASE + " WHERE " + LOG_COLUMN_TIMESTAMP +
                " >= " + time.getTime() + " AND " + LOG_COLUMN_DURATION + " >= 200", builder.getQuery());

        builder.resetDateFilter();
        assertEquals(BASE + " WHERE " + LOG_COLUMN_DURATION + " >= 200", builder.getQuery());

        builder.resetDurationFilter();
        assertEquals(BASE, builder.getQuery());
    }
}
