package com.stefankussmaul.activitylog.content;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Stores data required for a log. The Date can be set/retrieved via msSinceEpoch.
 */

public class LogEntry {

    private String activityName;
    private Date date;
    private int duration;

    public LogEntry(String activityName, Date date, int duration) {
        this.activityName = activityName;
        this.date = date;
        this.duration = duration;
    }

    public LogEntry(String activityName, long msSinceEpoch, int duration) {
        this.activityName = activityName;
        this.date = new Date(msSinceEpoch);
        this.duration = duration;
    }

    // returns deep-copied clone of the given LogEntry
    public LogEntry(LogEntry toClone) {
        this(toClone.getActivityName(), toClone.getDateInMS(), toClone.getDuration());
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // returns date as MS since epoch
    public long getDateInMS() {
        return date.getTime();
    }

    // sets date as MS since epoch
    public void setDateMS(long msSinceEpoch) {
        date.setTime(msSinceEpoch);
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
        return activityName.equals(other.getActivityName()) && date.equals(other.getDate()) &&
                duration == other.getDuration();
    }

    @Override
    public int hashCode() {
        int value = activityName.hashCode();
        value += date.hashCode();
        value += Long.valueOf(duration).hashCode();
        return value;
    }

    @Override
    public String toString() {
        return "LogEntry(" + activityName + ", date " + DateUtil.format(date) + ", " + duration + "ms)";
    }
}
