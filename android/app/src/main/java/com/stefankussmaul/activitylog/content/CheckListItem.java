package com.stefankussmaul.activitylog.content;

/**
 * Created by Stefan on 6/3/2018.
 */

public class CheckListItem {

    private String text;
    private boolean finished;

    public CheckListItem(String text) {
        this(text, false);
    }

    public CheckListItem(String text, boolean finished) {
        this.text = text;
        this.finished = finished;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    // converts contents to a string in a particular format
    // simply text:finished
    public static String toString(CheckListItem item) {
        return item.text + ":" + Boolean.toString(item.finished);
    }

    // creates object by parsing string created in saveToString
    public static CheckListItem parseCheckListItem(String savedString) {
        int colon_index = savedString.indexOf(':');
        return new CheckListItem(savedString.substring(0, colon_index),
                Boolean.parseBoolean(savedString.substring(colon_index + 1)));
    }
}
