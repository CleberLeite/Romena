package com.frf.app.models;

public class ErrorModel {
    private int error = -1;
    private String msg;

    public ErrorModel() {
    }

    public ErrorModel(int error, String msg) {
        this.error = error;
        this.msg = msg;
    }



    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
