package com.example.cristiano.myteam.structure;

/**
 * Created by Cristiano on 2017/4/17.
 */

public class Squad {
    private String name, role;
    private int number;

    public Squad(String name, String role, int number) {
        this.name = name;
        this.role = role;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
