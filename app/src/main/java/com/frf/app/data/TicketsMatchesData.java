package com.frf.app.data;

import java.util.ArrayList;

public class TicketsMatchesData {

    private int id;
    private String url = "";
    private int eventID;
    private int mapID;
    private String name = "";
    private String title = "";
    private String titleEn = "";
    private String venueDetails = "";
    private String eventDescription = "";
    private String eventDescriptionEn = "";
    private String eventNote = "";
    private String eventNoteEn = "";
    private String eventDateTime = "";
    private String eventDateTimeEnd = "";
    private String portraitImageUrl = "";
    private String landscapeImageUrl = "";
    private String scheduleStatus = "";
    private String availabilityStatus = "";
    private String fbPixelID = "";
    private String stopSellingDate = "";
    private String stopSellingMsg = "";
    private String competitionBreadcrumb = "";
    private ArrayList<Clubs> clubs = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getEventID() {
        return eventID;
    }

    public int getMapID() {
        return mapID;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public String getVenueDetails() {
        return venueDetails;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventDescriptionEn() {
        return eventDescriptionEn;
    }

    public String getEventNote() {
        return eventNote;
    }

    public String getEventNoteEn() {
        return eventNoteEn;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public String getEventDateTimeEnd() {
        return eventDateTimeEnd;
    }

    public String getPortraitImageUrl() {
        return portraitImageUrl;
    }

    public String getLandscapeImageUrl() {
        return landscapeImageUrl;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getFbPixelID() {
        return fbPixelID;
    }

    public String getStopSellingDate() {
        return stopSellingDate;
    }

    public String getStopSellingMsg() {
        return stopSellingMsg;
    }

    public String getCompetitionBreadcrumb() {
        return competitionBreadcrumb;
    }

    public ArrayList<Clubs> getClubs() {
        return clubs;
    }

    public static class Clubs {
        private String name = "";
        private String title = "";
        private String titleEn = "";
        private String bannerUrl = "";

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

        public String getTitleEn() {
            return titleEn;
        }

        public String getBannerUrl() {
            return bannerUrl;
        }
    }
}
