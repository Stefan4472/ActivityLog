package com.stefankussmaul.activitylog.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DBQueryBuilder;
import com.stefankussmaul.activitylog.content.DBUtil;
import com.stefankussmaul.activitylog.content.LogEntry;

import java.util.Arrays;

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
        DBQueryBuilder query_builder = new DBQueryBuilder();
        query_builder.setNameFilter("Testing");
        Log.d("MainActivity", DBUtil.logListToString(DBUtil.getLogsFromCursor(manager.runQuery(query_builder.generateQuery()))));
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
