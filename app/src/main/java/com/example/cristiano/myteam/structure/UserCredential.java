package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/1.
 */

public class UserCredential {
    private String username,password;
    public UserCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
