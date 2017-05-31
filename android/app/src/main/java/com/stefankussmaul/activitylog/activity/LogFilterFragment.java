package com.stefankussmaul.activitylog.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.util.Date;
import java.util.List;

/**
 * Interactive filter creation.
 */

public class LogFilterFragment extends Fragment {

    // displays choices for activity filters
    private Spinner activityChoices;
    // displays choices for date filter prepositions (before, after, between, etc)
    private Spinner dateConfig;
    // button that displays first date chosen for Date config
    private Button datePicker1;
    // button that displays second date chosen for Date config, if needed
    private Button datePicker2;
    private Date date1;
    private Date date2;
    private Spinner durationConfig;
    private EditText durationPicker1;
    private EditText durationPicker2;
    private TextView dateConjunction;
    private TextView durationConjunction;
    private int duration1;
    private int duration2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LogFilterFragment", "Created");
        date1 = new Date(System.currentTimeMillis());
        date2 = new Date(System.currentTimeMillis());
        duration1 = 0;
        duration2 = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_filter_layout, container, false); // todo: double check
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        // get a handle to the log database
        DBManager db_manager = new DBManager(getActivity());

        activityChoices = (Spinner) view.findViewById(R.id.activity_choices);
        // get the possible choices for activity
        List<String> activity_choices = db_manager.getAllActivityNames();
        // add the option "Any" up front
        activity_choices.add(0, "Any");
        // create adapter using the choices
        ArrayAdapter<String> activity_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, activity_choices);
//        activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityChoices.setAdapter(activity_adapter);

        // get date-related UI elements
        dateConfig = (Spinner) view.findViewById(R.id.date_config_1);
        datePicker1 = (Button) view.findViewById(R.id.choose_date_1);
        datePicker2 = (Button) view.findViewById(R.id.choose_date_2);
        dateConjunction = (TextView) view.findViewById(R.id.date_conjunction);

        // create adapter using date key words from QueryBuilder
        ArrayAdapter<String> date_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, QueryBuilder.getDateKeyWords(getActivity()));
        dateConfig.setAdapter(date_adapter);
        // configure reaction to an item being selected
        dateConfig.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // item selected: figure out which it was and show/hide date pickers based on that
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String preposition = (String) parent.getItemAtPosition(position);
                Log.d("LogFilterFragment", "User chose " + preposition);
                // hide all except label and spinner if choice == "Any"
                if (preposition.equals(getString(R.string.date_any))) {
                    datePicker1.setVisibility(View.INVISIBLE);
                    datePicker2.setVisibility(View.INVISIBLE);
                    dateConjunction.setVisibility(View.INVISIBLE);
                } else if (preposition.equals(getString(R.string.date_between))) {
                    // display both date pickers if preposition is BETWEEN
                    datePicker1.setVisibility(View.VISIBLE);
                    datePicker1.setText(DateUtil.format(date1));
                    datePicker2.setVisibility(View.VISIBLE);
                    datePicker2.setText(DateUtil.format(date2));
                    dateConjunction.setVisibility(View.VISIBLE);
                } else { // display only first date picker
                    datePicker1.setVisibility(View.VISIBLE);
                    datePicker1.setText(DateUtil.format(date1));
                    datePicker2.setVisibility(View.INVISIBLE);
                    dateConjunction.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        datePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDatePickerClicked(v);
            }
        });

        datePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDatePickerClicked(v);
            }
        });

        // get duration UI elements
        durationConfig = (Spinner) view.findViewById(R.id.duration_config_1);
        durationPicker1 = (EditText) view.findViewById(R.id.choose_duration_1);
        durationPicker2 = (EditText) view.findViewById(R.id.choose_duration_2);
        durationConjunction = (TextView) view.findViewById(R.id.duration_conjunction);

        // create adapter using Duration keywords from QueryBuilder
        ArrayAdapter<String> duration_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, QueryBuilder.getDurationKeyWords(getActivity()));
        durationConfig.setAdapter(duration_adapter);
        // configure reaction to an item being selected
        durationConfig.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // item selected: figure out which it was and show/hide date pickers based on that
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String preposition = (String) parent.getItemAtPosition(position);
                Log.d("LogFilterFragment", "User chose " + preposition);
                // hide all except label and spinner if choice == "Any"
                if (preposition.equals(getString(R.string.duration_any))) {
                    durationPicker1.setVisibility(View.INVISIBLE);
                    durationPicker2.setVisibility(View.INVISIBLE);
                    durationConjunction.setVisibility(View.INVISIBLE);
                } else if (preposition.equals(getString(R.string.duration_between))) {
                    // display both duration pickers if preposition is BETWEEN
                    durationPicker1.setVisibility(View.VISIBLE);
                    durationPicker1.setText(duration1 + "");
                    durationPicker2.setVisibility(View.VISIBLE);
                    durationPicker2.setText(duration2 + "");
                    durationConjunction.setVisibility(View.VISIBLE);
                } else { // display only first duration picker
                    durationPicker1.setVisibility(View.VISIBLE);
                    durationPicker1.setText(duration1 + "");
                    durationPicker2.setVisibility(View.INVISIBLE);
                    durationConjunction.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        durationPicker1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                duration1 = Integer.parseInt(v.getText().toString());
                return true;
            }
        });
        durationPicker2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                duration2 = Integer.parseInt(v.getText().toString());
                return true;
            }
        });
    }

    // handles user clicking a button to select a date. Determines which date we are setting
    // (first or second) and pops up a DatePickerFragment
    public void handleDatePickerClicked(View v) throws IllegalArgumentException {
        switch (v.getId()) {
            case R.id.choose_date_1: // pop up dialog to select date1
                DatePickerFragment date_picker = DatePickerFragment.newInstance(date1);
                date_picker.setListener(
                        new DatePickerFragment.DatePickerListener() {
                            @Override
                            public void onDateSelected(DatePickerFragment dialogFragment, Date selectedDate) {
                                date1 = selectedDate;
                                datePicker1.setText(DateUtil.format(date1));
                            }
                        });
                date_picker.show(getFragmentManager(), "DatePicker");
                break;
            case R.id.choose_date_2: // pop up dialog to select date2
                DatePickerFragment date2_picker = DatePickerFragment.newInstance(date2);
                date2_picker.setListener(
                        new DatePickerFragment.DatePickerListener() {
                             @Override
                             public void onDateSelected(DatePickerFragment dialogFragment, Date selectedDate) {
                                 date2 = selectedDate;
                                 datePicker2.setText(DateUtil.format(date2));
                             }
                         });
                        date2_picker.show(getFragmentManager(), "DatePicker");
                break;
            default:
                Log.d("LogfilterFragment", "Unrecognized View");
                throw new IllegalArgumentException();
        }
    }
}
