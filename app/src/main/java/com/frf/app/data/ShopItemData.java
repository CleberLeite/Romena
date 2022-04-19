package com.frf.app.data;

public class ShopItemData {

    private int id = -1;
    private Integer idCategory;
    private String name;
    private String description;
    private String url;
    private String discont;
    private String title = "";
    private String typeValue = "";
    private int embed;
    private int value = -1;
    private int tipo = -1;
    private int idCupom = -1;
    private int redeemed = 0;
    private int installmentAmount;
    private int numberOfInstallments;
    private String img;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Integer idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDiscont() {
        return discont;
    }

    public void setDiscont(String discont) {
        this.discont = discont;
    }

    public int getEmbed() {
        return embed;
    }

    public void setEmbed(int embed) {
        this.embed = embed;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(int installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public int getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(int numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public int getRedeemed() {
        return redeemed;
    }

    public boolean isRedeemed() {
        return redeemed == 1;
    }

    public int getIdCupom() {
        return idCupom;
    }

    public int getTipo() {
        return tipo;
    }

    public String getTypeValue() {
        return typeValue;
    }
}
