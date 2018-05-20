package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DBUtil;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.Goal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Stefan on 5/13/2018.
 */

public class NewGoalActivity extends AppCompatActivity {

    public static final String TIME_GOAL = "Time Goal", REPETITION_GOAL = "Repetition Goal";
    public static final String DAILY_GOAL = "Today", WEEKLY_GOAL = "This Week", MONTHLY_GOAL = "This Month";
    private EditText goalNameEntry;
    private Spinner goalTypeSpinner;
    private ViewSwitcher goalEntrySwitcher;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker repetitionPicker;
    private Spinner activitySpinner;
    private Spinner repeatSpinner;
    private CheckBox recurMonBox, recurTuesBox, recurWedBox, recurThursBox,
            recurFriBox, recurSatBox, recurSunBox, recurWeeklyBox, recurMonthlyBox;
    private EditText goalNoteEntry;

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

        goalNameEntry = (EditText) findViewById(R.id.goal_name_entry);
        goalTypeSpinner = (Spinner) findViewById(R.id.goal_type_spinner);
        goalEntrySwitcher = (ViewSwitcher) findViewById(R.id.goal_entry_switcher);
        hourPicker = (NumberPicker) findViewById(R.id.hour_picker);
        minutePicker = (NumberPicker) findViewById(R.id.minute_picker);
        repetitionPicker = (NumberPicker) findViewById(R.id.repetition_picker);
        activitySpinner = (Spinner) findViewById(R.id.activity_spinner);
        repeatSpinner = (Spinner) findViewById(R.id.repeat_spinner);
        goalNoteEntry = (EditText) findViewById(R.id.goal_note_entry);

        recurMonBox = (CheckBox) findViewById(R.id.recur_mon_btn);
        recurTuesBox = (CheckBox) findViewById(R.id.recur_tue_btn);
        recurWedBox = (CheckBox) findViewById(R.id.recur_wed_btn);
        recurThursBox = (CheckBox) findViewById(R.id.recur_thu_btn);
        recurFriBox = (CheckBox) findViewById(R.id.recur_fri_btn);
        recurSatBox = (CheckBox) findViewById(R.id.recur_sat_btn);
        recurSunBox = (CheckBox) findViewById(R.id.recur_sun_btn);
        recurWeeklyBox = (CheckBox) findViewById(R.id.recur_weekly);
        recurMonthlyBox = (CheckBox) findViewById(R.id.recur_monthly);

        hourPicker.setMaxValue(1000);
        minutePicker.setMaxValue(59);
        repetitionPicker.setMinValue(1);
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
        String goal_name = goalNameEntry.getText().toString();
        String activity_name = activitySpinner.getSelectedItem().toString();

        // calculate numerical value of goal
        int goal_num;
        Goal.GoalType goal_type;

        // calculate goal_num as ms, or as number of repetitions
        if (goalTypeSpinner.getSelectedItem().toString().equals(TIME_GOAL)) {
            goal_num = (int) DateUtil.timeToMs(hourPicker.getValue(), minutePicker.getValue(), 0);
            goal_type = Goal.GoalType.GOAL_TIME;
        } else {
            goal_num = repetitionPicker.getValue();
            goal_type = Goal.GoalType.GOAL_REPETITIONS;
        }

        Date startDate, endDate;
        switch (repeatSpinner.getSelectedItem().toString()) {
            case DAILY_GOAL:
            default:
                // set to start at midnight this morning and run to midnight tonight
                startDate = DateUtil.getMidnightVal(Calendar.getInstance()).getTime();
//                endDate = DateUtil.addToDate(startDate, 1, Calendar.DAY_OF_YEAR);
                endDate = new Date(startDate.getTime() + DateUtil.DAY_MS); // THERE'S AN ISSUE WITH SOMETHING HERE
                Log.d("NewGoalActivity", "Start is " + startDate.getTime() + " End is " + endDate.getTime());
                break;
            case WEEKLY_GOAL:
                // set to start at midnight this morning and run for 7 days // TODO: RUN TO END OF WEEK
                startDate = DateUtil.getMidnightVal(Calendar.getInstance()).getTime();
                endDate = DateUtil.addToDate(startDate, 7, Calendar.DAY_OF_YEAR);
                break;
            case MONTHLY_GOAL:
                // set to start at midnight this morning and run for 30 days // TODO: RUN TO END OF MONTH
                startDate = DateUtil.getMidnightVal(Calendar.getInstance()).getTime();
                endDate = DateUtil.addToDate(startDate, 30, Calendar.DAY_OF_YEAR);
                break;
        }

        String note = goalNoteEntry.getText().toString();

        // check if user wanted the goal to recur--in which case, a RecurringGoal is required
        boolean recur_mon = recurMonBox.isChecked();
        boolean recur_tue = recurTuesBox.isChecked();
        boolean recur_wed = recurWedBox.isChecked();
        boolean recur_thu = recurWedBox.isChecked();
        boolean recur_fri = recurWedBox.isChecked();
        boolean recur_sat = recurWedBox.isChecked();
        boolean recur_sun = recurWedBox.isChecked();
        boolean recur_weekly = recurWedBox.isChecked();
        boolean recur_monthly = recurWedBox.isChecked();

        if (recur_mon || recur_tue || recur_wed || recur_thu || recur_fri || recur_sat ||
                recur_sun || recur_weekly || recur_monthly) {
            // create the RecurringGoal and add to database
            // TODO
        } else {
            // create a Goal and add to the database
            DBManager.addGoal(new Goal(activity_name, startDate, endDate, goal_type, goal_num, note));
        }
    }
}
