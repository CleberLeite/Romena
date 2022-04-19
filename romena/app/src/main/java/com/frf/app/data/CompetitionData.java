package com.frf.app.data;

public class CompetitionData {
    public int id;
    public int idTicket = -1;
    public String date;
    public String club1;
    public String clubImg1;
    public String club2;
    public String clubImg2;
    public String category;
    public String stadium;
    public int goalsClub1;
    public int goalsClub2;

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getClub1() {
        return club1;
    }

    public String getClubImg1() {
        return clubImg1;
    }

    public String getClub2() {
        return club2;
    }

    public String getClubImg2() {
        return clubImg2;
    }

    public String getCategory() {
        return category;
    }

    public String getStadium() {
        return stadium;
    }

    public int getGoalsClub1() {
        return goalsClub1;
    }

    public int getGoalsClub2() {
        return goalsClub2;
    }

    public int getIdTicket() {
        return idTicket;
    }

}
