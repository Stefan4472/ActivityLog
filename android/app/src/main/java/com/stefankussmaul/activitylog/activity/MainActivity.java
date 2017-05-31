package com.stefankussmaul.activitylog.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.LogEntry;

/**
 * Displays MainScreen of the app.
 */

public class MainActivity extends AppCompatActivity {

    // handle to database with log data
    private static DBManager logManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);

        setSupportActionBar(toolbar);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logManager = new DBManager(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // handle user clicking something in the action bar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shownav:
                Log.d("MainActivity", "ShowNav clicked");
                return true;
            default: // send to be handled by super
                return super.onOptionsItemSelected(item);
        }
    }

    public void launchLogDialog(View view) {
        Log.d("MainActivity", "Launching Log Activity");
        LogActivityDialogFragment log_dialog = new LogActivityDialogFragment();
        // set listener that inserts new LogEntry to the database and closes the dialog
        log_dialog.setListener(new LogActivityDialogFragment.LogDialogListener() {
            @Override
            public void onLogSaved(LogActivityDialogFragment dialogFragment, LogEntry createdEntry) {
               logManager.insertEntry(createdEntry);
                dialogFragment.dismiss();
            }
        });
        log_dialog.show(getFragmentManager(), "Log");
    }

    public void launchStartActivity(View view) {
        Log.d("MainActivity", "Launching Start Activity");
        Intent timer_intent = new Intent(this, TimerActivity.class);
        startActivity(timer_intent);
    }

    public void launchAnalytics(View view) {
        Log.d("MainActivity", "Launching Analytics");
        Intent analytics_intent = new Intent(this, AnalyticsActivity.class);
        startActivity(analytics_intent);
    }

    public void launchViewEditLog(View view) {
        Log.d("MainActivity", "Launching View/Edit Log");


    }

    public static DBManager getLogManager() {
        return logManager;
    }
}
