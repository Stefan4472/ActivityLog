package com.stefankussmaul.activitylog.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.charts.ChartConfig;
import com.stefankussmaul.activitylog.charts.ChartUtil;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DBUtil;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.util.Calendar;
import java.util.Date;

/**
 * Dialog that displays a report for the user, meant to be run once a day.
 */

public class DailyReportDialog extends DialogFragment {

    private static final String DATE_KEY = "TODAY'S_DATE";
    private static final String LOGS_YESTERDAY = "LOGS_YESTERDAY";
    private static final String TIME_YESTERDAY = "TIME_YESTERDAY";
    private static final String LOGS_LAST_SEVEN_DAYS = "LOGS_LAST_SEVEN_DAYS";
    private static final String TIME_LAST_SEVEN_DAYS = "TIME_LAST_SEVEN_DAYS";
    private static final String LOGS_LAST_30_DAYS = "LOGS_LAST_THIRTY_DAYS";
    private static final String TIME_LAST_30_DAYS = "TIME_LAST_THIRTY_DAYS";

    // today's date
    private Date today;

    // returns a new instance of the fragment with bundle that is set to today's date
    public static DailyReportDialog newInstance(Date today) {
        DailyReportDialog fragment = new DailyReportDialog();
        // populate bundle
        Bundle args = new Bundle();
        args.putLong(DATE_KEY, today.getTime());
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
        return inflater.inflate(R.layout.daily_report_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve arguments from bundle
        Bundle args = getArguments();
        // initialize date
        if (args != null && args.containsKey(DATE_KEY)) {
            today = new Date(args.getLong(DATE_KEY));
        } else {
            today = new Date(System.currentTimeMillis());
        }
        today = DateUtil.stripToPrecision(today, Calendar.DAY_OF_MONTH);

        TextView yesterday_stats = (TextView) view.findViewById(R.id.yesterday_stat);
        yesterday_stats.setText(generateOverview(getActivity(), today, 1));

        TextView seven_days = (TextView) view.findViewById(R.id.seven_day_stat);
        seven_days.setText(generateOverview(getActivity(), today, 7));

        TextView thirty_days = (TextView) view.findViewById(R.id.thirty_day_stat);
        thirty_days.setText(generateOverview(getActivity(), today, 30));
    }

    private static String generateOverview(Context context, Date endDate, int numDays) {
        long sessions = generateStat(endDate, numDays, ChartConfig.ChartBy.NUM_SESSIONS);
        long time = generateStat(endDate, numDays, ChartConfig.ChartBy.TIME_SPENT);
        return ChartUtil.getOverviewLabel(context, sessions, time);
    }

    private static long generateStat(Date endDate, int numDays, ChartConfig.ChartBy chartBy) {
        QueryBuilder query = new QueryBuilder();
        // set the min/max to numDays behind startDate
        Date startDate = DateUtil.addToDate(endDate, -numDays, Calendar.DAY_OF_YEAR);
        query.setDateBoundedMinMax(startDate, endDate);
        Cursor data = DBManager.runQuery(query.getAggregateQuery(chartBy));
        long total = DBUtil.getTotalOfAggregates(DBUtil.getAggregatesFromCursor(data));
        data.close();
        return total;
    }
}
