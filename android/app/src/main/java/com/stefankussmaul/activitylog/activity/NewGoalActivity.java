package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 5/13/2018.
 */

public class NewGoalActivity extends AppCompatActivity {

    public static final String TIME_GOAL = "Time Goal", REPETITION_GOAL = "Repetition Goal";
    public static final String DAILY_GOAL = "Today", WEEKLY_GOAL = "This Week", MONTHLY_GOAL = "This Month";
    private Spinner goalTypeSpinner;
    private ViewSwitcher goalEntrySwitcher;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker repetitionPicker;
    private Spinner activitySpinner;
    private Spinner repeatSpinner;
    private CheckBox recurMonBox, recurTuesBox, recurWedBox, recurThursBox,
            recurFriBox, recurSatBox, recurSunBox, recurWeeklyBox, recurMonthlyBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_goal_layout);

        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable up button to go back to MainActivity
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        goalTypeSpinner = (Spinner) findViewById(R.id.goal_type_spinner);
        goalEntrySwitcher = (ViewSwitcher) findViewById(R.id.goal_entry_switcher);
        hourPicker = (NumberPicker) findViewById(R.id.hour_picker);
        minutePicker = (NumberPicker) findViewById(R.id.minute_picker);
        repetitionPicker = (NumberPicker) findViewById(R.id.repetition_picker);
        activitySpinner = (Spinner) findViewById(R.id.activity_spinner);
        repeatSpinner = (Spinner) findViewById(R.id.repeat_spinner);

        hourPicker.setMaxValue(1000);
        minutePicker.setMaxValue(59);
        repetitionPicker.setMaxValue(1000);

        List<String> goal_types = new ArrayList<>();
        goal_types.add(TIME_GOAL);
        goal_types.add(REPETITION_GOAL);
        goalTypeSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, goal_types));

        goalTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = (String) adapterView.getItemAtPosition(i);
                if (choice.equals(TIME_GOAL)) {
                    goalEntrySwitcher.showPrevious();
                } else if (choice.equals(REPETITION_GOAL)) {
                    goalEntrySwitcher.showNext();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // populate activity spinner with activity names from the database
        List<String> activity_choices = DBManager.getAllActivityNames();
        // add "New Activity" choice up front
        activity_choices.add(0, getString(R.string.new_activity));
        ArrayAdapter<String> activities_adapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                activity_choices);
        activitySpinner.setAdapter(activities_adapter);

        // configure reaction to an item being selected
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // if selected item is "New Activity", show newActivityField, else hide it
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = (String) parent.getItemAtPosition(position);
                if (choice.equals(getString(R.string.new_activity))) {
//                    newActivityField.setVisibility(View.VISIBLE);
                } else {
//                    newActivityField.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> repeat_settings = new ArrayList<>();
        repeat_settings.add(DAILY_GOAL);
        repeat_settings.add(WEEKLY_GOAL);
        repeat_settings.add(MONTHLY_GOAL);
        repeatSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, repeat_settings));

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = (String) adapterView.getItemAtPosition(i);
                // show/hide different UI parts based on choice
                if (choice.equals(DAILY_GOAL)) {
                    findViewById(R.id.repeat_daily_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.repeat_weekly_layout).setVisibility(View.GONE);
                    findViewById(R.id.repeat_monthly_layout).setVisibility(View.GONE);
                } else if (choice.equals(WEEKLY_GOAL)) {
                    findViewById(R.id.repeat_daily_layout).setVisibility(View.GONE);
                    findViewById(R.id.repeat_weekly_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.repeat_monthly_layout).setVisibility(View.GONE);
                } else if (choice.equals(MONTHLY_GOAL)) {
                    findViewById(R.id.repeat_daily_layout).setVisibility(View.GONE);
                    findViewById(R.id.repeat_weekly_layout).setVisibility(View.GONE);
                    findViewById(R.id.repeat_monthly_layout).setVisibility(View.VISIBLE);
                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    // called when user clicks to save the goal
    public void onSaveGoal(View view) {

    }
}
