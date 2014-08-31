package com.github.andrdev.sc2gamer.model;


public class Game {
    private Team mTeam1 = new Team();
    private Team mTeam2 = new Team();
    private String mTime;
    private String mBo;

    public String getTeam1Name() {
        return mTeam1.mName;
    }

    public void setTeam1Name(String team1Name) {
        mTeam1.mName = team1Name;
    }

    public String getTeam1Logo() {
        return mTeam1.mLogo;
    }

    public void setTeam1Logo(String team1Logo) {
        mTeam1.mLogo = team1Logo;
    }

    public String getTeam2Name() {
        return mTeam2.mName;
    }

    public void setTeam2Name(String team2Name) {
        mTeam2.mName = team2Name;
    }

    public String getTeam2Logo() {
        return mTeam2.mLogo;
    }

    public void setTeam2Logo(String team2Logo) {
        mTeam2.mLogo = team2Logo;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getBo() {
        return mBo;
    }

    public void setBo(String bo) {
        mBo = bo;
    }

    private class Team {
        private String mName;
        private String mLogo;
    }
}
