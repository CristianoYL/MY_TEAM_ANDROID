package com.example.cristiano.myteam.util;

/**
 * Created by Cristiano on 2017/3/16.
 */

public class Constant {
//    public static final String URL = "https://my-team-rest-api.herokuapp.com";
    public static final String URL = "http://192.168.1.3:5000";
    public static final String URL_LOGIN = URL + "/auth";
    public static final String URL_REGISTER = URL + "/user";
    public static final String URL_GET_PLAYER = URL + "/player/";
    public static final String URL_POST_PLAYER = URL + "/player/";
    public static final String URL_GET_PLAYER_CLUBS = URL + "/teamsheet/player/";

    public static final String SERVER_CHARSET = "UTF-8";

    public static final String TABLE_USER = "user";
    public static final String TABLE_PLAYER = "player";
    public static final String TABLE_CLUB = "club";
    public static final String TABLE_TOURNAMENT = "tournament";
    public static final String TABLE_SQUAD = "squad";
    public static final String TABLE_TEAMSHEET = "teamsheet";
    public static final String TABLE_STATS = "stats";

    public static final String KEY_USER_PREF = "user preference";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER = "remember username";
    public static final String KEY_AUTO_LOGIN = "auto login";
    public static final String KEY_DESC = "description";
    public static final String KEY_MSG = "message";

    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    public static final String PLAYER_INFO = "playerInfo";
    public static final String PLAYER_SELECTED_STATS = "selectedStats";

    public static final String PLAYER_ID = "id";
    public static final String PLAYER_EMAIL = "email";
    public static final String PLAYER_FIRST_NAME = "firstName";
    public static final String PLAYER_LAST_NAME = "lastName";
    public static final String PLAYER_DISPLAY_NAME = "displayName";
    public static final String PLAYER_CLUB = "club";
    public static final String PLAYER_AGE = "age";
    public static final String PLAYER_WEIGHT = "weight";
    public static final String PLAYER_HEIGHT = "height";
    public static final String PLAYER_FOOT = "leftFooted";
    public static final String PLAYER_ROLE = "role";
    public static final String PLAYER_PHONE = "phone";
    public static final String PLAYER_AVATAR = "avatar";

    public static final String CLUB_ID = "id";
    public static final String CLUB_NAME = "name";
    public static final String CLUB_INFO = "info";

    public static final String STRONG_FOOT = "Strong Foot";
    public static final String PLAYER_POSITION = "position";


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

    public static final String POSITION_DEF = "Defender";
    public static final String POSITION_MID = "Midfielder";
    public static final String POSITION_FWD = "Forward";
    public static final String POSITION_GK = "Goalkeeper";

    public static final String[] roles = {Constant.ROLE_PLAYER,Constant.ROLE_MANAGER};

    public static final String[] positions = {
            Constant.POSITION_DEF,
            Constant.POSITION_MID,
            Constant.POSITION_FWD,
            Constant.POSITION_GK    };
}
