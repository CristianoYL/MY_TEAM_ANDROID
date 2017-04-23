package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/17.
 */

public class TournamentRegistration {
    int clubID;
    String name, info;

    public TournamentRegistration(int clubID, String name, String info) {
        this.clubID = clubID;
        this.name = name;
        this.info = info;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
