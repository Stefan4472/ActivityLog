package com.stefankussmaul.activitylog.content;

import java.util.List;

/**
 * Usually toString utility methods
 */

public class StringUtil {

    public static String aggregatesToString(List<ActivityAggregate> aggregates) {
        String to_str = "";
        int i = 0;
        for (ActivityAggregate a : aggregates) {
            i++;
            to_str += i + ". " + a.toString() + "\n";
        }
        return to_str;
    }


}
