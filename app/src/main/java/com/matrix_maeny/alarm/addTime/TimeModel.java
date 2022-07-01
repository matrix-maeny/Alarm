package com.matrix_maeny.alarm.addTime;

public class TimeModel {

    private int hour;
    private int minute;
    private int enabled;
    private int code;

    public TimeModel(int hour, int minute, int enabled) {
        this.hour = hour;
        this.minute = minute;
        this.enabled = enabled;
    }

    public int getCode() {
        return code;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isEnabled() {
        return enabled==1;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTimeString(){
        return hour+" : "+minute;
    }
}
