package com.stefankussmaul.activitylog.content;

import static com.stefankussmaul.activitylog.content.DateUtil.HOUR_MS;
import static com.stefankussmaul.activitylog.content.DateUtil.MINUTE_MS;
import static com.stefankussmaul.activitylog.content.DateUtil.SECOND_MS;

/**
 *
 */

public class MsTimer {

    // whether timer is currently keeping track of time
    private boolean timing;
    // last time timer was started, in ms since Epoch
    private long lastStart;
    // sum of milliseconds this timer has kept track of
    private long recordedMs;
    // the number of milliseconds timer is counting to (if COUNT_DOWN)
    private long goalMs;
    // current mode timer is in
    private Mode curMode = Mode.COUNT_UP;

    // modes timer is capable of. Changing will require updates of methods in this class.
    public enum Mode {
        COUNT_UP, COUNT_DOWN;
    }

    public void setCountUp() {
        curMode = Mode.COUNT_UP;
    }

    public void pause() {
        if (timing) {
            recordedMs += System.currentTimeMillis() - lastStart;
            timing = false;
        }
    }

    public void reset() {
        recordedMs = 0;
        timing = false;
    }

    public boolean isTiming() {
        return timing;
    }

    public void setCountDown(long goalMs) {
        curMode = Mode.COUNT_DOWN;
        this.goalMs = goalMs;
    }

    public void start() {
        if (!timing) {
            lastStart = System.currentTimeMillis();
            timing = true;
        }
    }

    public long getTimedMs() {
        if (timing) {
            return recordedMs + System.currentTimeMillis() - lastStart;
        } else {
            return recordedMs;
        }
    }

    public int getHoursField() {
        if (curMode == Mode.COUNT_UP) {
            return (int) getTimedMs() / HOUR_MS;
        } else {
            return (int) (goalMs - getTimedMs()) / HOUR_MS;
        }
    }

    public int getMinutesField() {
        long timed_ms = getTimedMs();
        if (curMode == Mode.COUNT_UP) {
            return (int) ((timed_ms % HOUR_MS) / MINUTE_MS);
        } else {
            return (int) (((goalMs - timed_ms) % HOUR_MS) / MINUTE_MS);
        }
    }

    public int getSecondsField() {
        long timed_ms = getTimedMs();
        if (curMode == Mode.COUNT_UP) {
            return (int) (((timed_ms % HOUR_MS) % MINUTE_MS) / SECOND_MS);
        } else {
            return (int) ((((goalMs - timed_ms) % HOUR_MS) % MINUTE_MS) / SECOND_MS);

        }
    }

    // formats the value of a field into a String that can be displayed on a timer
    public static String formatField(int fieldVal) {
        if (fieldVal == 0) {
            return "00";
        } else if (fieldVal < 10) {
            return  "0" + fieldVal;
        } else {
            return Integer.toString(fieldVal);
        }
    }
}
