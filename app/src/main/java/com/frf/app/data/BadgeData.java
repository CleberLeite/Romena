package com.frf.app.data;

public class BadgeData {

    private int id;
    private String img;
    private String name;
    private String desc;
    private int prize;
    private String description;
    private int owned;
    private int hide;
    private int occurrenceNeeded;
    private int ocurrenceActual;

    public BadgeData (String img, String descricao, String nome){
        this.img = img;
        this.description = descricao;
        this.name = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }

    public String getDescricao() {
        return description;
    }

    public int getOwned() {
        return owned;
    }

    public void setOwned(int owned) {
        this.owned = owned;
    }

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }

    public int getOccurrenceNeeded() {
        return occurrenceNeeded;
    }

    public int getOcurrenceActual() {
        return ocurrenceActual;
    }
}
