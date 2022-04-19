package com.frf.app.data;

public class EndSessionData {

    private String time;
    private int sId;

    public EndSessionData(String time, int sId) {
        this.time = time;
        this.sId = sId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getsId() {
        return sId;
    }

    public void setsId(int sId) {
        this.sId = sId;
    }
}
