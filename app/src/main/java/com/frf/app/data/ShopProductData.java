package com.frf.app.data;

import org.json.JSONObject;

import java.util.ArrayList;

public class ShopProductData {

    private int productID = -1;
    private int adaos = -1;
    private int stockno = -1;

    private String masterID = "";
    private String brandID = "";
    private String brand = "";
    private String categoryID = "";
    private String category = "";
    private String stockID = "";
    private String stock = "";
    private String code = "";
    private String master_code = "";
    private String supp_code = "";
    private String publishID = "";
    private String price = "";
    private String oldprice = "";
    private String save_percent = "";
    private String acquisition_price = "";
    private String currencyID = "";
    private String vat = "";
    private String discount = "";
    private String sortorder = "";
    private String publicsite = "";
    private String supplierID = "";
    private String datalansare = "";
    private String pic = "";
    private String external_href = "";
    private String internalnote = "";
    private String homepage = "";
    private String nohomepage = "";
    private String status = "";
    private String imported = "";
    private String completion = "";
    private String oldurl = "";
    private String stars = "";
    private String reviews = "";
    private String emag_price = "";
    private String emag_oldprice = "";
    private String barcode = "";
    private String barcodestandard = "";
    private String variant_order = "";
    private String delivery_days = "";
    private JSONObject promo = new JSONObject();
    private String product_type = "";
    private String min_qty = "";
    private String um = "";
    private String options = "";

    private boolean liked = false;
    private boolean buyable = true;

    private Info info = new Info();
    private Prices prices = new Prices();

    private ArrayList<CartegoryListData> categoryList = new ArrayList<>();
    private ArrayList<Pics> pics = new ArrayList<>();
    private ArrayList<Param> param = new ArrayList<>();
    private ArrayList<Sections> sections = new ArrayList<>();

    public int getProductID() {
        return productID;
    }

    public int getAdaos() {
        return adaos;
    }

    public int getStockno() {
        return stockno;
    }

    public String getMasterID() {
        return masterID;
    }

