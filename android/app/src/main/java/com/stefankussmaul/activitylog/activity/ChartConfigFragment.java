package com.stefankussmaul.activitylog.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.charts.ChartConfig;

import java.util.LinkedList;
import java.util.List;

/**
 * Fragment that provides options for configuring the AnalyticsActivity chart
 */

public class ChartConfigFragment extends Fragment {

    // spinners for selecting chartType and chartBy values
    private Spinner chartTypeSpinner;
    private Spinner chartBySpinner;
    // selected chartType and chartBy settings
    private ChartConfig.ChartType chartType;
    private ChartConfig.ChartBy chartBy;
    private OnConfigChangeListener mListener;

    // interface parent activity must implement
    public interface OnConfigChangeListener {
        void onConfigChanged(ChartConfigFragment chartConfigFragment, ChartConfig config);
    }

    public ChartConfig getConfiguration() {
        return new ChartConfig(chartType, chartBy);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chartType = ChartConfig.ChartType.PIE;
        chartBy = ChartConfig.ChartBy.NUM_SESSIONS;
    }

    @Override // ensures activity implements OnFilterUpdatedListener
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnConfigChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnConfigChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chart_config_layout, container, false); // todo: double check
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        chartTypeSpinner = (Spinner) view.findViewById(R.id.chart_type_spinner);
        List<String> type_choices = new LinkedList<>();
        type_choices.add(getString(R.string.pie_chart));
        type_choices.add(getString(R.string.line_chart));
        // create adapter using the choices
        ArrayAdapter<String> type_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, type_choices);
        chartTypeSpinner.setAdapter(type_adapter);

        chartTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) parent.getItemAtPosition(position);
                if (type.equals(getString(R.string.pie_chart))) {
                    chartType = ChartConfig.ChartType.PIE;
                } else if (type.equals(getString(R.string.line_chart))) {
                    chartType = ChartConfig.ChartType.LINE;
                } else {
                    throw new IllegalArgumentException("Unrecognized Type '" + type + "'. Invalid Spinner option.");
                }
                mListener.onConfigChanged(ChartConfigFragment.this, new ChartConfig(chartType, chartBy));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chartBySpinner = (Spinner) view.findViewById(R.id.chart_by_spinner);
        List<String> by_choices = new LinkedList<>();
        by_choices.add(getString(R.string.num_sessions));
        by_choices.add(getString(R.string.time_spent));
        // create adapter using the choices
        ArrayAdapter<String> by_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, by_choices);
        chartBySpinner.setAdapter(by_adapter);

        chartBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) parent.getItemAtPosition(position);
                if (type.equals(getString(R.string.time_spent))) {
                    chartBy = ChartConfig.ChartBy.TOTAL_DURATION;
                } else if (type.equals(getString(R.string.num_sessions))) {
                    chartBy = ChartConfig.ChartBy.NUM_SESSIONS;
                } else {
                    throw new IllegalArgumentException("Unrecognized Type '" + type + "'. Invalid Spinner option.");
                }
                mListener.onConfigChanged(ChartConfigFragment.this, new ChartConfig(chartType, chartBy));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
