package com.stefankussmaul.activitylog.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.LogEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Stefan on 5/29/2017.
 */

public class LogActivityDialogFragment extends DialogFragment implements DatePickerFragment.DatePickerListener {

    // keys used for storing data in bundle
    private static final String ACTIVITY_KEY = "LOGGED_ACTIVITY";
    private static final String DURATION_KEY = "LOGGED_DURATION";
    private static final String DATE_KEY = "LOGGED_DATE";

    // used to format dates
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy"); // todo: correct locale

    private EditText nameField;
    private EditText durationField;
    private TextView dateField;
    private Date selectedDate;

    // returns a new instance of the fragment with bundle that has the values given by toEdit.
    // Will populate the dialog with data from toEdit.
    public static LogActivityDialogFragment newInstance(LogEntry toEdit) {
        LogActivityDialogFragment fragment = new LogActivityDialogFragment();

        // populate bundle
        Bundle args = new Bundle();
        args.putString(ACTIVITY_KEY, toEdit.getActivityName());
        args.putInt(DURATION_KEY, toEdit.getDuration());
        args.putLong(DATE_KEY, toEdit.getDate());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request window without title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_activity_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve arguments from bundle
        Bundle args = getArguments();

        // todo: use AutoCompleteTextView to give suggestions of Activity names
        nameField = (EditText) view.findViewById(R.id.name_field);
        durationField = (EditText) view.findViewById(R.id.duration_field);
        dateField = (TextView) view.findViewById(R.id.selected_date);
        final ImageButton show_date_picker = (ImageButton) view.findViewById(R.id.show_date_picker);
        selectedDate = new Date(System.currentTimeMillis());

        // populate fields with data from args (otherwise defaults will remain)
        if (args != null) {
            if (args.containsKey(ACTIVITY_KEY)) {
                nameField.setText(args.getString(ACTIVITY_KEY));
            }
            if (args.containsKey(DURATION_KEY)) {
                durationField.setText(args.getInt(DURATION_KEY) + "");
            }
            if (args.containsKey(DATE_KEY)) {
                selectedDate.setTime(args.getLong(DATE_KEY));
            }
        }

        // set listener on activity_name EditText so it automatically gives focus to the duration field
        nameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("LogActivityDialog", "Moving to activity duration field");
                durationField.requestFocus();
                return true;
            }
        });

        // forward Duration field to the Date Picker button
        durationField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                show_date_picker.requestFocus();
                return false;
            }
        });

        // set chosen date field to the selected date
        dateField.setText(getString(R.string.log_date_field, dateFormat.format(selectedDate)));

        // pull up DatePickerFragment initialized to selected_date
        show_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d("LogActivityDialog", "Showing DatePicker");
            // will be initialized to today's date
            DatePickerFragment date_picker = DatePickerFragment.newInstance(selectedDate);
            date_picker.setListener(LogActivityDialogFragment.this);
            date_picker.show(getFragmentManager(), "DatePicker");
            }
        });
    }


    @Override
    public void onDateSelected(DatePickerFragment dialogFragment, Date selectedDate) {
        Log.d("LogActivityDialog", "Chose " + selectedDate.toString());
        this.selectedDate = selectedDate;
        dateField.setText(getString(R.string.log_date_field, dateFormat.format(selectedDate)));
    }
}