    public String getBrandID() {
        return brandID;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getCategory() {
        return category;
    }

    public String getStockID() {
        return stockID;
    }

    public String getStock() {
        return stock;
    }

    public String getCode() {
        return code;
    }

    public String getMaster_code() {
        return master_code;
    }

    public String getSupp_code() {
        return supp_code;
    }

    public String getPublishID() {
        return publishID;
    }

    public String getPrice() {
        return price;
    }

    public String getOldprice() {
        return oldprice;
    }

    public String getSave_percent() {
        return save_percent;
    }

    public String getAcquisition_price() {
        return acquisition_price;
    }

    public String getCurrencyID() {
        return currencyID;
    }

    public String getVat() {
        return vat;
    }

    public String getDiscount() {
        return discount;
    }

    public String getSortorder() {
        return sortorder;
    }

    public String getPublicsite() {
        return publicsite;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public String getDatalansare() {
        return datalansare;
    }

    public String getPic() {
        return pic;
    }

    public String getExternal_href() {
        return external_href;
    }

    public String getInternalnote() {
        return internalnote;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getNohomepage() {
        return nohomepage;
    }

    public String getStatus() {
        return status;
    }

    public String getImported() {
        return imported;
    }

    public String getCompletion() {
        return completion;
    }

    public String getOldurl() {
        return oldurl;
    }

    public String getStars() {
        return stars;
    }

    public String getReviews() {
        return reviews;
    }

    public String getEmag_price() {
        return emag_price;
    }

    public String getEmag_oldprice() {
        return emag_oldprice;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getBarcodestandard() {
        return barcodestandard;
    }

    public String getVariant_order() {
        return variant_order;
    }

    public String getDelivery_days() {
        return delivery_days;
    }

    public JSONObject getPromo() {
        return promo;
    }

    public String getProduct_type() {
        return product_type;
    }

    public String getMin_qty() {
        return min_qty;
    }

    public String getUm() {
        return um;
    }

    public String getOptions() {
        return options;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isBuyable() {
        return buyable;
    }

    public ArrayList<CartegoryListData> getCategoryList() {
        return categoryList;
    }

    public Info getInfo() {
        return info;
    }

    public ArrayList<Pics> getPics() {
        return pics;
    }

    public ArrayList<Param> getParam() {
        return param;
    }

    public ArrayList<Sections> getSections() {
        return sections;
    }

    public Prices getPrices() {
        return prices;
    }

    static class CartegoryListData {

        private int categoryID = -1;

        private String category = "";
        private String parentID = "";
        private String sortorder = "";
        private String code = "";

        public int getCategoryID() {
            return categoryID;
        }

        public String getCategory() {
            return category;
        }

        public String getParentID() {
            return parentID;
        }

        public String getSortorder() {
            return sortorder;
        }

        public String getCode() {
            return code;
        }
    }

    public static class Info {

        private int id = -1;

        private String productID = "";
        private String product = "";
        private String master_product = "";
        private String url = "";
        private String description = "";
        private String commercial = "";
        private String productemag = "";
        private String descriptionemag = "";
        private String metatitle = "";
        private String metakeywords = "";
        private String metadescription = "";
        private String canonical = "";
        private String robots = "";
        private String warranty = "";
        private String subtitle = "";

        public int getId() {
            return id;
        }

        public String getProductID() {
            return productID;
        }

        public String getProduct() {
            return product;
        }

        public String getMaster_product() {
            return master_product;
        }

        public String getUrl() {
            return url;
        }

        public String getDescription() {
            return description;
        }

        public String getCommercial() {
            return commercial;
        }

        public String getProductemag() {
            return productemag;
        }

        public String getDescriptionemag() {
            return descriptionemag;
        }

        public String getMetatitle() {
            return metatitle;
        }

        public String getMetakeywords() {
            return metakeywords;
        }

        public String getMetadescription() {
            return metadescription;
        }

        public String getCanonical() {
            return canonical;
        }

        public String getRobots() {
            return robots;
        }

        public String getWarranty() {
            return warranty;
        }

        public String getSubtitle() {
            return subtitle;
        }
    }

    static class Pics {

        private String mini = "";
        private String thumb = "";
        private String original = "";

        public String getMini() {
            return mini;
        }

        public String getThumb() {
            return thumb;
        }

        public String getOriginal() {
            return original;
        }
    }

    static class Param {

        private String paramid = "";
        private String value = "";
        private String variant = "";
        private String picture = "";
        private String type = "";
        private String show_in_listing = "";
        private String unitmeasure = "";
        private String sortorder = "";

        public String getParamid() {
            return paramid;
        }

        public String getValue() {
            return value;
        }

        public String getVariant() {
            return variant;
        }

        public String getPicture() {
            return picture;
        }

        public String getType() {
            return type;
        }

        public String getShow_in_listing() {
            return show_in_listing;
        }

        public String getUnitmeasure() {
            return unitmeasure;
        }

        public String getSortorder() {
            return sortorder;
        }
    }

    static class Sections {
        private String id = "";
        private String sectionID = "";
        private String title = "";
        private String content = "";

        public String getId() {
            return id;
        }

        public String getSectionID() {
            return sectionID;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

    static class Prices {
        private String priceid = "";
        private String productid = "";
        private String vendorID = "";
        private String pcid = "";
        private String price = "";
        private String oldprice = "";
        private String distribution_price = "";
        private String min_qty = "";
        private String max_qty = "";
        private String updated = "";
        private String from_erp = "";
        private String adaos = "";
        private String discount = "";
        private String adaosRuleID = "";
        private String discountRuleID = "";
        private String groupID = "";
        private String companyid = "";

        public String getPriceid() {
            return priceid;
        }

        public String getProductid() {
            return productid;
        }

        public String getVendorID() {
            return vendorID;
        }

        public String getPcid() {
            return pcid;
        }

        public String getPrice() {
            return price;
        }

        public String getOldprice() {
            return oldprice;
        }

        public String getDistribution_price() {
            return distribution_price;
        }

        public String getMin_qty() {
            return min_qty;
        }

        public String getMax_qty() {
            return max_qty;
        }

        public String getUpdated() {
            return updated;
        }

        public String getFrom_erp() {
            return from_erp;
        }

        public String getAdaos() {
            return adaos;
        }

        public String getDiscount() {
            return discount;
        }

        public String getAdaosRuleID() {
            return adaosRuleID;
        }

        public String getDiscountRuleID() {
            return discountRuleID;
        }

        public String getGroupID() {
            return groupID;
        }

        public String getCompanyid() {
            return companyid;
        }
    }

}

