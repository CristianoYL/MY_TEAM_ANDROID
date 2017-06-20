package com.example.cristiano.myteam.request;

import com.example.cristiano.myteam.util.Constant;


/**
 * Created by Cristiano on 2017/3/28.
 *
 *  This helper class provide the interfaces for sending the HTTP requests
 *  it will internally call the HttpBackgroundTask, which runs the request in the background
 */

public class RequestHelper {
    public static void sendPostRequest(String url, String jsonData, RequestAction requestAction){
        String[] params = new String[3];
        params[0] = Constant.METHOD_POST;
        params[1] = url;
        params[2] = jsonData;
        new HttpBackgroundTask(requestAction).execute(params);
    }

    public static void sendGetRequest(String url, RequestAction requestAction){
        String[] params = new String[2];
        params[0] = Constant.METHOD_GET;
        params[1] = url;
        new HttpBackgroundTask(requestAction).execute(params);

    }

    public static void sendPutRequest(String url, String jsonData, RequestAction requestAction){
        String[] params = new String[3];
        params[0] = Constant.METHOD_PUT;
        params[1] = url;
        params[2] = jsonData;
        new HttpBackgroundTask(requestAction).execute(params);
    }

    public static void sendDeleteRequest(String url, String jsonData, RequestAction requestAction){
        String[] params = new String[3];
        params[0] = Constant.METHOD_DELETE;
        params[1] = url;
        params[2] = jsonData;
        new HttpBackgroundTask(requestAction).execute(params);
    }

    // sending request with jwt
    public static void sendPostRequest(String url, String jsonData, String jwt, RequestAction requestAction){
        String[] params = new String[4];
        params[0] = Constant.METHOD_POST;
        params[1] = url;
        params[2] = jsonData;
        params[3] = jwt;
        new HttpBackgroundTask(requestAction).execute(params);
    }

    // sending request with jwt
    public static void sendGetRequest(String url, String jwt, RequestAction requestAction){
        String[] params = new String[4];
        params[0] = Constant.METHOD_GET;
        params[1] = url;
        params[2] = null;
        params[3] = jwt;
        new HttpBackgroundTask(requestAction).execute(params);

    }

    // sending request with jwt
    public static void sendPutRequest(String url, String jsonData, String jwt, RequestAction requestAction){
        String[] params = new String[4];
        params[0] = Constant.METHOD_PUT;
        params[1] = url;
        params[2] = jsonData;
        params[3] = jwt;
        new HttpBackgroundTask(requestAction).execute(params);
    }

    // sending request with jwt
    public static void sendDeleteRequest(String url, String jsonData, String jwt, RequestAction requestAction){
        String[] params = new String[4];
        params[0] = Constant.METHOD_DELETE;
        params[1] = url;
        params[2] = jsonData;
        params[3] = jwt;
        new HttpBackgroundTask(requestAction).execute(params);
    }
}
