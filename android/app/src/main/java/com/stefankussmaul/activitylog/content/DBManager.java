package com.stefankussmaul.activitylog.content;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class to provide access to the database.
 */

public class DBManager extends SQLiteOpenHelper { // todo: testing

    private static final String DB_NAME = "ActivityLog.db";
    public static final String LOG_TABLE_NAME = "LogTable";
    // unique id of log entry
    public static final String LOG_COLUMN_ID = "_id";
    // activity name, stored as a String (todo: use Activity id and another table)
    public static final String LOG_COLUMN_ACTIVITY = "activity";
    // duration of activity, stored as number of milliseconds
    public static final String LOG_COLUMN_DURATION = "duration";
    // timestamp of activity, stored as number of milliseconds
    public static final String LOG_COLUMN_TIMESTAMP = "timestamp";
    // keyword used for aliasing sums, counts, etc.
    public static final String AGGREGATE_KEYWORD = "Aggregate";
    // keyword used for aliasing min values
    public static final String MIN_KEYWORD = "MinVal";

    // columns of Goals table. This is where goals that exist/have existed are stored.
    // They can be generated from the Scheduled Goals table
    public static final String GOALS_TABLE_NAME = "goals";
    public static final String GOAL_ID = "goals_id";
//    public static final String GOAL_ACTIVITY = "goals_activity";
    public static final String GOAL_START_TIME = "goals_start_time";
    public static final String GOAL_END_TIME = "goals_end_time";
    public static final String GOAL_QUERY = "goals_query";
    public static final String GOAL_NOTE = "goals_note";

    // columns of Scheduled Goals table. This is where recurring goals are stored
    public static final String SCHEDGOALS_TABLE = "scheduled_goals";
    public static final String SCHEDGOAL_QUERY = "scheduled_goals.query";
    public static final String SCHEDGOAL_RECUR_MON = "scheduled_goals.recur_mon";
    public static final String SCHEDGOAL_RECUR_TUE = "scheduled_goals.recur_tue";
    public static final String SCHEDGOAL_RECUR_WED = "scheduled_goals.recur_wed";
    public static final String SCHEDGOAL_RECUR_THU = "scheduled_goals.recur_thu";
    public static final String SCHEDGOAL_RECUR_FRI = "scheduled_goals.recur_fri";
    public static final String SCHEDGOAL_RECUR_SAT = "scheduled_goals.recur_sat";
    public static final String SCHEDGOAL_RECUR_SUN = "scheduled_goals.recur_sun";
    public static final String SCHEDGOAL_RECUR_WEEKLY = "scheduled_goals.recur_weekly";
    public static final String SCHEDGOAL_RECUR_MONTHLY = "scheduled_goals.recur_monthly";
    public static final String SCHEDGOAL_NOTE = "scheduled_goals.note";

    // handle to the database
    private static SQLiteOpenHelper dbHandle;

    // takes copy of context and initializes the DBManager Singleton. REQUIRED!
    public static void init(Context context) {
        if (dbHandle == null) {
            dbHandle = new DBManager(context);
        }
    }

