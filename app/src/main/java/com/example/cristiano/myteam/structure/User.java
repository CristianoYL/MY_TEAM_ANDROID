package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/1.
 */

public class User {
    private int id;
    private String email,password;

    public User(int id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password) {
        this.id = 0;
        this.email = email;
        this.password = password;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
