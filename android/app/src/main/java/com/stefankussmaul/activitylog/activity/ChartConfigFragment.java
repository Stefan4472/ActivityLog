package com.stefankussmaul.activitylog.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.QueryBuilder;

import java.util.Calendar;

/**
 * Fragment that provides options for configuring the AnalyticsActivity chart
 */

public class ChartConfigFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override // ensures activity implements OnFilterUpdatedListener
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (LogFilterFragment.OnFilterUpdatedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnFilterUpdatedListener");
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chart_config_layout, container, false); // todo: double check
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
    }
}
