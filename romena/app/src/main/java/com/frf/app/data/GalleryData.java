package com.frf.app.data;

import java.util.ArrayList;

public class GalleryData {

    private int id;
    private int like;
    private int comments;
    private int numLikes;
    private String title = "";
    private String cat = "";
    private String userType = "";
    private String name = "";
    private String date = "";
    private String avatar = "";
    private String desc = "";
    private String text = "";
    private ArrayList<String> imgs = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();

    public int getId() {
        return id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getComments() {
        return comments;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public String getTitle() {
        return title;
    }

    public String getUserType() {
        return userType;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isLike() {
        return like == 1;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public String getText() {
        return text;
    }

    public String getCat() {
        return cat;
    }
}