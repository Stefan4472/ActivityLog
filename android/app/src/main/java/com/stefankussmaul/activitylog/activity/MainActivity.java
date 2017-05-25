package com.stefankussmaul.activitylog.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.QueryBuilder;

/**
 * Displays MainScreen of the app.
 */

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.mainscreen_layout);
        DBManager manager = new DBManager(this);
//        manager.insertEntry(new LogEntry("Testing", System.currentTimeMillis(), 100000));
//        manager.insertEntry(new LogEntry("Testing", System.currentTimeMillis(), 500));
        QueryBuilder query_builder = new QueryBuilder();

//        query_builder.setActivityFilter("Testing");
//        Log.d("MainActivity", "Generated query: " + query_builder.getQuery());
//        Log.d("MainActivity", DBUtil.logListToString(DBUtil.getLogsFromCursor(manager.runQuery(query_builder.getQuery()))));
//
//        query_builder.setDateBoundedMinMax(new Date(2017, 5, 25), new Date(2017, 5, 26));
//        Log.d("MainActivity", "Generated query: " + query_builder.getQuery());
//        Log.d("MainActivity", DBUtil.logListToString(DBUtil.getLogsFromCursor(manager.runQuery(query_builder.getQuery()))));
//
//        query_builder.setDurationFilter(0, 2000);
//        Log.d("MainActivity", "Generated query: " + query_builder.getQuery());
//        Log.d("MainActivity", DBUtil.logListToString(DBUtil.getLogsFromCursor(manager.runQuery(query_builder.getQuery()))));
        // todo: move to legit testing class
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        manager.insertEntry(new LogEntry("TestInsert", System.currentTimeMillis(), 3_600_000));
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        long id = manager.insertEntry(new LogEntry("TestModify", System.currentTimeMillis(), 7_200_000));
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        manager.updateEntry(id, new LogEntry("TestModify2", System.currentTimeMillis(), 0));
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        manager.deleteEntry(id);
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        Log.d("MainActivity", (new LogEntry("Hi", System.currentTimeMillis(), 1000)).toString());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
