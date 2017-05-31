package com.stefankussmaul.activitylog.content;

/**
 * Essentially stores String, val pair. Made for easier packaging of Activity name with certain
 * calculated value.
 */

public class ActivityAggregate {

    long val;
    String activityName;

    public ActivityAggregate(String activityName, long val) {
        this.activityName = activityName;
        this.val = val;
    }

    public long getVal() {
        return val;
    }

    public String getActivityName() {
        return activityName;
    }

    @Override
    public String toString() {
        return activityName + "(" + val + ")";
    }
}
