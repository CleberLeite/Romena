package com.frf.app.data;

public class MatchCompetitionsData {
    private int id;
    private String title;
    private String championship;
    private String img;
    private int limit = -1;
    private int margin = -1;

    public MatchCompetitionsData(int id, String title, String championship, String img) {
        this.id = id;
        this.title = title;
        this.championship = championship;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChampionship() {
        return championship;
    }

    public void setChampionship(String championship) {
        this.championship = championship;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }
}
