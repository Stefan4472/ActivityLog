package com.stefankussmaul.activitylog.content;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Stores data required for a log.
 */

public class LogEntry {

    private String activityName;
    private long timeStamp;
    private int duration;

    public LogEntry(String activityName, long timeStamp, int duration) {
        this.activityName = activityName;
        this.timeStamp = timeStamp;
        this.duration = duration;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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
        return activityName.equals(other.getActivityName()) && timeStamp == other.getTimeStamp() &&
                duration == other.getDuration();
    }

    @Override
    public int hashCode() {
        int value = activityName.hashCode();
        value += Long.valueOf(timeStamp).hashCode();
        value += Long.valueOf(duration).hashCode();
        return value;
    }

    private static Date date = new Date();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
    @Override
    public String toString() {
        date.setTime(timeStamp);
        return "LogEntry(" + activityName + ", timeStamp " + dateFormat.format(date) + ", " + duration + "ms)";
    }
}
