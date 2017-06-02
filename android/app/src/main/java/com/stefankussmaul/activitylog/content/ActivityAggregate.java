package com.stefankussmaul.activitylog.content;

import java.util.Date;

/**
 * Essentially stores String, val pair. Made for easier packaging of Activity name with certain
 * calculated value.
 */

public class ActivityAggregate {

    private long val;
    private String activityName;
    private Date date;

    public ActivityAggregate(String activityName, long val) {
        this.activityName = activityName;
        this.val = val;
    }

    public ActivityAggregate(String activityName, long val, Date date) {
        this.activityName = activityName;
        this.val = val;
        this.date = date;
    }

    public void setName(String activityName) {
        this.activityName = activityName;
    }

    public void addToVal(long add) {
        val += add;
    }

    public long getVal() {
        return val;
    }

    public String getActivityName() {
        return activityName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return activityName + "(" + val + ")";
    }
}
