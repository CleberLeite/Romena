package com.frf.app.data;

import java.util.ArrayList;

public class TicketsSectorsData {

    private int eventId;
    private int mapId;
    private ArrayList<AvailableSectors> availableSectors = new ArrayList<>();

    public int getEventId() {
        return eventId;
    }

    public int getMapId() {
        return mapId;
    }

    public ArrayList<AvailableSectors> getAvailableSectors() {
        return availableSectors;
    }

    public static class AvailableSectors {

        private int mapAreaID;
        private String mapAreaColor = "";
        private String mapAreaName = "";
        private String mapAreaIdentifier = "";
        private String rootIdentifier = "";
        private String structIdentifier = "";

        public int getMapAreaID() {
            return mapAreaID;
        }

        public String getMapAreaColor() {
            return mapAreaColor;
        }

        public String getMapAreaName() {
            return mapAreaName;
        }

        public String getMapAreaIdentifier() {
            return mapAreaIdentifier;
        }

        public String getRootIdentifier() {
            return rootIdentifier;
        }

        public String getStructIdentifier() {
            return structIdentifier;
        }
    }
}
