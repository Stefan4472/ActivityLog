package com.stefankussmaul.activitylog.charts;

/**
 * Defines a configuration for the AnalyticsActivity chart
 */

public class ChartConfig {

    public enum ChartType {
        PIE, LINE;
    }
    public enum ChartBy {
        NUM_SESSIONS, TOTAL_DURATION;
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
