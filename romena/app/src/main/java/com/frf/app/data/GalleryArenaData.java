package com.frf.app.data;

import java.util.ArrayList;

public class GalleryArenaData {

    private int id;
    private String text = "";
    private int idCategory;
    private String idUser = "";
    private String created_at = "";
    private int likesCount = 0;
    private int commentsCount = 0;
    private int sponsored = 0;
    private String imgUser = "";
    private String color = "";
    private String userName = "";
    private String userType;
    private String cat;
    private int liked;
    private ArrayList<String> images = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public String getImgUser() {
        return imgUser;
    }

    public String getColor() {
        return color;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }

    public String getCat() {
        return cat;
    }

    public int getLiked() {
        return liked;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public boolean isSponsored() {
        return sponsored == 1;
    }
}