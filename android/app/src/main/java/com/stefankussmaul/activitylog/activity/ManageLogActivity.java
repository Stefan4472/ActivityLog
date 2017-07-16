package com.stefankussmaul.activitylog.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class ManageLogActivity extends AppCompatActivity implements
        LogFilterFragment.OnFilterUpdatedListener, LogEntryAdapter.LogEntryListener,
        EditLogEntryDialog.LogDialogListener {

    // RecyclerView displaying the list of queried LogEntries
    private RecyclerView logEntryDisplay;
    // TextView displaying aggregateStats from the queried LogEntries
    private TextView aggregateStats;
    // FloatingActionButton displayed when user selects a LogEntry. Clicking brings up edit dialog
    private FloatingActionButton editActionBtn;
    // handle to Database to retrieve queries
    private DBManager dbManager;
    // current SQL query generating the LogEntries being displayed
    private QueryBuilder currentQuery;
    // LogEntry currently being edited, if any
    private LogEntry editing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managelog_layout);

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

    @Override  // fired when a LogEntry is selected. Set editing to a copy of selected and display
    // the edit action button if it isn't already shown
    public void onSelectLogEntry(LogEntry selected) {
        editing = new LogEntry(selected);
    }

    // todo: logic for editing/deleting entries from Adapter
    // fired when user clicks the editActionBtn to edit the selected LogEntry
    public void onEditLogEntry(View v) {
        Log.d("ViewEditLog", "Received Action for " + editing);
        // make a copy of the selected LogEntry so it can be updated or deleted
        EditLogEntryDialog dialog = EditLogEntryDialog.newInstance(editing);
        dialog.show(getFragmentManager(), "Edit Selected Dialog");
    }

    @Override
    public void onDeleteLogEntry(LogEntry toDelete) {
        Log.d("ViewEditLog", "Received Delete for " + toDelete);
    }

    @Override // called when a LogEntry is being edited and has been saved. Update RecyclerView,
    // database, and close the DialogFragment
    public void onLogSaved(EditLogEntryDialog dialogFragment, LogEntry createdEntry) {
        DBManager.updateEntry(editing, createdEntry);
        editing = null;
        dialogFragment.dismiss();
    }

    @Override
    public void onLogCancelled(EditLogEntryDialog dialogFragment) {
        dialogFragment.dismiss();
    }

    @Override // called when the filter is changed. Get LogEntries from the database and swap out the
    // RecyclerView adapter with a new one that contains the new data
    public void onFilterUpdated(LogFilterFragment logFilterFragment, QueryBuilder query) {
        if (!query.equals(currentQuery)) {
            // retrieve new data and create a new adapter. Swap out the current one
            Cursor new_data = DBManager.runQuery(query.getQuery());
            List<LogEntry> entries = DBUtil.getLogsFromCursor(new_data);
            logEntryDisplay.swapAdapter(new LogEntryAdapter(this, entries, this), true);
            // calculate and display aggregates
            long agg_time = DBUtil.getTotalOfAggregates(
                    DBUtil.getAggregatesFromCursor(DBManager.runQuery(query.getTimeSpentQuery())));
            long agg_sessions = DBUtil.getTotalOfAggregates(
                    DBUtil.getAggregatesFromCursor(DBManager.runQuery(query.getSessionCountQuery())));
            aggregateStats.setText(ChartUtil.getOverviewLabel(this, agg_sessions, agg_time));
            // update currentQuery
            currentQuery = new QueryBuilder(query);
        }
    }
}
