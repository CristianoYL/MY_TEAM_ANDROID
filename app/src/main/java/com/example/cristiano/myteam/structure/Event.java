package com.example.cristiano.myteam.structure;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/7/2.
 */

public class Event {
    public int id,clubID;
    public String eventTitle, eventAddress, eventTime;
    public double latitude, longitude;

    public Event(int id, int clubID, String eventTitle, String eventAddress, double latitude, double longitude, String eventTime) {
        this.id = id;
        this.clubID = clubID;
        this.eventTitle = eventTitle;
        this.eventAddress = eventAddress;
        this.eventTime = eventTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Event(int id, int clubID, String eventTitle, String eventAddress, String strLatitude, String strLongitude, String eventTime){
        this(id,clubID,eventTitle,eventAddress,Double.parseDouble(strLatitude),Double.parseDouble(strLongitude),eventTime);
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
