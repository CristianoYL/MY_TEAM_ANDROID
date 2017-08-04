package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/6.
 */

public class Club {
    public int id;
    public String name, info;
    public int priority;

    public Club(int id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
