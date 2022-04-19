package com.frf.app.data;

public class CategoryData {
    private int id;
    private int selected;
    private String title = "";

    public CategoryData(int id, int selected, String title) {
        this.id = id;
        this.selected = selected;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public int getSelected() {
        return selected;
    }

    public String getTitle() {
        return title;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

}
