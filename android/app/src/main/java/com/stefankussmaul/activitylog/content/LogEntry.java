package com.stefankussmaul.activitylog.content;

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
}
