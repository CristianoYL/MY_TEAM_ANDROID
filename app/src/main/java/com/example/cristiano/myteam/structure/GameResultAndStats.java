package com.example.cristiano.myteam.structure;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CristianoYL on 5/26/17.
 */

public class GameResultAndStats{
    public int homeID, awayID, tournamentID;
    public String homeName,awayName,tournamentName,date,stage,ftScore,extraScore,penScore,info;
    public ArrayList<String> homeEvents,awayEvents;
    Stats[] stats;

    public GameResultAndStats(Result result, Stats[] stats) {
        this.homeID = result.homeID;
        this.awayID = result.awayID;
        this.tournamentID = result.tournamentID;
        this.homeName = result.homeName;
        this.awayName = result.awayName;
        this.tournamentName = result.tournamentName;
        this.date = result.date;
        this.stage = result.stage;
        this.ftScore = result.ftScore;
        this.extraScore = result.extraScore;
        this.penScore = result.penScore;
        this.info = result.info;
        this.homeEvents = result.homeEvents;
        this.awayEvents = result.awayEvents;
        this.stats = stats;
    }

    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("homeID",this.homeID);
            jsonObject.put("awayID",this.awayID);
            jsonObject.put("tournamentID",this.tournamentID);
            jsonObject.put("homeName",this.homeName);
            jsonObject.put("awayName",this.awayName);
            jsonObject.put("tournamentName",this.tournamentName);
            jsonObject.put("date",this.date);
            jsonObject.put("stage",this.stage);
            jsonObject.put("ftScore",this.ftScore);
            jsonObject.put("extraScore",this.extraScore);
            jsonObject.put("penScore",this.penScore);
            jsonObject.put("info",this.info);

            JSONArray homeEventJson = new JSONArray();
            for ( int i = 0; i < homeEvents.size(); i++ ) {
                String event = homeEvents.get(i);
                JSONObject eventJson = new JSONObject(event);
                homeEventJson.put(i,eventJson);
            }
            jsonObject.put("homeEvents",homeEventJson);

            JSONArray awayEventJson = new JSONArray();
            for ( int i = 0; i < awayEvents.size(); i++ ) {
                String event = awayEvents.get(i);
                Log.d("TO_JSON",awayEvents.get(i));
                JSONObject eventJson = new JSONObject(event);
                awayEventJson.put(i,eventJson);
            }
            jsonObject.put("awayEvents",awayEventJson);

            JSONArray statsJsonArray = new JSONArray();
            for ( int i = 0; i < this.stats.length; i++ ) {
                JSONObject statsJson = new JSONObject();
                statsJson.put("tournamentID",this.stats[i].getTournamentID());
                statsJson.put("clubID",this.stats[i].getClubID());
                statsJson.put("playerID",this.stats[i].getPlayerID());
                statsJson.put("attendance",this.stats[i].attendance);
                statsJson.put("appearance",this.stats[i].appearance);
                statsJson.put("start",this.stats[i].start);
                statsJson.put("goal",this.stats[i].goal);
                statsJson.put("penalty",this.stats[i].penalty);
                statsJson.put("freekick",this.stats[i].freekick);
                statsJson.put("penaltyShootout",this.stats[i].penaltyShootout);
                statsJson.put("penaltyTaken",this.stats[i].penaltyTaken);
                statsJson.put("ownGoal",this.stats[i].ownGoal);
                statsJson.put("header",this.stats[i].header);
                statsJson.put("weakFootGoal",this.stats[i].weakFootGoal);
                statsJson.put("otherGoal",this.stats[i].otherGoal);
                statsJson.put("assist",this.stats[i].assist);
                statsJson.put("yellow",this.stats[i].yellow);
                statsJson.put("red",this.stats[i].red);
                statsJson.put("cleanSheet",this.stats[i].cleanSheet);
                statsJson.put("penaltySaved",this.stats[i].penaltySaved);
                statsJsonArray.put(i,statsJson);
            }
            jsonObject.put("stats",statsJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
//        return new Gson().toJson(this);
    }
}
