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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Stefan on 5/29/2017.
 */

public class LogActivityDialogFragment extends DialogFragment implements DatePickerFragment.DatePickerListener {

    public static LogActivityDialogFragment newInstance() {
        LogActivityDialogFragment fragment = new LogActivityDialogFragment();

        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request window without title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

//        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_activity_layout, container);
    }

    // textfields for entering name and duration of activity to be logged
    private EditText activityName, activityDuration;
    private TextView activityDate;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy"); // todo: correct locale

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve arguments from bundle
//        args = getArguments();

        // todo: use AutoCompleteTextView to give suggestions of Activity names
        activityName = (EditText) view.findViewById(R.id.name_field);
        activityName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("LogActivityDialog", "Moving to next field");
                return true;
            }
        });

        activityDuration = (EditText) view.findViewById(R.id.duration_field);

        Date today = new Date(System.currentTimeMillis());
        activityDate = (TextView) view.findViewById(R.id.selected_date);
        activityDate.setText(dateFormat.format(today));

        ImageButton show_date_picker = (ImageButton) view.findViewById(R.id.show_date_picker);
        show_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LogActivityDialog", "Showing DatePicker");
                // will be initialized to today's date
                DatePickerFragment date_picker = DatePickerFragment.newInstance();
//                date_picker.setListener(this);
                date_picker.show(getFragmentManager(), "DatePicker");
            }
        });
    }


    @Override
    public void onDateSelected(DatePickerFragment dialogFragment, Date selectedDate) {
        
    }
}
