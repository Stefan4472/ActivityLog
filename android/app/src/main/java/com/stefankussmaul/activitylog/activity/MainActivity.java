package com.stefankussmaul.activitylog.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.ActivityAggregate;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DBUtil;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.Goal;
import com.stefankussmaul.activitylog.content.LogEntry;
import com.stefankussmaul.activitylog.content.QueryBuilder;
import com.stefankussmaul.activitylog.content.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Displays MainScreen of the app.
 */

public class MainActivity extends AppCompatActivity implements EditLogEntryDialog.LogDialogListener {

    private TextView titleText;
    private TextView subtitleText;
    private TextView activitySummary;
    private LinearLayout todoLayout;
    private LinearLayout goalLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        titleText = (TextView) findViewById(R.id.home_title);
        subtitleText = (TextView) findViewById(R.id.home_subtitle);
        activitySummary = (TextView) findViewById(R.id.home_activity_summary);
        todoLayout = (LinearLayout) findViewById(R.id.home_todo_layout);
        goalLayout = (LinearLayout) findViewById(R.id.home_goal_layout);

        // init database singleton
        DBManager.init(this);

        // TODO: LET USER CHANGE DATE BEING SHOWN
        Date now = new Date(System.currentTimeMillis());

        // set title e.g. Monday, 5/20
        titleText.setText(new SimpleDateFormat("EEEE, M/d").format(now));

        // set subtitle e.g. today, yesterday, 2 days ago, tomorrow, in 2 days, etc. TODO
        subtitleText.setText("Today");

        // set summary to [number of activities today] activities, [time logged today] hours
        List<LogEntry> activities_today = DBManager.getActivitiesBtwn(DateUtil.getMidnightToday(),
                DateUtil.getMidnightTomorrow());
        long time_today = 0;
        for (LogEntry activity : activities_today) {
            time_today += activity.getDuration();
        }
        activitySummary.setText(activities_today.size() + " Activities, " +
                (time_today / (double) DateUtil.HOUR_MS) + " Hours");

        // retrieve goals from database and add to goalLayout, in order TODO
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Goal goal : DBManager.getGoals(now)) {
            View goal_view = inflator.inflate(R.layout.goal_adapter, null);
            TextView title = (TextView) goal_view.findViewById(R.id.goal_title);
            title.setText("Goal " + goal.getActivity());
            goalLayout.addView(goal_view);
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

    public void launchNewTodo(View view) {
        Log.d("MainActivity", "Launching New Todo");
        Intent todo_intent = new Intent(this, NewTodoActivity.class);
        startActivity(todo_intent);
    }
}
