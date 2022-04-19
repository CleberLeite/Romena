package com.frf.app.data;

import java.util.ArrayList;

public class NewsData {
    private int id;
    private int fav;
    private String title;
    private String text;
    private String img;
    private String dateHour;
    private String url;
    private String share;
    private String noticeCateg;
    private ArrayList<RelatedData> related;

    public int getId() {
        return id;
    }

    public int getFav() {
        return fav;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getImg() {
        return img;
    }

    public String getDateHour() {
        return dateHour;
    }

    public String getUrl() {
        return url;
    }

    public String getShare() {
        return share;
    }

    public String getNoticeCateg() {
        return noticeCateg;
    }

    public ArrayList<RelatedData> getRelated() {
        return related;
    }
}
