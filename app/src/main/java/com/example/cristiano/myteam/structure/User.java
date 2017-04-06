package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/1.
 */

public class User {
    private String email,password;
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
