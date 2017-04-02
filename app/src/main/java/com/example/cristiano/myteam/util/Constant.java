package com.example.cristiano.myteam.util;

/**
 * Created by Cristiano on 2017/3/16.
 */

public class Constant {
    public static final String URL = "http://192.168.1.9:5000";
    public static final String URL_LOGIN = URL + "/auth";
    public static final String URL_REGISTER = URL + "/user";
    public static final String URL_GET_PLAYER = URL + "/player/";

    public static final String SERVER_CHARSET = "UTF-8";

    public static final int CODE_OK = 200;
    public static final int CODE_CREATED = 201;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;

    public static final String KEY_DESC = "description";
    public static final String KEY_MSG = "message";

    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    public static final String PLAYER_INFO = "playerInfo";
    public static final String PLAYER_SELECTED_STATS = "selectedStats";
    public static final String PLAYER_EMAIL = "email";
    public static final String PLAYER_DISPLAY_NAME = "displayName";
    public static final String PLAYER_CLUB = "club";
    public static final String PLAYER_AGE = "age";
    public static final String PLAYER_WEIGHT = "Weight";
    public static final String PLAYER_HEIGHT = "Height";
    public static final String PLAYER_FOOT = "Strong Foot";
    public static final String PLAYER_ROLE = "Role";
    public static final String PLAYER_POSITION = "Position";

    public static final String STATS_ATTENDANCE = "ATT";
    public static final String STATS_APPEARANCE = "APP";
    public static final String STATS_START = "START";
    public static final String STATS_MVP = "MVP";
    public static final String STATS_WIN = "WIN";
    public static final String STATS_DRAW = "DRAW";
    public static final String STATS_LOSS = "LOSS";
    public static final String STATS_GOAL = "GOAL";
    public static final String STATS_ASSIST = "ASSIST";
    public static final String STATS_PENALTY = "PEN";
    public static final String STATS_OG = "OG";
    public static final String STATS_YELLOW = "YELLOW";
    public static final String STATS_RED = "RED";

    public static final String ROLE_PLAYER = "Player";
    public static final String ROLE_MANAGER = "Manager";

    public static final String REQUEST_REGISTER = "Register";
    public static final String REQUEST_LOGIN = "Login";
}
