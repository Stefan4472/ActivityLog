package com.stefankussmaul.activitylog.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
    private TimePickerFragment setStartTimeFragment;
    private TimePickerFragment setEndTimeFragment;
    private TimePickerFragment setReminderTimeFragment;
    private LinearLayout setNoteLayout;
    private LinearLayout setChecklistLayout;

    private FragmentManager fragmentManager;

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

        fragmentManager = getSupportFragmentManager();

        titleEntry = (EditText) findViewById(R.id.todo_set_title);
        setStartTimeFragment = (TimePickerFragment) fragmentManager.findFragmentById(R.id.todo_start_time_picker);
        setEndTimeFragment = (TimePickerFragment) fragmentManager.findFragmentById(R.id.todo_end_time_picker);
        setReminderTimeFragment = (TimePickerFragment) fragmentManager.findFragmentById(R.id.todo_remind_time_picker);
        setNoteLayout = (LinearLayout) findViewById(R.id.todo_add_note_layout);
        setChecklistLayout = (LinearLayout) findViewById(R.id.todo_add_checklist_layout);

        // hide setStartTimeFragment for the moment
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(setStartTimeFragment)
                .hide(setEndTimeFragment)
                .hide(setReminderTimeFragment)
                .commit();

        // hide setStartTimeFragment for the moment
//        fragmentManager.beginTransaction()
//                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                .hide(setEndTimeFragment)
//                .commit();

        /*if (fragment.isHidden()) {
            ft.show(fragment);
            button.setText("Hide");
        } else {
            ft.hide(fragment);
            button.setText("Show");
        }
        ft.commit();*/
    }

    //
    public void onAddStartTime(View view) {
        // show fragment if not hidden
        if (setStartTimeFragment.isHidden()) {
            fragmentManager.beginTransaction()
                    .show(setStartTimeFragment)
                    .commit();
        }
    }

    public void onAddEndTime(View view) {
        // show fragment if not hidden
        if (setEndTimeFragment.isHidden()) {
            fragmentManager.beginTransaction()
                    .show(setEndTimeFragment)
                    .commit();
        }
    }

    public void onAddReminder(View view) {
        // show fragment if not hidden
        if (setReminderTimeFragment.isHidden()) {
            fragmentManager.beginTransaction()
                    .show(setReminderTimeFragment)
                    .commit();
        }
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
