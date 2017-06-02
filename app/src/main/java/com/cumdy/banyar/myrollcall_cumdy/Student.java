package com.cumdy.banyar.myrollcall_cumdy;

/**
 * Created by banyar on 1/15/17.
 */

public class Student {
    private String roll_no, name, month;
    private int uni_total, std_total;

    public Student() {
    }

    public Student(String roll_no, String name, String month, int uni_total, int std_total) {
        this.roll_no = roll_no;
        this.name = name;
        this.month = month;
        this.uni_total = uni_total;
        this.std_total = std_total;
    }

    public String getRoll_no() {

        return roll_no;
    }

    public void setRoll_no(String roll_no) {
        this.roll_no = roll_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getUni_total() {
        return uni_total;
    }

    public void setUni_total(int uni_total) {
        this.uni_total = uni_total;
    }

    public int getStd_total() {
        return std_total;
    }

    public void setStd_total(int std_total) {
        this.std_total = std_total;
    }
}
