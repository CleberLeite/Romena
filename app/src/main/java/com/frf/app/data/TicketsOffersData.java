package com.frf.app.data;

import java.util.ArrayList;

public class TicketsOffersData {

    private int eventId;
    private int mapId;
    private String sectorMapSvgUrl = "";
    private ArrayList<Categories> categories = new ArrayList<>();
    private ArrayList<Offers> offers = new ArrayList<>();

    public int getEventId() {
        return eventId;
    }

    public int getMapId() {
        return mapId;
    }

    public String getSectorMapSvgUrl() {
        return sectorMapSvgUrl;
    }

    public ArrayList<Categories> getCategories() {
        return categories;
    }

    public ArrayList<Offers> getOffers() {
        return offers;
    }

    public static class Categories {
        private String categoryName = "";
        private String structWarning = "";
        private String structInfo = "";
        private String price = "";
        private String priceColorCssClass = "";
        private String priceFormatted = "";
        private String priceRangeMin = "";
        private String priceRangeMax = "";
        private String priceRangeFormatted = "";
        private String rootId = "";
        private String rootIdentifier = "";
        private boolean isActive;
        private boolean canOrder;
        private boolean isSoldOut;
        private int sortIndex;

        public String getCategoryName() {
            return categoryName;
        }

        public String getStructWarning() {
            return structWarning;
        }

        public String getStructInfo() {
            return structInfo;
        }

        public String getPrice() {
            return price;
        }

        public String getPriceColorCssClass() {
            return priceColorCssClass;
        }

        public String getPriceFormatted() {
            return priceFormatted;
        }

        public String getPriceRangeMin() {
            return priceRangeMin;
        }

        public String getPriceRangeMax() {
            return priceRangeMax;
        }

        public String getPriceRangeFormatted() {
            return priceRangeFormatted;
        }

        public String getRootId() {
            return rootId;
        }

        public String getRootIdentifier() {
            return rootIdentifier;
        }

        public boolean isActive() {
            return isActive;
        }

        public boolean isCanOrder() {
            return canOrder;
        }

        public boolean isSoldOut() {
            return isSoldOut;
        }

        public int getSortIndex() {
            return sortIndex;
        }
    }

    public static class Offers {
        private String title = "";
        private String subtitle = "";
        private String categoryName = "";
        private int categorySortIndex;
        private String gateName = "";
        private String zoneName = "";
        private String levelName = "";
        private String sectorName = "";
        private String lodgeName = "";
        private String rowName = "";
        private String bookingsPageUrl = "";
        private boolean isPitchviewEnabled;
        private String pitchviewUrl = "";
        private boolean isPhotosphereEnabled;
        private String photosphereUrl = "";
        private int eventId;
        private boolean isSeatMapEnabled;
        private int mapId;
        private int mapAreaId;
        private String mapAreaName = "";
        private int structId;
        private int structRootId;
        private String structColorCssClass = "";
        private String seatPrice = "";
        private String seatPriceFormatted = "";
        private String structWarning = "";
        private String structInfo = "";
        private int structSeatCount;
        private int seatCountPurchasablePerOrder;
        private int seatCountAvailable;
        private String seatCountAvailableFormatted = "";
        private int seatCountBookedByCurrentOrder;
        private int seatCountPurchasableByCurrentOrder;
        private String structIdentifier = "";
        private String rootIdentifier = "";
        private String mapAreaIdentifier = "";
        private boolean isActive;
        private boolean canOrder;
        private boolean simulateSoldOut;
        private boolean isSoldOut;
        private int sortIndex;
        private boolean hideAvailableSeatCount;

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public int getCategorySortIndex() {
            return categorySortIndex;
        }

        public String getGateName() {
            return gateName;
        }

        public String getZoneName() {
            return zoneName;
        }

        public String getLevelName() {
            return levelName;
        }

        public String getSectorName() {
            return sectorName;
        }

        public String getLodgeName() {
            return lodgeName;
        }

        public String getRowName() {
            return rowName;
        }

        public String getBookingsPageUrl() {
            return bookingsPageUrl;
        }

        public boolean isPitchviewEnabled() {
            return isPitchviewEnabled;
        }

        public String getPitchviewUrl() {
            return pitchviewUrl;
        }

        public boolean isPhotosphereEnabled() {
            return isPhotosphereEnabled;
        }

        public String getPhotosphereUrl() {
            return photosphereUrl;
        }

        public int getEventId() {
            return eventId;
        }

        public boolean isSeatMapEnabled() {
            return isSeatMapEnabled;
        }

        public int getMapId() {
            return mapId;
        }

        public int getMapAreaId() {
            return mapAreaId;
        }

        public String getMapAreaName() {
            return mapAreaName;
        }

        public int getStructId() {
            return structId;
        }

        public int getStructRootId() {
            return structRootId;
        }

        public String getStructColorCssClass() {
            return structColorCssClass;
        }

        public String getSeatPrice() {
            return seatPrice;
        }

        public String getSeatPriceFormatted() {
            return seatPriceFormatted;
        }

        public String getStructWarning() {
            return structWarning;
        }

        public String getStructInfo() {
            return structInfo;
        }

        public int getStructSeatCount() {
            return structSeatCount;
        }

        public int getSeatCountPurchasablePerOrder() {
            return seatCountPurchasablePerOrder;
        }

        public int getSeatCountAvailable() {
            return seatCountAvailable;
        }

        public String getSeatCountAvailableFormatted() {
            return seatCountAvailableFormatted;
        }

        public int getSeatCountBookedByCurrentOrder() {
            return seatCountBookedByCurrentOrder;
        }

        public int getSeatCountPurchasableByCurrentOrder() {
            return seatCountPurchasableByCurrentOrder;
        }

        public String getStructIdentifier() {
            return structIdentifier;
        }

        public String getRootIdentifier() {
            return rootIdentifier;
        }

        public String getMapAreaIdentifier() {
            return mapAreaIdentifier;
        }

        public boolean isActive() {
            return isActive;
        }

        public boolean isCanOrder() {
            return canOrder;
        }

        public boolean isSimulateSoldOut() {
            return simulateSoldOut;
        }

        public boolean isSoldOut() {
            return isSoldOut;
        }

        public int getSortIndex() {
            return sortIndex;
        }

        public boolean isHideAvailableSeatCount() {
            return hideAvailableSeatCount;
        }
    }
}
