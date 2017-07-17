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
 * Activity where user can view all logs listed out. The user can apply filters to the data as well
 * as edit or remove individual entries. Such actions are fulfilled via the database manager.
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
    // layout manager used with logEntryDisplay RecyclerView
    LinearLayoutManager displayManager;
    // current SQL query generating the LogEntries being displayed
    private QueryBuilder currentQuery;
    // list of LogEntry objects that match the current query and are shown in logEntryDisplay
    private List<LogEntry> displayedEntries;
    // index of the LogEntry in the RecyclerView that's currently selected.
    // This will be the same in the displayedEntries list, which mirrors the RecyclerView
    private int selectedIndex = -1;

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
        displayManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        logEntryDisplay.setLayoutManager(displayManager);

        editActionBtn = (FloatingActionButton) findViewById(R.id.edit_action_button);
        aggregateStats = (TextView) findViewById(R.id.stats_overview);
        onFilterUpdated(null, new QueryBuilder());
    }

    // todo: logic for selectedIndex/deleting entries from Adapter
    // fired when user clicks the editActionBtn to edit the selected LogEntry. Opens
    // EditLogEntryDialog with copy of the selected LogEntry
    public void onEditLogEntry(View v) {
        Log.d("ViewEditLog", "Received Action for " + selectedIndex);
        EditLogEntryDialog dialog = EditLogEntryDialog.newInstance(displayedEntries.get(selectedIndex));
        dialog.show(getFragmentManager(), "Edit Selected Dialog");
    }

    @Override  // fired when a LogEntry is selected. Set selectedIndex to a copy of selected and display
    // the edit action button if it isn't already shown
    public void onSelectLogEntry(int index, LogEntry selected) {
        editActionBtn.setVisibility(View.VISIBLE);
        LogEntryAdapter.setClicked(displayManager.findViewByPosition(index), true);
        if (selectedIndex > -1) {
            LogEntryAdapter.setClicked(displayManager.findViewByPosition(selectedIndex), false);
        }
        selectedIndex = index;
        editActionBtn.setVisibility(View.VISIBLE); // todo: animation
    }

    @Override // fired when a LogEntry is deselected, and there are no selections made.
    public void onDeselectLogEntry(int index, LogEntry deselected) {
        selectedIndex = -1;
        LogEntryAdapter.setClicked(displayManager.findViewByPosition(index), false);
        editActionBtn.setVisibility(View.INVISIBLE);
    }

    @Override // sends request to DBManager to delete the selected LogEntry and makes a call to
    // update the GUI
    public void onDeleteLogEntry(int index, LogEntry toDelete) {
        Log.d("ViewEditLog", "Received Delete for " + toDelete);
        DBManager.deleteEntry(toDelete);
        onFilterUpdated(null, currentQuery);
    }

    @Override // called when a LogEntry is being edited and has been saved. Update database,
    // update GUI, and close the DialogFragment
    public void onLogSaved(EditLogEntryDialog dialogFragment, LogEntry createdEntry) {
        DBManager.updateEntry(displayedEntries.get(selectedIndex), createdEntry);
        onFilterUpdated(null, currentQuery);
        selectedIndex = -1;
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
            displayedEntries = DBUtil.getLogsFromCursor(new_data);
            logEntryDisplay.swapAdapter(new LogEntryAdapter(this, displayedEntries, this), true);
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

    // called when the list of LogEntry objects to display changes. Swaps out the adapter and
    // recalculates aggregate stats. Should be called when filter changes or when an entry is removed
    private void onEntriesUpdated(List<LogEntry> entries) {

    }
}
