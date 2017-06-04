package com.stefankussmaul.activitylog.content;

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

    public void start() {
        if (!timing) {
            lastStart = System.currentTimeMillis();
            timing = true;
        }
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

    public long getTimedMs() {
        if (timing) {
            return recordedMs + System.currentTimeMillis() - lastStart;
        } else {
            return recordedMs;
        }
    }

    public int getHoursField() {
        return (int) getTimedMs() / 3_600_000;
    }

    public int getMinutesField() {
        long timed_ms = getTimedMs();
        return (int) ((timed_ms % 3_600_000) / 60_000);
    }

    public int getSecondsField() {
        long timed_ms = getTimedMs();
        return (int) (((timed_ms % 3_600_000) % 60_000) / 1_000);
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
