package com.frf.app.data;

import java.util.ArrayList;

public class PrizesItemData {

    private int id;

    private int type; //Diferenciando de cupom (0) ou arquivo (1)
    private long file_size; //Peso em bytes do arquivo [OPC]
    private String name = "";
    private String url = "";
    private String expirationDate = "";
    private String ticket = "";
    private String file_name = "";
    private String description = "";
    private ArrayList<String> img = new ArrayList<>();
    private int value;
    private int price = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
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

    public ArrayList<String> getImg() {
        return img;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getTicket() {
        return ticket;
    }

    public String getFile_name() {
        return file_name;
    }

    public int getPrice() {
        return price;
    }
}
