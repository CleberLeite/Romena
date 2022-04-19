package com.frf.app.data;

public class Answers {

    int id;
    String title;
    String img;
    int correct = 0;

    public Answers(int id, String title, int correct, String img) {
        this.id = id;
        this.title = title;
        this.correct = correct;
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

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
