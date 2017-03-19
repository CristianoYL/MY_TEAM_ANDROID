package com.example.cristiano.myteam.util;
import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/3/17.
 */

public class ParamFactory {
    public static ArrayList<String> params = new ArrayList<>();
    public static void put(String name, String value) {
        params.add(name);
        params.add(value);
    }
    public static String parseParams() {
        StringBuilder parsedParams = new StringBuilder();
        for ( int i = 0; i < params.size(); i += 2 ) {
            parsedParams.append(params.get(i));
            parsedParams.append("=");
            parsedParams.append(params.get(i+1));
            parsedParams.append("&");
        }
        parsedParams.deleteCharAt(parsedParams.length()-1);
        params.clear();
        return parsedParams.toString();
    }
}
