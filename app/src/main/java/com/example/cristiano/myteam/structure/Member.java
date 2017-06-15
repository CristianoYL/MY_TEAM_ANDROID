package com.example.cristiano.myteam.structure;

import com.example.cristiano.myteam.util.Constant;
import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class Member {
    int clubID,playerID,priority;
    String memberSince;
    boolean isActive;


    public Member(int clubID, int playerID, String memberSince, boolean isActive, int priority) {
        this.clubID = clubID;
        this.playerID = playerID;
        this.memberSince = memberSince;
        this.isActive = isActive;
        this.priority =priority;
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

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerId(int playerId) {
        this.playerID = playerId;
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

    public int getPriority() {
        return this.priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
