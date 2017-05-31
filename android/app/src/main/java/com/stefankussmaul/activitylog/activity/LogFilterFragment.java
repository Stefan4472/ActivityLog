package com.stefankussmaul.activitylog.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // get a handle to the log database
        DBManager db_manager = new DBManager(getActivity());
        // get the possible choices for activity
        List<String> activity_choices = db_manager.getAllActivityNames();
        // add the option "Any" up front
        activity_choices.add(0, "Any");
        // create adapter using the choices
        ArrayAdapter<String> activity_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, activity_choices);
        activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityChoices.setAdapter(activity_adapter);

        dateConfig = (Spinner) view.findViewById(R.id.date_config_1);
        datePicker1 = (Button) view.findViewById(R.id.choose_date_1);
        datePicker2 = (Button) view.findViewById(R.id.choose_date_2);
    }


}
