package com.stefankussmaul.activitylog.content;

import java.util.LinkedList;
import java.util.List;

/**
 * Essentially a list of CheckListItems. Includes convenience methods for saving to string and
 * reading from string, as well as packing to and unpacking from a Bundle.
 */

public class CheckList {

    private List<CheckListItem> items;

    public CheckList() {
        this.items = new LinkedList<>();
    }

    public CheckList(List<CheckListItem> items) {
        this.items = items;
    }

    public void addItem(CheckListItem newItem) {
        items.add(newItem);
    }

    public List<CheckListItem> getItems() {
        return items;
    }

    public void setItems(List<CheckListItem> items) {
        this.items = items;
    }

    // leverages CheckListItem toString to put each item on a separate line
    public static String toString(CheckList checkList) {
        String to_string = "";
        for (CheckListItem item : checkList.getItems()) {
            to_string += item.toString() + "\n";
        }
        return to_string;
    }

    // leverages CheckListItem parsing method to retrieve CheckList from string
    public static CheckList parseCheckList(String checkListStr) {
        CheckList parsed = new CheckList();
        String[] lines = checkListStr.split("\\n");
        for (String line : lines) {
            parsed.addItem(CheckListItem.parseCheckListItem(line));
        }
        return parsed;
    }
}
