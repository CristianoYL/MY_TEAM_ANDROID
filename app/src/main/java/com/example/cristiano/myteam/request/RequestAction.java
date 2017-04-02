package com.example.cristiano.myteam.request;

/**
 * Created by Cristiano on 2017/4/1.
 */

public interface RequestAction {
    void actOnPre();
    void actOnPost(int responseCode,String response);
}
