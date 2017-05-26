package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/17.
 */

public class Squad {
    private String name, role;
    private int tournamentID, clubID, playerID, number;

    public Squad(int tournamentID, int clubID, int playerID, String name, String role, int number) {
        this.tournamentID = tournamentID;
        this.clubID = clubID;
        this.playerID = playerID;
        this.name = name;
        this.role = role;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
