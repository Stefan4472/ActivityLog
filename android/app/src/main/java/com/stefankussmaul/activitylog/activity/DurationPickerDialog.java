package com.stefankussmaul.activitylog.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DateUtil;

/**
 * Dialog that allows user to set/choose a specific duration in Hours/Minutes/Seconds format. Can
 * be set to hide any of the fields (e.g. show HOURS and MINUTES but hide SECONDS).
 */

public class DurationPickerDialog extends DialogFragment {

    private static final String TITLE_KEY = "TITLE";
    private static final String SHOW_HOURS_KEY = "SHOW_HOURS_KEY";
    private static final String HOURS_KEY = "HOURS_KEY";
    private static final String SHOW_MINS_KEY = "SHOW_MINUTES_KEY";
    private static final String MINUTES_KEY = "MINUTES_KEY";
    private static final String SHOW_SECS_KEY = "SHOW_SECONDS_KEY";
    private static final String SECONDS_KEY = "SECONDS_KEY";

    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;

    private int curHour;
    private int curMin;
    private int curSec;

    private OnDurationChangedListener mListener;

    // interface parent activity must implement
    public interface OnDurationChangedListener {
        // called when the duration is changed
//        void onDurationChanged(DurationPickerDialog dialog, int hours, int minutes, int seconds);
        // called when the duration is confirmed
        void onDurationSet(DurationPickerDialog dialog, int hours, int minutes, int seconds);
    }

    public static DurationPickerDialog newInstance(String title, boolean showHours, boolean showMinutes,
                                                   boolean showSeconds, int startTime) {
        return newInstance(title, showHours, startTime / DateUtil.HOUR_MS, showMinutes,
                startTime / DateUtil.MINUTE_MS, showSeconds, startTime/ DateUtil.SECOND_MS);
    }

    public static DurationPickerDialog newInstance(String title, boolean showHours, int hours, boolean showMinutes,
                                            int minutes, boolean showSeconds, int seconds) {
        DurationPickerDialog dialog = new DurationPickerDialog();

        // populate the bundle
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_KEY, title);
        bundle.putBoolean(SHOW_HOURS_KEY, showHours);
        bundle.putInt(HOURS_KEY, hours);
        bundle.putBoolean(SHOW_MINS_KEY, showMinutes);
        bundle.putInt(MINUTES_KEY, minutes);
        bundle.putBoolean(SHOW_SECS_KEY, showSeconds);
        bundle.putInt(SECONDS_KEY, seconds);

        // set args and return dialog
        dialog.setArguments(bundle);
        return dialog;
    }

    public void setOnDurationChangedListener(OnDurationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setTitle(getArguments().getString(TITLE_KEY));

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.duration_picker_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get args from bundle
        Bundle args = getArguments();

        // extract settings for initial hour/min/sec
        curHour = args.getInt(HOURS_KEY);
        curMin = args.getInt(MINUTES_KEY);
        curSec = args.getInt(SECONDS_KEY);

        hourPicker = (NumberPicker) view.findViewById(R.id.hour_picker);
        minutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);
        secondPicker = (NumberPicker) view.findViewById(R.id.second_picker);

        // initialize OnValueChangeListener that can be used with each NumberPicker
        NumberPicker.OnValueChangeListener change_listener = new NumberPicker.OnValueChangeListener() {
            @Override // check which
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                switch (picker.getId()) {
                    case R.id.hour_picker:
                        curHour = hourPicker.getValue();
                        break;
                    case R.id.minute_picker:
                        curMin = minutePicker.getValue();
                        break;
                    case R.id.second_picker:
                        curSec = secondPicker.getValue();
                        break;
                    default:
                        throw new IllegalArgumentException("Unrecognized NumberPicker");
                }
            }
        };

        // for each field we need to check whether we should show it or not. If not, set to
        // GONE. Else, initialize proper min/max values, starting value, and listener
        if (args.getBoolean(SHOW_HOURS_KEY)) {
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            hourPicker.setValue(curHour);
            hourPicker.setOnValueChangedListener(change_listener);
        } else {
            hourPicker.setVisibility(View.GONE);
        }

        if (args.getBoolean(SHOW_MINS_KEY)) {
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setValue(curMin);
            minutePicker.setOnValueChangedListener(change_listener);
        } else {
            minutePicker.setVisibility(View.GONE);
        }

        if (args.getBoolean(SHOW_SECS_KEY)) {
            secondPicker.setMinValue(0);
            secondPicker.setMaxValue(59);
            secondPicker.setValue(curSec);
            secondPicker.setOnValueChangedListener(change_listener);
        } else {
            secondPicker.setVisibility(View.GONE);
        }

        // set OnClickListener of confirm button to fire OnDurationSet
        Button confirm = (Button) view.findViewById(R.id.confirm_time);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDurationSet(DurationPickerDialog.this, curHour, curMin, curSec);
                }
            }
        });

        // set OnClickListener of cancel button to dismiss the dialog
        Button cancel = (Button) view.findViewById(R.id.edit_log);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // todo: callback method?
            }
        });
    }
}
