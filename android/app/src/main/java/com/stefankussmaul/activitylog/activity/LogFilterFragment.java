package com.stefankussmaul.activitylog.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.LogEntry;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.LinkedList;
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
    private EditText duration1;
    private EditText duration2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LogFilterFragment", "Created");
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

        dateConfig = (Spinner) view.findViewById(R.id.date_config_1);
        // create adapter using date key words from QueryBuilder
        ArrayAdapter<String> date_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, QueryBuilder.getDateKeyWords(getActivity()));
        dateConfig.setAdapter(date_adapter);
        // configure reaction to an item being selected
        dateConfig.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        datePicker1 = (Button) view.findViewById(R.id.choose_date_1);
        datePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDatePickerClicked(v);
            }
        });
        datePicker2 = (Button) view.findViewById(R.id.choose_date_2);
        datePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDatePickerClicked(v);
            }
        });

        durationConfig = (Spinner) view.findViewById(R.id.duration_config_1);
        // create adapter using Duration keywords from QueryBuilder
        ArrayAdapter<String> duration_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, QueryBuilder.getDurationKeyWords(getActivity()));
        durationConfig.setAdapter(duration_adapter);

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
