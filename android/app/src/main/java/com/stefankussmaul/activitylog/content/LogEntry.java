package com.stefankussmaul.activitylog.content;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Stores data required for a log.
 */

public class LogEntry {

    private String activityName;
    private long date;
    private int duration;

    public LogEntry(String activityName, long date, int duration) {
        this.activityName = activityName;
        this.date = date;
        this.duration = duration;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == null || o == null) {
            return false;
        } else if (this == o) {
            return true;
        }
        final LogEntry other = (LogEntry) o;
        return activityName.equals(other.getActivityName()) && date == other.getDate() &&
                duration == other.getDuration();
    }

    @Override
    public int hashCode() {
        int value = activityName.hashCode();
        value += Long.valueOf(date).hashCode();
        value += Long.valueOf(duration).hashCode();
        return value;
    }

    private static Date formatHelper = new Date();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
    @Override
    public String toString() {
        formatHelper.setTime(date);
        return "LogEntry(" + activityName + ", date " + dateFormat.format(formatHelper) + ", " + duration + "ms)";
    }
}
