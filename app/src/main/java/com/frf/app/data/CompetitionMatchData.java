package com.frf.app.data;

public class CompetitionMatchData {
    public int id;
    public String date = "";
    public int idClub1;
    public int notify = 0;
    public String club1 = "";
    public String clubImg1 = "";
    public int idClub2;
    public String club2 = "";
    public String clubImg2 = "";
    public String category = "";
    public String championship = "";
    public String stadium = "";
    public String goalsClub1 = "0";
    public String goalsClub2 = "0";
    public GroupData groupData;
    public GameTimeLineData[] timeline;

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getIdClub1() {
        return idClub1;
    }

    public String getClub1() {
        return club1;
    }

    public String getClubImg1() {
        return clubImg1;
    }

    public int getIdClub2() {
        return idClub2;
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

    public String getChampionship() {
        return championship;
    }

    public String getStadium() {
        return stadium;
    }

    public GroupData getGroupData() {
        return groupData;
    }

    public void setGroupData(GroupData groupData) {
        this.groupData = groupData;
    }

    public GameTimeLineData[] getTimeline() {
        return timeline;
    }

    public void setTimeline(GameTimeLineData[] timeline) {
        this.timeline = timeline;
    }

    public String getGoalsClub1() {
        return goalsClub1;
    }

    public String getGoalsClub2() {
        return goalsClub2;
    }

    public int getNotify() {
        return notify;
    }
}
