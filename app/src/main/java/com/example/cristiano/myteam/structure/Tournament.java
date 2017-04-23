package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/15.
 */

public class Tournament {
    public int id;
    public String name, info;

    public Tournament(int id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
