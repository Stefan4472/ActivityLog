package com.stefankussmaul.activitylog.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.LogEntry;

import java.util.Date;
import java.util.List;

/**
 * DialogFragment that allows the user to set/edit the contents of a LogEntry via TextFields and a
 * DatePicker.
 */

public class EditLogEntryDialog extends DialogFragment implements DatePickerFragment.DatePickerListener,
    DurationPickerDialog.OnDurationChangedListener {

    // keys used for storing data in bundle
    private static final String ACTIVITY_KEY = "LOGGED_ACTIVITY";
    private static final String DURATION_KEY = "LOGGED_DURATION";
    private static final String DATE_KEY = "LOGGED_DATE";

    private Spinner nameSpinner;
    private EditText newActivityField;
    private TextView durationField;
    private int duration;
    private TextView dateField;
    private Date selectedDate;
    private TextView errorMessage;

    // interface for receiving dialog callbacks
    public interface LogDialogListener {
        void onLogSaved(EditLogEntryDialog dialogFragment, LogEntry createdEntry);
        void onLogCancelled(EditLogEntryDialog dialogFragment);
    }

    // listener that receives callbacks
    private LogDialogListener mListener;

    // returns a new instance of the fragment with bundle that has the values given by toEdit.
    // Will populate the dialog with data from toEdit.
    public static EditLogEntryDialog newInstance(LogEntry toEdit) {
        EditLogEntryDialog fragment = new EditLogEntryDialog();

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
        return inflater.inflate(R.layout.edit_entry_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve arguments from bundle
        Bundle args = getArguments();

        // todo: use AutoCompleteTextView to give suggestions of Activity names
        nameSpinner = (Spinner) view.findViewById(R.id.name_spinner);
        newActivityField = (EditText) view.findViewById(R.id.new_activity_field);
        durationField = (TextView) view.findViewById(R.id.display_duration);
        duration = 0;
        dateField = (TextView) view.findViewById(R.id.selected_date);
        selectedDate = new Date(System.currentTimeMillis());
        errorMessage = (TextView) view.findViewById(R.id.error_message);

        // populate the spinner with activity names from the database
        List<String> name_choices = DBManager.getAllActivityNames();
        // add "New Activity" choice up front
        name_choices.add(0, getString(R.string.new_activity));
        ArrayAdapter<String> names_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, name_choices);
        nameSpinner.setAdapter(names_adapter);

        // configure reaction to an item being selected
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // if selected item is "New Activity", show newActivityField, else hide it
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = (String) parent.getItemAtPosition(position);
                if (choice.equals(getString(R.string.new_activity))) {
                    newActivityField.setVisibility(View.VISIBLE);
                } else {
                    newActivityField.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // populate fields with data from args (otherwise defaults will remain)
        if (args != null) {
            if (args.containsKey(ACTIVITY_KEY)) {
                int index = name_choices.indexOf(args.getString(ACTIVITY_KEY));
                if (index < 0) { // given key is not in our list of possible names. Put it in our
                    // newActivityField
                    newActivityField.setText(args.getString(ACTIVITY_KEY));
                } else { // set spinner selection to the Activity
                    nameSpinner.setSelection(index);
                }
            }
            if (args.containsKey(DURATION_KEY)) {
                duration = args.getInt(DURATION_KEY);
            }
            if (args.containsKey(DATE_KEY)) {
                selectedDate.setTime(args.getLong(DATE_KEY));
            }
        }

// todo: DurationPicker Fixes and customizable title
        durationField.setText(getString(R.string.log_duration_field, DateUtil.format(duration)));

        // pops up a DurationPicker Dialog Fragment
        ImageButton set_duration_btn = (ImageButton) view.findViewById(R.id.set_duration);

        // create a listener that displays the DurationPicker dialog
        View.OnClickListener set_duration_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DurationPickerDialog dp = DurationPickerDialog.newInstance(getString(R.string.set_duration),
                        true, true, false, duration);
                dp.setOnDurationChangedListener(EditLogEntryDialog.this);
                dp.show(getFragmentManager(), "Duration Picker");
            }
        };

        // attach listener to set_duration button as well as durationField
        set_duration_btn.setOnClickListener(set_duration_listener);
        durationField.setOnClickListener(set_duration_listener);

        // set chosen date field to the selected date
        dateField.setText(getString(R.string.log_date_field, DateUtil.format(selectedDate)));

        // pops up the DatePicker dialog fragment
        ImageButton set_date_btn = (ImageButton) view.findViewById(R.id.set_date);

        // create a listener that displays the date picker dialog
        View.OnClickListener show_date_picker_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // will be initialized to today's date
                DatePickerFragment date_picker = DatePickerFragment.newInstance(selectedDate);
                date_picker.setListener(EditLogEntryDialog.this);
                date_picker.show(getFragmentManager(), "DatePicker");
            }
        };

        // attach it to the label and the button
        set_date_btn.setOnClickListener(show_date_picker_listener);
        dateField.setOnClickListener(show_date_picker_listener);

        Button save_button = (Button) view.findViewById(R.id.save_log);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLogAndExit(v);
            }
        });

        Button cancel_button = (Button) view.findViewById(R.id.cancel_log);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLogCancelled(EditLogEntryDialog.this);
            }
        });
    }


    @Override
    public void onDateSelected(DatePickerFragment dialogFragment, Date selectedDate) {
        Log.d("LogActivityDialog", "Chose " + selectedDate.toString());
        this.selectedDate = selectedDate;
        dateField.setText(getString(R.string.log_date_field, DateUtil.format(selectedDate)));
    }

    @Override
    public void onDurationSet(DurationPickerDialog dialog, int hours, int minutes, int seconds) {
        Log.d("", hours + "," + minutes + "," + seconds);
        duration = (int) DateUtil.timeToMs(hours, minutes, seconds);
        durationField.setText(getString(R.string.log_duration_field, DateUtil.format(duration)));
        dialog.dismiss();
    }

    // called when user clicks button to save data and exit the dialog. Checks to ensure data is
    // valid before encapsulating it in a new LogEntry object and firing onLogSaved to mListener.
    public void saveLogAndExit(View view) {
        // validate input (todo)
        boolean valid = true;
        String activity_name;
        if (nameSpinner.getSelectedItem().toString().equals(getString(R.string.new_activity))) {
            activity_name = newActivityField.getText().toString();
            if (activity_name.isEmpty()) {
                errorMessage.setText(getString(R.string.error_no_name));
                errorMessage.setVisibility(View.VISIBLE);
                valid = false;
            }
        } else {
            activity_name = nameSpinner.getSelectedItem().toString();
        }
        if (valid) {
            LogEntry logged = new LogEntry(activity_name,
                    selectedDate, Integer.parseInt(durationField.getText().toString()));
            mListener.onLogSaved(this, logged);
        }
         // todo: better entry of duration
    }
}
