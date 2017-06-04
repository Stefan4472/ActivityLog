package com.stefankussmaul.activitylog.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.charts.ChartUtil;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DBUtil;
import com.stefankussmaul.activitylog.content.LogEntry;
import com.stefankussmaul.activitylog.content.LogEntryAdapter;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.util.List;

/**
 * Created by Stefan on 5/29/2017.
 */

public class ViewEditLogActivity extends AppCompatActivity implements
        LogFilterFragment.OnFilterUpdatedListener, LogEntryAdapter.LogEntryListener,
        EditLogEntryDialog.LogDialogListener {

    // RecyclerView displaying the list of queried LogEntries
    private RecyclerView logEntryDisplay;
    // TextView displaying aggregateStats from the queried LogEntries
    private TextView aggregateStats;
    // handle to Database to retrieve queries
    private DBManager dbManager;
    // current SQL query generating the LogEntries being displayed
    private QueryBuilder currentQuery;
    // LogEntry currently being edited, if any
    private LogEntry editing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewlog_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable up button to go back to MainActivity
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        logEntryDisplay = (RecyclerView) findViewById(R.id.log_display);
        logEntryDisplay.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        logEntryDisplay.setAdapter(new LogEntryAdapter(this, demo, this));

        aggregateStats = (TextView) findViewById(R.id.stats_overview);
        onFilterUpdated(null, new QueryBuilder());
    }

    @Override // fired when a LogEntry is selected. Bring up EditLogDialogFragment
    // todo: logic for editing/deleting entries from Adapter
    public void onLogEntryAction(LogEntry selected) {
        Log.d("ViewEditLog", "Received Action for " + selected);
        // make a copy of the selected LogEntry so it can be updated or deleted
        editing = new LogEntry(selected);
        EditLogEntryDialog dialog = EditLogEntryDialog.newInstance(selected);
        dialog.show(getFragmentManager(), "Edit Selected Dialog");
    }

    @Override // called when a LogEntry is being edited and has been saved. Update RecyclerView,
    // database, and close the DialogFragment
    public void onLogSaved(EditLogEntryDialog dialogFragment, LogEntry createdEntry) {
        dbManager.updateEntry(editing, createdEntry);
        editing = null;
        dialogFragment.dismiss();
    }

    @Override // called when the filter is changed. Get LogEntries from the database and swap out the
    // RecyclerView adapter with a new one that contains the new data
    public void onFilterUpdated(LogFilterFragment logFilterFragment, QueryBuilder query) {
        if (!query.equals(currentQuery)) {
            // retrieve new data and create a new adapter. Swap out the current one
            Cursor new_data = dbManager.runQuery(query.getQuery());
            List<LogEntry> entries = DBUtil.getLogsFromCursor(new_data);
            logEntryDisplay.swapAdapter(new LogEntryAdapter(this, entries, this), true);
            // calculate and display aggregates
            long agg_time = DBUtil.getTotalOfAggregates(
                    DBUtil.getAggregatesFromCursor(dbManager.runQuery(query.getTimeSpentQuery())));
            long agg_sessions = DBUtil.getTotalOfAggregates(
                    DBUtil.getAggregatesFromCursor(dbManager.runQuery(query.getSessionCountQuery())));
            aggregateStats.setText(ChartUtil.getOverviewLabel(this, agg_sessions, agg_time));
            // update currentQuery
            currentQuery = new QueryBuilder(query);
        }
    }
}
