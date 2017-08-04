package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/6/5.
 */

public class Chat {
    public int id,tournamentID,clubID,receiverID,senderID;
    public String messageType, messageContent, time, senderName;

    public Chat(int id, int tournamentID, int clubID, int receiverID, int senderID, String senderName, String messageType, String messageContent, String time) {
        this.id = id;
        this.tournamentID = tournamentID;
        this.clubID = clubID;
        this.receiverID = receiverID;
        this.senderID = senderID;
        this.senderName = senderName;
        this.messageType = messageType;
        this.messageContent = messageContent;
        this.time = time;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
