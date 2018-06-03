package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.stefankussmaul.activitylog.R;

/**
 * Created by Stefan on 6/3/2018.
 */

public class NewTodoActivity extends AppCompatActivity {

    private EditText titleEntry;
    private Button addStartTimeBtn;
    private LinearLayout setStartTimeLayout;
    private NumberPicker startHourPicker;
    private NumberPicker startMinutePicker;
    private NumberPicker startAMPMPicker;
    private LinearLayout setNoteLayout;
    private LinearLayout setChecklistLayout;

    private static final String AM_STRING = "AM", PM_STRING = "PM";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_todo_activity);

        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable up button to go back to MainActivity
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        titleEntry = (EditText) findViewById(R.id.todo_set_title);
        addStartTimeBtn = (Button) findViewById(R.id.todo_add_start_btn); // TODO: REMOVE
        setStartTimeLayout = (LinearLayout) findViewById(R.id.todo_set_start);
        startHourPicker = (NumberPicker) findViewById(R.id.todo_start_hour_picker);
        startMinutePicker = (NumberPicker) findViewById(R.id.todo_start_minute_picker);
        startAMPMPicker = (NumberPicker) findViewById(R.id.todo_start_ampm_picker);
        setNoteLayout = (LinearLayout) findViewById(R.id.todo_add_note_layout);
        setChecklistLayout = (LinearLayout) findViewById(R.id.todo_add_checklist_layout);

        startHourPicker.setMinValue(0);
        startHourPicker.setMaxValue(12);
        startMinutePicker.setMinValue(0); // TODO: TIME PICKERS SHOULD HAVE INCREMENTS OF 10 MINUTES
        startMinutePicker.setMaxValue(59);
        startAMPMPicker.setMinValue(0);
        startAMPMPicker.setMaxValue(1);
        startAMPMPicker.setDisplayedValues(new String[] {AM_STRING, PM_STRING});
    }

    public void onAddStartTime(View view) {
        setStartTimeLayout.setVisibility(View.VISIBLE);
    }

    public void onAddNote(View view) {
        setNoteLayout.setVisibility(View.VISIBLE);
    }

    public void onAddChecklist(View view) {
        setChecklistLayout.setVisibility(View.VISIBLE);
    }

    public void onAddChecklistItem(View view) {

    }

    public void onSaveTodo(View view) {

    }
}