    private DBManager(Context context){
        super(context, DB_NAME, null, 1);
        // uncomment to wipe the database
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + GOALS_TABLE_NAME);
        onCreate(getWritableDatabase());
    }

    @Override // run SQL command to create the database with the table and fields
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " + LOG_TABLE_NAME + " (" +
                    LOG_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    LOG_COLUMN_ACTIVITY + " INTEGER, " +
                    LOG_COLUMN_DURATION + " INTEGER, " +
                    LOG_COLUMN_TIMESTAMP + " INTEGER)");
        database.execSQL(
                "CREATE TABLE " + GOALS_TABLE_NAME + " (" +
                    GOAL_ID + " INTEGER PRIMARY KEY, " +
                    GOAL_START_TIME + " INTEGER, " +
                    GOAL_END_TIME + " INTEGER, " +
                    GOAL_QUERY + " TEXT, " +
                    GOAL_NOTE + " TEXT)");
        Log.d("DBManager", "Created Database");
    }

    @Override // drop the table and re-create database if needs to be ugraded
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d("DBManager", "Request to upgrade from " + oldVersion + " to " + newVersion);
        database.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + GOALS_TABLE_NAME);
        onCreate(database);
    }
    // todo: need to defend against leaks by ensuring DBManager and all Cursors are closed after use
    // returns Cursor containing all the data from the database
    public static Cursor getAllData() {
        return dbHandle.getReadableDatabase().rawQuery("SELECT * FROM " + LOG_TABLE_NAME, null);
    }

    // inserts a LogEntry object to the database. Returns the id
    public static long insertEntry(LogEntry newEntry) {
        Log.d("DBManager", "Adding " + newEntry);
        SQLiteDatabase db = dbHandle.getWritableDatabase();
        return db.insert(LOG_TABLE_NAME, null, DBUtil.getContentVals(newEntry));
    }

    // returns LogEntry under given id
    public static LogEntry getEntry(long id) {
        SQLiteDatabase db = dbHandle.getWritableDatabase();
        Cursor retrieved = db.rawQuery("SELECT * FROM " + LOG_TABLE_NAME +
                " WHERE " + LOG_COLUMN_ID + " = " + id, null);
        return DBUtil.getLogsFromCursor(retrieved).get(0);
    }

    // updates LogEntry under given id with the new LogEntry object
    public static boolean updateEntry(long id, LogEntry newEntry) {
        Log.d("DBManager", "Updating " + getEntry(id) + " to " + newEntry);
        SQLiteDatabase db = dbHandle.getWritableDatabase();
        long new_id = db.update(LOG_TABLE_NAME, DBUtil.getContentVals(newEntry), LOG_COLUMN_ID + " = " + id, null);
        return new_id > -1;
    }

    // uses LogEntry's timestamp to look it up in the database. If found, sets new values. Raise
    // IllegalArgumentException if oldEntry can't be found in the database
    public static boolean updateEntry(LogEntry oldEntry, LogEntry newEntry) {
        Log.d("DBManager", "Updating " + oldEntry + " to " + newEntry);
        Cursor result = runQuery("SELECT " + LOG_COLUMN_ID + " FROM " + LOG_TABLE_NAME + " WHERE "
                + LOG_COLUMN_ACTIVITY + " = '" + oldEntry.getActivityName() + "' AND "
                + LOG_COLUMN_DURATION + " = " + oldEntry.getDuration() + " AND "
                + LOG_COLUMN_TIMESTAMP + " = " + newEntry.getDateInMS());
        result.moveToFirst();
        long id = result.getLong(result.getColumnIndex(LOG_COLUMN_ID));
        Log.d("DBManager", "got id " + id);
        result.close();
        return updateEntry(id, newEntry);
    }

    // deletes given row from the database
    public static void deleteEntry(long id) {
        Log.d("DBManager", "Deleting " + getEntry(id));
        dbHandle.getWritableDatabase().delete(LOG_TABLE_NAME, LOG_COLUMN_ID + " = " + id, null);
    }

    // uses LogEntry's timestamp to find it in the database and delete it. Although it is not guaranteed
    // the TimeStamp will be unique (there is an insanely low chance it isn't) it almost
    // certainly will be
    public static void deleteEntry(LogEntry toDelete) {
        Log.d("DBManager", "Deleting " + toDelete);
        dbHandle.getWritableDatabase().delete(LOG_TABLE_NAME,
                LOG_COLUMN_ACTIVITY + " = '" + toDelete.getActivityName() + "' AND " +
                LOG_COLUMN_DURATION + " = " + toDelete.getDuration() + " AND " +
                LOG_COLUMN_TIMESTAMP + " = " + toDelete.getDateInMS(), null);
    }

    // runs the given query and returns the Cursor
    public static Cursor runQuery(String sqlQuery) {
        return dbHandle.getWritableDatabase().rawQuery(sqlQuery, null);
    }

    // returns alphabetically-sorted list of all Activity names that exist
    public static List<String> getAllActivityNames() {
        Cursor cursor = runQuery("SELECT DISTINCT " + LOG_COLUMN_ACTIVITY + " FROM " + LOG_TABLE_NAME +
                " ORDER BY " + LOG_COLUMN_ACTIVITY + " DESC");
        List<String> names = new LinkedList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            names.add(cursor.getString(cursor.getColumnIndex(LOG_COLUMN_ACTIVITY)));
            cursor.moveToNext();
        }
        cursor.close();
        return names;
    }

    public static void addGoal(Goal goal) {
        Log.d("DBManager", "Adding Goal " + goal.toString());
        dbHandle.getWritableDatabase().insert(GOALS_TABLE_NAME, null, DBUtil.getContentVals(goal));
    }

    // returns list of Goals that are valid today, by increasing end date
    public static List<Goal> getGoals(Date today) {
        Cursor cursor = runQuery("SELECT * FROM " + GOALS_TABLE_NAME + " WHERE " + GOAL_START_TIME
                + " > " + today.getTime() + " OR " + GOAL_END_TIME + " > " + today.getTime() +
                " ORDER BY " + GOAL_START_TIME + " ASC");
        return DBUtil.getGoalsFromCursor(cursor);
    }

    // given the last time an update was made, goes through recurring goals table and creates
    // new goals as required. This is used to generate goal objects as scheduled.
    public static void refreshRecurringGoals(Date lastUpdate) {

    }
}
