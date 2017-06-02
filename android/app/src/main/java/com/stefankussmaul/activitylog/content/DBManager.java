package com.stefankussmaul.activitylog.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public DBManager(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override // run SQL command to create the database with the table and fields
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " + LOG_TABLE_NAME + " (" +
                LOG_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                LOG_COLUMN_ACTIVITY + " INTEGER, " +
                LOG_COLUMN_DURATION + " INTEGER, " +
                LOG_COLUMN_TIMESTAMP + " INTEGER)");
        Log.d("DBManager", "Created Database");
    }

    @Override // drop the table and re-create database if needs to be ugraded
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d("DBManager", "Request to upgrade from " + oldVersion + " to " + newVersion);
        database.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
        onCreate(database);
    }

    // returns Cursor containing all the data from the database
    public Cursor getAllData() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + LOG_TABLE_NAME, null);
    }

    // inserts a LogEntry object to the database. Returns the id
    public long insertEntry(LogEntry newEntry) {
        Log.d("DBManager", "Adding " + newEntry);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(LOG_TABLE_NAME, null, DBUtil.getContentVals(newEntry));
        return id;
    }

    // returns LogEntry under given id
    public LogEntry getEntry(long id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor retrieved = db.rawQuery("SELECT * FROM " + LOG_TABLE_NAME +
                " WHERE " + LOG_COLUMN_ID + " = " + id, null);
        return DBUtil.getLogsFromCursor(retrieved).get(0);
    }

    // updates LogEntry under given id with the new LogEntry object
    public boolean updateEntry(long id, LogEntry newEntry) {
        Log.d("DBManager", "Updating " + getEntry(id) + " to " + newEntry);
        SQLiteDatabase db = getWritableDatabase();
        long new_id = db.update(LOG_TABLE_NAME, DBUtil.getContentVals(newEntry), LOG_COLUMN_ID + " = " + id, null);
        return new_id > -1;
    }

    // uses LogEntry's timestamp to look it up in the database. If found, sets new values
    public boolean updateEntry(LogEntry oldEntry, LogEntry newEntry) {
        Cursor result = runQuery("SELECT " + LOG_COLUMN_ID + " FROM " + LOG_TABLE_NAME + " WHERE "
                + LOG_COLUMN_TIMESTAMP + " = '" + newEntry.getDateInMS() + "'");
        result.moveToFirst();
        long id = result.getLong(result.getColumnIndex(LOG_COLUMN_ID));
        result.close();
        return updateEntry(id, newEntry);
    }

    // deletes given row from the database
    public void deleteEntry(long id) {
        Log.d("DBManager", "Deleting " + getEntry(id));
        SQLiteDatabase db = getWritableDatabase();
        db.delete(LOG_TABLE_NAME, LOG_COLUMN_ID + " = " + id, null);
    }

    // uses LogEntry's timestamp to find it in the database and delete it. Although it is not guaranteed
    // the TimeStamp will be unique (there is an insanely low chance it isn't) it almost
    // certainly will be
    public void deleteEntry(LogEntry toDelete) {
        getWritableDatabase().delete(LOG_TABLE_NAME, LOG_COLUMN_TIMESTAMP + " = '" +
                toDelete.getDateInMS() + "'", null);
    }

    // runs the given query and returns the Cursor
    public Cursor runQuery(String sqlQuery) {
        return getWritableDatabase().rawQuery(sqlQuery, null);
    }

    // returns alphabetically-sorted list of all Activity names that exist
    public List<String> getAllActivityNames() {
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
}
