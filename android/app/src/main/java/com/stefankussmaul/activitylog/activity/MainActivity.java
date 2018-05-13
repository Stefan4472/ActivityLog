package com.stefankussmaul.activitylog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.ActivityAggregate;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DBUtil;
import com.stefankussmaul.activitylog.content.LogEntry;
import com.stefankussmaul.activitylog.content.QueryBuilder;
import com.stefankussmaul.activitylog.content.StringUtil;

import java.util.List;

/**
 * Displays MainScreen of the app.
 */

public class MainActivity extends AppCompatActivity implements EditLogEntryDialog.LogDialogListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DBManager.init(this);

        Log.d("MainActivity", "Printing Database\n" + StringUtil.cursorToString(DBManager.getAllData()));

        QueryBuilder query_builder = new QueryBuilder();

        Log.d("MainActivity", "Printing calculated aggregates");
        Log.d("MainActivity", query_builder.getSessionCountQuery());
        Log.d("MainActivity", query_builder.getTimeSpentQuery());
        Log.d("MainActivity", "Aggregate Durations");
        int counter = 1;
        List<ActivityAggregate> sums = DBUtil.getAggregatesFromCursor(DBManager.runQuery(query_builder.getTimeSpentQuery()));
        for (ActivityAggregate a : sums) {
            Log.d("MainActivity", counter + ". " + a.toString());
            counter++;
        }
        Log.d("MainActivity", "Aggregate Logged Sessions");
        counter = 1;
        List<ActivityAggregate> counts = DBUtil.getAggregatesFromCursor(DBManager.runQuery(query_builder.getSessionCountQuery()));
        for (ActivityAggregate c : counts) {
            Log.d("MainActivity", counter + ". " + c.toString());
            counter++;
        }

        // show daily report dialog
        DailyReportDialog report = new DailyReportDialog();
        report.show(getFragmentManager(), "Daily Report");
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
        EditLogEntryDialog log_dialog = new EditLogEntryDialog();
        log_dialog.show(getFragmentManager(), "Log");
    }

    @Override // called when a LogEntry is being closed
    public void onLogSaved(EditLogEntryDialog dialogFragment, LogEntry createdEntry) {
        DBManager.insertEntry(createdEntry);
        dialogFragment.dismiss();
    }

    @Override
    public void onLogCancelled(EditLogEntryDialog dialogFragment) {
        dialogFragment.dismiss();
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
        Intent view_intent = new Intent(this, ManageLogActivity.class);
        startActivity(view_intent);
    }

    public void launchNewGoal(View v) {
        Log.d("MainActivity", "Launching New Goal");
        Intent new_goal_intent = new Intent(this, NewGoalActivity.class);
        startActivity(new_goal_intent);
    }

    public void launchManageGoals(View view) {
        Log.d("MainActivity", "Launching Manage Goals");
        Intent goals_intent = new Intent(this, ManageGoalsActivity.class);
        startActivity(goals_intent);
    }
}
