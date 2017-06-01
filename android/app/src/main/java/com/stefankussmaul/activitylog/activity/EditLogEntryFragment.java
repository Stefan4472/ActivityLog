package com.stefankussmaul.activitylog.activity;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.LogEntry;

import java.util.Date;

/**
 * DialogFragment that allows the user to set/edit the contents of a LogEntry via TextFields and a
 * DatePicker.
 */

public class EditLogEntryFragment extends DialogFragment implements DatePickerFragment.DatePickerListener {

    // keys used for storing data in bundle
    private static final String ACTIVITY_KEY = "LOGGED_ACTIVITY";
    private static final String DURATION_KEY = "LOGGED_DURATION";
    private static final String DATE_KEY = "LOGGED_DATE";

    private EditText nameField;
    private EditText durationField;
    private TextView dateField;
    private Date selectedDate;
    private TextView errorMessage;

    // interface for receiving dialog callbacks
    public interface LogDialogListener {
        void onLogSaved(EditLogEntryFragment dialogFragment, LogEntry createdEntry);
    }

    // listener that receives callbacks
    private LogDialogListener mListener;

    // returns a new instance of the fragment with bundle that has the values given by toEdit.
    // Will populate the dialog with data from toEdit.
    public static EditLogEntryFragment newInstance(LogEntry toEdit) {
        EditLogEntryFragment fragment = new EditLogEntryFragment();

        // populate bundle
        Bundle args = new Bundle();
        args.putString(ACTIVITY_KEY, toEdit.getActivityName());
        args.putInt(DURATION_KEY, toEdit.getDuration());
        args.putLong(DATE_KEY, toEdit.getDateInMS());
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

    @Override // ensures activity implements LogDialogListener
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LogDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LogDialogListener");
        }
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
        errorMessage = (TextView) view.findViewById(R.id.error_message);

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
                return true;
            }
        });

        // set chosen date field to the selected date
        dateField.setText(getString(R.string.log_date_field, DateUtil.format(selectedDate)));

        // pull up DatePickerFragment initialized to selected_date
        show_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d("LogActivityDialog", "Showing DatePicker");
            // will be initialized to today's date
            DatePickerFragment date_picker = DatePickerFragment.newInstance(selectedDate);
            date_picker.setListener(EditLogEntryFragment.this);
            date_picker.show(getFragmentManager(), "DatePicker");
            }
        });

        Button exit_button = (Button) view.findViewById(R.id.save_and_exit);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLogAndExit(v);
            }
        });
    }


    @Override
    public void onDateSelected(DatePickerFragment dialogFragment, Date selectedDate) {
        Log.d("LogActivityDialog", "Chose " + selectedDate.toString());
        this.selectedDate = selectedDate;
        dateField.setText(getString(R.string.log_date_field, DateUtil.format(selectedDate)));
    }

    // called when user clicks button to save data and exit the dialog. Checks to ensure data is
    // valid before encapsulating it in a new LogEntry object and firing onLogSaved to mListener.
    public void saveLogAndExit(View view) {
        // validate input (todo)
        if (nameField.getText().toString().isEmpty()) {
            errorMessage.setText(getString(R.string.error_no_name));
            errorMessage.setVisibility(View.VISIBLE);
        } else { // todo: better entry of duration
            LogEntry logged = new LogEntry(nameField.getText().toString(), selectedDate.getTime(),
                    Integer.parseInt(durationField.getText().toString()));
            if (mListener != null) {
                mListener.onLogSaved(this, logged);
            }
        }
    }
}
