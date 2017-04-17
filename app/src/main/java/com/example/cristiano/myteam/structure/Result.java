package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/4/8.
 */

public class Result {
    private int id;
    public int homeID, awayID, tournamentID;
    public String homeName,awayName,tournamentName,date,stage,ftScore,extraScore,penScore,info;
    public ArrayList<String> homeEvents,awayEvents;

    public Result(int id, int homeID, int awayID, int tournamentID, String homeName, String awayName,
                  String tournamentName, String date, String stage, String ftScore, String extraScore,
                  String penScore, String info, ArrayList<String> homeEvents, ArrayList<String> awayEvents) {
        this.id = id;
        this.homeID = homeID;
        this.awayID = awayID;
        this.tournamentID = tournamentID;
        this.homeName = homeName;
        this.awayName = awayName;
        this.tournamentName = tournamentName;
        this.date = date;
        this.stage = stage;
        this.ftScore = ftScore;
        this.extraScore = extraScore;
        this.penScore = penScore;
        this.info = info;
        this.homeEvents = homeEvents;
        this.awayEvents = awayEvents;
    }

    public Result(int id, int homeID, int awayID, int tournamentID, String homeName, String awayName,
                  String tournamentName, String date, String stage, String ftScore, String extraScore,
                  String penScore, String info) {
        this(id, homeID, awayID, tournamentID, homeName, awayName, tournamentName, date, stage, ftScore,
                extraScore, penScore, info, new ArrayList<String>(),new ArrayList<String>());
    }

    public void addEvent(String eventType, String player, String time, boolean isHomeEvent){
        Event event = new Event(eventType,player,time);
        if ( isHomeEvent ) {
            this.homeEvents.add(event.toJson());
        } else {
            this.awayEvents.add(event.toJson());
        }
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
