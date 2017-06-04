package com.stefankussmaul.activitylog.charts;

import com.github.mikephil.charting.formatter.IValueFormatter;

/**
 * Defines a configuration for the AnalyticsActivity chart
 */

public class ChartConfig {

    public enum ChartType {
        PIE, LINE;
    }
    // possibilities for what data can be charted. Stores the correct ValueFormatter to be used with
    public enum ChartBy {
        NUM_SESSIONS(new SessionsValueFormatter()), TIME_SPENT(new DurationValueFormatter());

        private IValueFormatter formatter;

        ChartBy(IValueFormatter valFormatter) {
            formatter = valFormatter;
        }

        public IValueFormatter getFormatter() {
            return formatter;
        }
    }

    private ChartType chartType;
    private ChartBy chartBy;

    public ChartConfig(ChartType chartType, ChartBy chartBy) {
        this.chartType = chartType;
        this.chartBy = chartBy;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public ChartBy getChartBy() {
        return chartBy;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public void setChartBy(ChartBy chartBy) {
        this.chartBy = chartBy;
    }

    @Override
    public String toString() {
        return "[" + chartBy + "," + chartType + "]";
    }
}
