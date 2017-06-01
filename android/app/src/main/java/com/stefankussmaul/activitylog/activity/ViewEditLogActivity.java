package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.LogEntry;
import com.stefankussmaul.activitylog.content.LogEntryAdapter;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 5/29/2017.
 */

public class ViewEditLogActivity extends AppCompatActivity implements
        LogFilterFragment.OnFilterUpdatedListener, LogEntryAdapter.LogEntryListener,
        EditLogEntryFragment.LogDialogListener {

    private RecyclerView logEntryDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewlog_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable up button to go back to MainActivity
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        List<LogEntry> demo = new LinkedList<>();
        demo.add(new LogEntry("Hello Oressa", System.currentTimeMillis(), 1_000));
        demo.add(new LogEntry("What's Up", System.currentTimeMillis(), 5_000));
        demo.add(new LogEntry("Surprised Much?", System.currentTimeMillis(), 3_000));
        demo.add(new LogEntry("Doggo", System.currentTimeMillis(), 1323_000));

        logEntryDisplay = (RecyclerView) findViewById(R.id.log_display);
        logEntryDisplay.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        logEntryDisplay.setAdapter(new LogEntryAdapter(this, demo, this));

    }

    @Override // fired when a LogEntry is selected. Bring up EditLogDialogFragment
    // todo: logic for editing/deleting entries from Adapter
    public void onLogEntryAction(LogEntry selected) {
        Log.d("ViewEditLog", "Received Action for " + selected);
        EditLogEntryFragment dialog = EditLogEntryFragment.newInstance(selected);
        dialog.show(getFragmentManager(), "Edit Selected Dialog");
    }

    @Override // called when a LogEntry is being edited and has been saved. Update RecyclerView,
    // database, and close the DialogFragment
    public void onLogSaved(EditLogEntryFragment dialogFragment, LogEntry createdEntry) {
        dialogFragment.dismiss();
    }

    @Override // called when the filter is changed. Get LogEntries from the database and swap out the
    // RecyclerView adapter with a new one that contains the new data
    public void onFilterUpdated(LogFilterFragment logFilterFragment, QueryBuilder query) {

    }
}
