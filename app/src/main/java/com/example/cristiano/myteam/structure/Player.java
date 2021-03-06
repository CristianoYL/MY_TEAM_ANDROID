package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Cristiano on 2017/4/2.
 */

public class Player {
    private int id;
    private int userID;
    private String firstName;
    private String lastName;
    private String displayName;
    private String role;
    private String phone;
    private int age;
    private float weight;
    private float height;
    private boolean leftFooted;
    private String avatar;

    public Player(int id, int userID, String firstName, String lastName, String displayName, String role,
                  String phone, int age, float weight, float height, boolean leftFooted, String avatar) {
        this.id = id;
        this.userID = userID;
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

    public int getId() {
        return id;
    }

    public int getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public int getAge() {
        return age;
    }

    public float getWeight() {
        return weight;
    }

    public float getHeight() {
        return height;
    }

    public boolean isLeftFooted() {
        return leftFooted;
    }

    public String getAvatar() {
        return avatar;
    }
}
