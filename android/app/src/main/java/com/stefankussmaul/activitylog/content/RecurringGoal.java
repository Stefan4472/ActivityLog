package com.stefankussmaul.activitylog.content;

import java.util.List;

/**
 * Created by Stefan on 5/14/2018.
 */

public class RecurringGoal {

    private String query;
    private boolean recurMon, recurTue, recurWed, recurThu, recurFri, recurSat, recurSun;
    private boolean recurWeekly, recurMonthly;
    private String note;

    public RecurringGoal(String query, boolean recurMon, boolean recurTue, boolean recurWed,
                         boolean recurThu, boolean recurFri, boolean recurSat, boolean recurSun,
                         boolean recurWeekly, boolean recurMonthly, String note) {
        this.query = query;
        this.recurMon = recurMon;
        this.recurTue = recurTue;
        this.recurWed = recurWed;
        this.recurThu = recurThu;
        this.recurFri = recurFri;
        this.recurSat = recurSat;
        this.recurSun = recurSun;
        this.recurWeekly = recurWeekly;
        this.recurMonthly = recurMonthly;
        this.note = note;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isRecurMon() {
        return recurMon;
    }

    public void setRecurMon(boolean recurMon) {
        this.recurMon = recurMon;
    }

    public boolean isRecurTue() {
        return recurTue;
    }

    public void setRecurTue(boolean recurTue) {
        this.recurTue = recurTue;
    }

    public boolean isRecurWed() {
        return recurWed;
    }

    public void setRecurWed(boolean recurWed) {
        this.recurWed = recurWed;
    }

    public boolean isRecurThu() {
        return recurThu;
    }

    public void setRecurThu(boolean recurThu) {
        this.recurThu = recurThu;
    }

    public boolean isRecurFri() {
        return recurFri;
    }

    public void setRecurFri(boolean recurFri) {
        this.recurFri = recurFri;
    }

    public boolean isRecurSat() {
        return recurSat;
    }

    public void setRecurSat(boolean recurSat) {
        this.recurSat = recurSat;
    }

    public boolean isRecurSun() {
        return recurSun;
    }

    public void setRecurSun(boolean recurSun) {
        this.recurSun = recurSun;
    }

    public boolean isRecurWeekly() {
        return recurWeekly;
    }

    public void setRecurWeekly(boolean recurWeekly) {
        this.recurWeekly = recurWeekly;
    }

    public boolean isRecurMonthly() {
        return recurMonthly;
    }

    public void setRecurMonthly(boolean recurMonthly) {
        this.recurMonthly = recurMonthly;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
