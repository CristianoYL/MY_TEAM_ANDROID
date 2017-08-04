package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/5/30.
 */

public class PlayerIDList {
    private ArrayList<Integer> playerID = new ArrayList<>();

    public void add(int i){
        this.playerID.add(i);
    }

    public ArrayList<Integer> getPlayerID() {
        return playerID;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
