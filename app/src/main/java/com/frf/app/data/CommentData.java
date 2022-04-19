package com.frf.app.data;

import java.util.ArrayList;

public class CommentData {

    private int id;
    private String text;
    private String idUser;
    private String userName;
    private String userType;
    private String created_at;
    private String userImg;
    private ArrayList<RepliesData> replies  = new ArrayList<>();;
    private int moreReplies;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public ArrayList<RepliesData> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<RepliesData> replies) {
        this.replies = replies;
    }

    public int getMoreReplies() {
        return moreReplies;
    }

    public void setMoreReplies(int moreReplies) {
        this.moreReplies = moreReplies;
    }
}
