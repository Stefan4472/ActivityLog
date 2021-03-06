package com.stefankussmaul.activitylog.activity;

import android.app.Activity;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Interactive filter creation.
 */

public class LogFilterFragment extends Fragment {

    // displays choices for activity filters
    private Spinner activityChoices;
    // displays choices for date filter prepositions (before, after, between, etc)
    private Spinner dateChoices;
    // button that displays first date chosen for Date config
    private Button datePicker1;
    // button that displays second date chosen for Date config, if needed
    private Button datePicker2;
    private Date date1;
    private Date date2;
    private Spinner durationChoices;
    private EditText durationPicker1;
    private EditText durationPicker2;
    private TextView dateConjunction;
    private TextView durationConjunction;
    private int duration1;
    private int duration2;
    // the query defined by the user
    private QueryBuilder configuredQuery;
    private OnFilterUpdatedListener mListener;

    // container activity must implement this interface
    public interface OnFilterUpdatedListener {
        // sends reference to the fragment and QueryBuilder generated by user
        void onFilterUpdated(LogFilterFragment logFilterFragment, QueryBuilder query);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LogFilterFragment", "Created");
        // set both dates to midnight val of today
        date1 = DateUtil.getMidnightVal(Calendar.getInstance()).getTime();
        date2 = DateUtil.getMidnightVal(Calendar.getInstance()).getTime();
        duration1 = 0;
        duration2 = 0;
        configuredQuery = new QueryBuilder();
    }

    @Override // ensures activity implements OnFilterUpdatedListener
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFilterUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFilterUpdatedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_filter_layout, container, false); // todo: double check
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        activityChoices = (Spinner) view.findViewById(R.id.activity_choices);
        // get the possible choices for activity
        List<String> activity_choices = DBManager.getAllActivityNames();
        // add the option "Any" up front
        activity_choices.add(0, getString(R.string.activity_any));
        // create adapter using the choices
        ArrayAdapter<String> activity_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, activity_choices);
//        activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityChoices.setAdapter(activity_adapter);

        // get date-related UI elements
        dateChoices = (Spinner) view.findViewById(R.id.date_config_1);
        datePicker1 = (Button) view.findViewById(R.id.choose_date_1);
        datePicker2 = (Button) view.findViewById(R.id.choose_date_2);
        dateConjunction = (TextView) view.findViewById(R.id.date_conjunction);

        // create adapter using date key words from QueryBuilder
        ArrayAdapter<String> date_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, QueryBuilder.getDateKeyWords(getActivity()));
        dateChoices.setAdapter(date_adapter);
        // configure reaction to an item being selected
        dateChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        durationChoices = (Spinner) view.findViewById(R.id.duration_config_1);
        durationPicker1 = (EditText) view.findViewById(R.id.choose_duration_1);
        durationPicker2 = (EditText) view.findViewById(R.id.choose_duration_2);
        durationConjunction = (TextView) view.findViewById(R.id.duration_conjunction);

        // create adapter using Duration keywords from QueryBuilder
        ArrayAdapter<String> duration_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, QueryBuilder.getDurationKeyWords(getActivity()));
        durationChoices.setAdapter(duration_adapter);
        // configure reaction to an item being selected
        durationChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        Button update_button = (Button) view.findViewById(R.id.update_button);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilterUpdated(v);
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

    // handles user clicking button to update the filter. Takes data from UI fields and uses it
    // to create a QueryBuilder
    public void onFilterUpdated(View view) {
        String activity_config = activityChoices.getSelectedItem().toString();
        if (activity_config.equals(getString(R.string.activity_any))) {
            configuredQuery.resetActivityFilter();
        } else {
            configuredQuery.setActivityFilter(activity_config);
        }
        String date_config = dateChoices.getSelectedItem().toString();
        configuredQuery.setDateFilter(getActivity(), date_config, date1, date2);

        String duration_config = durationChoices.getSelectedItem().toString();
        configuredQuery.setDurationFilter(getActivity(), duration_config, duration1, duration2);
        Log.d("LogFilterFragment", "Generated Query: " + configuredQuery.getQuery());
        // fire event to OnFilterUpdated listener todo: should return a new QueryBuilder object each time?
        mListener.onFilterUpdated(this, configuredQuery);
    }

    public QueryBuilder getConfiguredQuery() {
        return configuredQuery;
    }
}
