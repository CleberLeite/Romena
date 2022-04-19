package com.frf.app.models.user;

import com.frf.app.utils.UTILS;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {

    private String id = "";
    private String token="";
    private String email="";
    private String dn="";
    private String name="";
    private String nickname="";
    private String dtCreated="";
    private String gender="";
    private String apns="";
    private int sId = -1;
    private String img="";
    private String userType="";
    private String city="";
    private int notifications=0;
    private int coins = 0;
    private int cardId = 0;
    private int idMsg = 0;
    private int rank = 0;

    private SSOInfo sso_info = new SSOInfo();

    public UserModel(){}

    public UserModel(String nome,String email){
        this.name=nome;
        this.email=email;
    }

    public String getId() {
        return id;
    }

    public int getIdMsg() {
        return idMsg;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setIdMsg(int idMsg) {
        this.idMsg = idMsg;
    }

    public String getApns() {
        return apns;
    }

    public void setApns(String apns) {
        this.apns = apns;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getName() {
        return nickname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(String dtCreated) {
        this.dtCreated = dtCreated;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getsId() {
        return sId;
    }

    public void setsId(int sId) {
        this.sId = sId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getImg(){
        return this.img;
    }

    public String getNickname() {
        return nickname;
    }

    public void setImg(String image){
        if(image.contains("http:")){
            image=image.replace("http:","https:");
        }
        this.img=image;
    }


    public SSOInfo getSso_info() {
        return sso_info;
    }

    public JSONObject toJSON(){
        JSONObject json=new JSONObject();

        try{

            json.put("cardId", getId());
            json.put("id", getCardId());
            json.put("notifications",getNotifications());
            json.put("token",getToken());
            json.put("gender",getGender());
            json.put("userType",getUserType());
            json.put("name",getName());
            json.put("email",getEmail());
            json.put("city",getCity());
            json.put("img",getImg());
            json.put("dn",getDn());
            json.put("dtCreated",getDtCreated());
            json.put("coins",getCoins());
            json.put("sId",getsId());
        }catch(JSONException e){
            UTILS.DebugLog("UserData",e);
        }

        return json;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", dn='" + dn + '\'' +
                ", name='" + name + '\'' +
                ", dtCreated='" + dtCreated + '\'' +
                ", gender='" + gender + '\'' +
                ", sId=" + sId +
                ", img='" + img + '\'' +
                ", userType='" + userType + '\'' +
                ", coins=" + coins +
                '}';
    }

    public int getRank() {
        return rank;
    }

    public static class SSOInfo {
        private String real_name = "";
        private String gender = "";
        private String birthdate = "";
        private String city = "";

        public String getReal_name() {
            return real_name;
        }

        public String getGender() {
            return gender;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public String getCity() {
            return city;
        }
    }
}