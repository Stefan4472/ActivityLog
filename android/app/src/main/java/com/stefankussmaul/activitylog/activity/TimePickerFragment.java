package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.stefankussmaul.activitylog.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Stefan on 6/3/2018.
 */
// TODO: ACCEPT INITIAL TIME PARAMETERS
public class TimePickerFragment extends android.support.v4.app.Fragment {

    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker am_pmPicker;

    private static final String AM_STRING = "AM", PM_STRING = "PM";
    private String[] am_pmChoices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.time_picker_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        hourPicker = (NumberPicker) view.findViewById(R.id.time_picker_hour);
        minutePicker = (NumberPicker) view.findViewById(R.id.time_picker_minute);
        am_pmPicker = (NumberPicker) view.findViewById(R.id.time_picker_ampm);

        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(12);

        // minute picker is in resolution of 15 minutes. String values are converted to
        // integers later
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(3);
        minutePicker.setDisplayedValues(new String[]{"00", "15", "30", "45"});

        am_pmPicker.setMinValue(0);
        am_pmPicker.setMaxValue(1);
        am_pmChoices = new String[] {AM_STRING, PM_STRING};
        am_pmPicker.setDisplayedValues(am_pmChoices);

    }

    public int getHour() {
        return hourPicker.getValue();
    }

    // returns selected hour in 24-hour time (0-23)
    public int getHourOfDay() {
        return hourPicker.getValue() % 12 + (getAMPM().equals(PM_STRING) ? 12 : 0);
    }

    public int getMinute() {
        return minutePicker.getValue();
    }

    public String getAMPM() {
        return am_pmChoices[am_pmPicker.getValue()];
    }

    // sets the hour/minute fields of the given Date object (leaving day unchanged)
    public Date getAsDate(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(Calendar.HOUR_OF_DAY, getHourOfDay());
        calendar.set(Calendar.MINUTE, getMinute());
        return calendar.getTime();
    }
}
