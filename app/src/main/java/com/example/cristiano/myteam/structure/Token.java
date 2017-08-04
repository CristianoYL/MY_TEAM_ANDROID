package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/6/18.
 */

public class Token {

    public int id,playerID;
    public String instanceToken;

    public Token(int id, int playerID, String instanceToken) {
        this.id = id;
        this.playerID = playerID;
        this.instanceToken = instanceToken;
    }

    public Token(int playerID, String instanceToken) {  // used to upload new token to server
        this.id = 0;    // do not need to set id value
        this.playerID = playerID;
        this.instanceToken = instanceToken;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
