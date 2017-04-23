package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class Teamsheet {
    int clubID,playerId;
    String memberSince;
    boolean isActive, isAdmin;

    public Teamsheet(int clubID, int playerId, String memberSince, boolean isActive, boolean isAdmin) {
        this.clubID = clubID;
        this.playerId = playerId;
        this.memberSince = memberSince;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
