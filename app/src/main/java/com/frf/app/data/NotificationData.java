package com.frf.app.data;

public class NotificationData {

    private static final String TAG = "NotificationData";

    private int id = -1;
    private int idPush;
    private int type = -1;
    private String msg = "";
    private String title = "";
    private String dateHour = "";
    private int visualized = -1;

    public NotificationData(int id, int idPush, int type) {
        this.id = id;
        this.idPush = idPush;
        this.type = type;
    }

    public int getVisualized() {
        return visualized;
    }

    public int getId() {
        return id;
    }

    public int getIdPush() {
        return idPush;
    }

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public String getTitle() {
        return title;
    }

    public String getDateHour() {
        return dateHour;
    }

    public boolean isVisualized() {
        return visualized == 1;
    }
}
