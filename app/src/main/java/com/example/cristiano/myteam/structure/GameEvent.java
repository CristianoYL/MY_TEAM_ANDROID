package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/8.
 */

public class GameEvent {
    public String type,player,time;

    public GameEvent(String type, String player, String time) {
        this.type = type;
        this.player = player;
        this.time = time;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
