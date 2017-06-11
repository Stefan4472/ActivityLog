package com.stefankussmaul.activitylog.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;

import com.stefankussmaul.activitylog.content.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Custom Fragment which displays a DatePicker with a given date
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // keys used in bundle to store initial date fields to display
    private static final String YEAR_KEY = "YEAR", MONTH_KEY = "MONTH", DAY_KEY = "DAY";

    // interface defined to send event callbacks
    public interface DatePickerListener {
        // sends a reference to the dialog along with the Date selected wrapped in a Date object
        void onDateSelected(DatePickerFragment dialogFragment, Date selectedDate);
    }

    // receives events from the interface
    private DatePickerListener mListener;

    // returns a DatePickerFragment where the initial date will be today
    public static DatePickerFragment newInstance() {
        return newInstance(new Date(System.currentTimeMillis()));
    }

    // returns a DatePickerFragment where the initial date will be the given one
    public static DatePickerFragment newInstance(Date initialDate) {
        DatePickerFragment dialog = new DatePickerFragment();

        // init calendar to given date
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialDate);

        // populate the bundle with the DAY/MONTH/YEAR fields from Calendar
        Bundle bundle = new Bundle();
        bundle.putInt(YEAR_KEY, calendar.get(Calendar.YEAR));
        bundle.putInt(MONTH_KEY, calendar.get(Calendar.MONTH));
        bundle.putInt(DAY_KEY, calendar.get(Calendar.DAY_OF_MONTH));

        // set args and return dialog
        dialog.setArguments(bundle);
        return dialog;
    }

    public void setListener(DatePickerListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // get args from bundle
        Bundle args = getArguments();
        int year = args.getInt(YEAR_KEY);
        int month = args.getInt(MONTH_KEY);
        int day = args.getInt(DAY_KEY);

        // create new dialog initialized to given args and maxed at current date
        DatePickerDialog d = new DatePickerDialog(getActivity(), this, year, month, day);
        d.getDatePicker().setMaxDate(System.currentTimeMillis());
        return d;
    }

    @Override // DatePickerDialog.OnDateSetListener override. Called when a date is picked.
    // If a DatePickerListener has been set, this method wraps the chosen year/month/day into a
    // Date object, strips off the hour/min/second vals and sends it to the listening object
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (mListener != null) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
//            c.set(Calendar.HOUR_OF_DAY, 0);
//            c.set(Calendar.MINUTE, 0);
//            c.set(Calendar.SECOND, 0);
            mListener.onDateSelected(this, DateUtil.stripToPrecision(c.getTime(), Calendar.DAY_OF_MONTH));
        }
    }
}
