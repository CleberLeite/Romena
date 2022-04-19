package com.frf.app.data;

import java.util.ArrayList;

public class CategoriesData {

    private int categoryID = -1;

    private String parentID = "";
    private String code = "";
    private String sortorder = "";
    private String template = "";
    private String status = "";
    private String modified_by = "";
    private String modified_on = "";
    private String oldurl = "";
    private String homepage = "";
    private String countProducts = "";

    private CategorieInfoData info = new CategorieInfoData();

    private ArrayList<CategoriesData> subcats = new ArrayList<>();

    public int getCategoryID() {
        return categoryID;
    }

    public String getParentID() {
        return parentID;
    }

    public String getCode() {
        return code;
    }

    public String getSortorder() {
        return sortorder;
    }

    public String getTemplate() {
        return template;
    }

    public String getStatus() {
        return status;
    }

    public String getModified_by() {
        return modified_by;
    }

    public String getModified_on() {
        return modified_on;
    }

    public String getOldurl() {
        return oldurl;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getCountProducts() {
        return countProducts;
    }

    public CategorieInfoData getInfo() {
        return info;
    }

    public ArrayList<CategoriesData> getSubcats() {
        return subcats;
    }
}
