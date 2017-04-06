package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Cristiano on 2017/4/2.
 */

public class Player implements Serializable{
    public int id;
    private String email;
    public String firstName;
    public String lastName;
    public String displayName;
    public String role;
    public String phone;
    public int age;
    public float weight;
    public float height;
    public boolean leftFooted;
    public int avatar;

    public Player(int id, String email, String firstName, String lastName, String displayName, String role,
                  String phone, int age, float weight, float height, boolean leftFooted, int avatar) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.role = role;
        this.phone = phone;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.leftFooted = leftFooted;
        this.avatar = avatar;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
