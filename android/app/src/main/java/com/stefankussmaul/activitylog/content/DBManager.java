package com.stefankussmaul.activitylog.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class to provide access to the database.
 */

public class DBManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "ActivityLog.db";
    private static final String LOG_TABLE_NAME = "LogTable";
    // unique id of log entry
    public static final String LOG_COLUMN_ID = "_id";
    // activity name, stored as a String (todo: use Activity id and another table)
    public static final String LOG_COLUMN_ACTIVITY = "activity";
    // duration of activity, stored as number of milliseconds
    public static final String LOG_COLUMN_DURATION = "duration";
    // timestamp of activity, stored as number of milliseconds
    public static final String LOG_COLUMN_TIMESTAMP = "timestamp";

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

    // inserts a LogEntry object to the database. Returns the id
    public long insertEntry(LogEntry newEntry) {
        Log.d("DBManager", "Adding " + newEntry);
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(LOG_TABLE_NAME, null, DBUtil.getContentVals(newEntry));
        assert(id != -1);
        assert getEntry(id).equals(newEntry);
        return id;
    }

    // returns Cursor containing all the data from the database
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + LOG_TABLE_NAME, null);
    }

    // returns LogEntry under given id
    public LogEntry getEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor retrieved = db.rawQuery("SELECT * FROM " + LOG_TABLE_NAME +
                " WHERE " + LOG_COLUMN_ID + " = " + id, null);
        return DBUtil.getLogsFromCursor(retrieved).get(0);
    }

    // updates LogEntry under given id with the new LogEntry object
    public boolean updateEntry(long id, LogEntry newEntry) {
        Log.d("DBManager", "Updating " + getEntry(id) + " to " + newEntry);
        SQLiteDatabase db = this.getWritableDatabase();
        long new_id = db.update(LOG_TABLE_NAME, DBUtil.getContentVals(newEntry), LOG_COLUMN_ID + " = " + id, null);
        assert getEntry(new_id).equals(newEntry);
        return true;
    }

    // deletes given row from the database
    public void deleteEntry(long id) {
        Log.d("DBManager", "Deleting " + getEntry(id));
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LOG_TABLE_NAME, LOG_COLUMN_ID + " = " + id, null);
    }
}
