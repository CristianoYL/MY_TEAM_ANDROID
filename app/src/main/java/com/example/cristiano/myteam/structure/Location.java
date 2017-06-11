package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/6/11.
 */

public class Location {
    int clubID, playerID;
    String latitude, longitude, lastUpdate;

    public Location(int clubID, int playerID, String latitude, String longitude, String lastUpdate) {
        this.clubID = clubID;
        this.playerID = playerID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdate = lastUpdate;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
