package com.example.cristiano.myteam.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Cristiano on 2017/3/16.
 *
 * this class maintains most of the constant values of the application
 */

public class Constant {

    public static final String SERVER_CHARSET = "UTF-8";

    public static final String MSG_TIME_OUT = "{ \"" + Constant.KEY_MSG + "\":\"Connection Timeout\"}";
    public static final int CONN_TIME_OUT = 10000;
    public static final int READ_TIME_OUT = 10000;

    public static final String CLUB_LIST = "clubs";
    public static final String RESULT_LIST = "results";
    public static final String MEMBER_LIST = "members";
    public static final String TOURNAMENT_LIST = "tournaments";

    public static final String KEY_USER_PREF = "user preference";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DESC = "description";
    public static final String KEY_MSG = "message";
    public static final String KEY_CLUB_ID = "clubID";
    public static final String KEY_PLAYER_ID = "playerID";
    public static final String KEY_PLAYER = "player";
    public static final String KEY_TOURNAMENT_ID= "tournamentID";
    public static final String KEY_CLUB_INFO = "clubInfo";
    public static final String KEY_GAME_PERFORMANCE = "gamePerformance";
    public static final String KEY_CONTEXT = "context";


    public static final String INTENT_NEW_MESSAGE = "new message";

    // user preference keys
    public static final String PREF_REMEMBER_USERNAME = "remember username";
    public static final String PREF_AUTO_LOGIN = "auto login";
    public static final String CACHE_PLAYER_ID = "my player ID";
    public static final String CACHE_DEFAULT_CLUB_ID = "my default club ID";
    public static final String CACHE_IDD_TOKEN = "cached token";

    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    public static DateFormat getServerDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
    public static final DateFormat MARKER_DATE_FORMAT = new SimpleDateFormat("HH:mm MMM-dd", Locale.getDefault());
    public static final DateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MMM dd HH:mm",Locale.getDefault());
    public static final DateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String MESSAGE_TYPE_IMAGE = "image";
    public static final String MESSAGE_TYPE_VIDEO = "video";

    public static final String PLAYER_INFO_PLAYER = "player";
    public static final String PLAYER_INFO_CLUBS = "clubs";
    public static final String PLAYER_INFO_TOTAL_STATS = "totalStats";

    public static final String PERFORMANCE_WIN = "win";
    public static final String PERFORMANCE_DRAW = "draw";
    public static final String PERFORMANCE_LOSS = "loss";
    public static final String PERFORMANCE_GOALS_CONCEDED = "goalsConceded";


    // table player
    public static final String TABLE_PLAYER = "player";
    // player table column
    public static final String PLAYER_ID = "id";
    public static final String PLAYER_USER_ID = "userID";
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

    // table user
    public static final String TABLE_USER = "user";
    // user columns
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_ACCESS_TOKEN = "access_token";

    // table club
    public static final String TABLE_CLUB = "club";
    // club table columns
    public static final String CLUB_ID = "id";
    public static final String CLUB_NAME = "name";
    public static final String CLUB_INFO = "info";

    //  table stats
    public static final String TABLE_STATS = "stats";
    //  stats table columns
    public static final String STATS_T_ID = "tournamentID";
    public static final String STATS_C_ID = "clubID";
    public static final String STATS_P_ID = "playerID";
    public static final String STATS_ATTENDANCE = "attendance";
    public static final String STATS_APPEARANCE = "appearance";
    public static final String STATS_START = "start";
    public static final String STATS_GOAL = "goal";
    public static final String STATS_PEN = "penalty";
    public static final String STATS_FREEKICK = "freekick";
    public static final String STATS_PEN_SHOOTOUT = "penaltyShootout";
    public static final String STATS_PEN_TAKEN = "penaltyTaken";
    public static final String STATS_OG = "ownGoal";
    public static final String STATS_HEADER = "header";
    public static final String STATS_WEAK_FOOT_GOAL = "weakFootGoal";
    public static final String STATS_OTHER_GOAL = "otherGoal";
    public static final String STATS_ASSIST = "assist";
    public static final String STATS_YELLOW = "yellow";
    public static final String STATS_RED = "red";
    public static final String STATS_CLEAN_SHEET = "cleanSheet";
    public static final String STATS_PEN_SAVED = "penaltySaved";

    //  table result
    public static final String TABLE_RESULT = "result";
    // result table columns
    public static final String RESULT_ID = "id";
    public static final String RESULT_HOME_ID = "homeID";
    public static final String RESULT_AWAY_ID = "awayID";
    public static final String RESULT_TOURNAMENT_ID = "tournamentID";
    public static final String RESULT_HOME_NAME = "homeName";
    public static final String RESULT_AWAY_NAME = "awayName";
    public static final String RESULT_TOURNAMENT_NAME = "tournamentName";
    public static final String RESULT_DATE = "date";
    public static final String RESULT_STAGE = "stage";
    public static final String RESULT_FT_SCORE = "ftScore";
    public static final String RESULT_EXTRA_SCORE = "extraScore";
    public static final String RESULT_PEN_SCORE = "penScore";
    public static final String RESULT_INFO = "info";
    public static final String RESULT_HOME_EVENTS = "homeEvents";
    public static final String RESULT_AWAY_EVENTS = "awayEvents";

    // table tournament
    public static final String TABLE_TOURNAMENT = "tournament";
    // tournament columns
    public static final String TOURNAMENT_ID = "id";
    public static final String TOURNAMENT_NAME = "name";
    public static final String TOURNAMENT_INFO = "info";


    // table chat
    public static final String TABLE_CHAT = "chat";
    // chat columns
    public static final String CHAT_ID = "id";
    public static final String CHAT_TOURNAMENT_ID = "tournamentID";
    public static final String CHAT_CLUB_ID = "clubID";
    public static final String CHAT_RECEIVER_ID = "receiverID";
    public static final String CHAT_SENDER_ID = "senderID";
    public static final String CHAT_SENDER_NAME = "senderName";
    public static final String CHAT_MESSAGE_TYPE = "messageType";
    public static final String CHAT_MESSAGE_CONTENT = "messageContent";
    public static final String CHAT_TIME = "time";

    // table location
    public static final String TABLE_LOCATION = "location";
    // location columns
    public static final String LOCATION_C_ID = "clubID";
    public static final String LOCATION_P_ID = "playerID";
    public static final String LOCATION_LAT = "latitude";
    public static final String LOCATION_LNG = "longitude";
    public static final String LOCATION_LAST_UPDATE = "lastUpdate";

    // table event
    // table location
    public static final String TABLE_EVENT = "event";
    public static final String KEY_EVENT_LIST = "events";
    // location columns
    public static final String EVENT_ID = "id";
    public static final String EVENT_C_ID = "clubID";
    public static final String EVENT_TITLE = "eventTitle";
    public static final String EVENT_ADDRESS = "eventAddress";
    public static final String EVENT_LATITUDE = "latitude";
    public static final String EVENT_LONGITUDE = "longitude";
    public static final String EVENT_DATETIME = "eventTime";

    // event keys
    public static final String EVENT_TYPE = "type";
    public static final String EVENT_PLAYER = "player";
    public static final String EVENT_TIME = "time";
    // event type options
    public static final String EVENT_TYPE_GOAL = "goal";
    public static final String EVENT_TYPE_ASSIST = "assist";
    public static final String EVENT_TYPE_YELLOW = "yellow";
    public static final String EVENT_TYPE_SECOND_YELLOW = "second yellow";
    public static final String EVENT_TYPE_RED = "red";
    public static final String EVENT_TYPE_SUB_ON = "sub on";
    public static final String EVENT_TYPE_SUB_OFF = "sub off";
    public static final String EVENT_TYPE_SUB = "substitution";
    public static final String EVENT_TYPE_OG = "own goal";
    // event half options
    public static final String EVENT_TIME_FIRST_HALF = "first half";
    public static final String EVENT_TIME_SECOND_HALF = "second half";
    public static final String EVENT_TIME_EXTRA_FIRST_HALF = "extra first half";
    public static final String EVENT_TIME_EXTRA_SECOND_HALF = "extra second half";
    public static final String EVENT_TIME_PK = "penalty shootout";

    public static final String[] EVENT_TYPES = {EVENT_TYPE_GOAL,EVENT_TYPE_YELLOW,EVENT_TYPE_RED,EVENT_TYPE_OG,EVENT_TYPE_SUB};
    public static final String[] EVENT_HALVES = {EVENT_TIME_FIRST_HALF,EVENT_TIME_SECOND_HALF,EVENT_TIME_EXTRA_FIRST_HALF,EVENT_TIME_EXTRA_SECOND_HALF};

    // result columns
    public static final String RESULT_KEY_HOME = "homeID";
    public static final String RESULT_KEY_AWAY = "awayID";
    public static final String RESULT_KEY_TOURNAMENT = "tournamentID";
    public static final String RESULT_KEY_SCORE = "score";
    public static final String RESULT_KEY_PEN = "penalty";
    public static final String RESULT_KEY_HOME_EVENT = "homeEvent";
    public static final String RESULT_KEY_AWAY_EVENT = "awayEvent";

    // table squad
    public static final String TABLE_SQUAD = "squad";
    // squad columns
    public static final String SQUAD_TOURNAMENT_ID = "tournamentID";
    public static final String SQUAD_CLUB_ID = "clubID";
    public static final String SQUAD_PLAYER_ID = "playerID";
    public static final String SQUAD_NUMBER = "number";

    // table member
    public static final String TABLE_MEMBER = "member";
    // member columns
    public static final String MEMBER_P_ID = "playerID";
    public static final String MEMBER_C_ID = "clubID";
    public static final String MEMBER_SINCE = "memberSince";
    public static final String MEMBER_IS_ACTIVE = "isActive";
    public static final String MEMBER_PRIORITY = "priority";

    public static final int PRIORITY_APPLICANT = 0;
    public static final int PRIORITY_REGULAR = 1;
    public static final int PRIORITY_CO_CAP = 2;
    public static final int PRIORITY_CAPTAIN = 3;

    public static final String ROLE_PLAYER = "Player";
    public static final String ROLE_MANAGER = "Manager";

    public static final String POSITION_DEF = "Defender";
    public static final String POSITION_MID = "Midfielder";
    public static final String POSITION_FWD = "Forward";
    public static final String POSITION_GK = "Goalkeeper";

    public static final String OPTION_ALL_CLUBS = "All Clubs";
    public static final String OPTION_ALL_TOURNAMENTS = "All Tournaments";

    public static final String[] roles = {Constant.ROLE_PLAYER,Constant.ROLE_MANAGER};

    public static final String[] positions = {
            Constant.POSITION_DEF,
            Constant.POSITION_MID,
            Constant.POSITION_FWD,
            Constant.POSITION_GK    };

    public static final String[] PLAYER_TABS = {"Game Performance","Penalty History","Goal Distribution","Overall Stats"};
    public static final String[] PLAYER_STATS_CENTER_TEXT = {"Total Games","Penalty Taken","Total Goals",null};
    public static final boolean[] PLAYER_STATS_SHOW_CENTER = {true,true,true,false};
    public static final boolean[] PLAYER_STATS_IS_INT = {true,true,true,true};

    public static final String[] CLUB_STATS_TABS = {"Game Performance","Team Stats","Player Stats"};
    public static final String[] CLUB_STATS_CENTER_TEXT = {"Games Played",null,null};
    public static final boolean[] CLUB_STATS_SHOW_CENTER = {true,false,false};
    public static final boolean[] CLUB_STATS_IS_INT = {true,true,true};

    public static final int CHART_TYPE_BAR = 0;
    public static final int CHART_TYPE_PIE = 1;

    public static final int ALL_STATS = -1;

    public static final String[] LABEL_GAME_PERFORMANCE = {"WIN","DRAW","LOSS"};
    public static final String[] LABEL_PENALTY_HISTORY = {"Scored","Missed"};
    public static final String[] LABEL_GOAL_DISTRIBUTION = {"Strong Foot","Weak Foot","Header","Other"};
    public static final String[] LABEL_PLAYER_TOTAL_STATS = {"played","start","goal","assist","yellow","red"};

    public static final String FRAGMENT_PLAYER_PROFILE = "fragment_player_profile";
    public static final String FRAGMENT_VISITOR = "fragment_visitor";
    public static final String FRAGMENT_PLAYER_CLUB_LIST = "fragment_player_club_list";
    public static final String FRAGMENT_CLUB = "fragment_club";
    public static final String FRAGMENT_CLUB_MEMBER = "fragment_club_member";
    public static final String FRAGMENT_CLUB_TOURNAMENT_LIST = "fragment_club_tournament_list";
    public static final String FRAGMENT_MAP = "fragment_map";
    public static final String FRAGMENT_TOURNAMENT = "fragment_tournament";

    // FireBase Cloud Messaging Topics
    public static final String KEY_TOPICS = "topics";
    public static final String FCM_TOPIC_ALL = "all";

    // Location service related values
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String RECEIVER = "RECEIVER";
    public static final String RESULT_DATA_KEY = "RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = "LOCATION_DATA_EXTRA";
    public static final String ADDRESS_DATA_EXTRA = "ADDRESS_DATA_EXTRA";

    // AWS S3 related constants
    public static final String S3_BUCKET = "s3-test-jian";
    public static final String S3_ROOT_FOLDER = "https://s3.amazonaws.com/s3-test-jian/";
    public static final String S3_IDENTITY_POOL_ID = "us-west-2:9f75cf75-db5b-4d31-88a4-67889bdf82c9";
    public static final String S3_UPLOAD_RESULT_LISTENER = "onUploadResultListener";
    public static final String S3_UPLOAD_BITMAP = "bitmap";
    public static final String S3_SHOULD_COMPRESS = "shouldCompress";
    public static final String S3_UPLOAD_TYPE = "uploadType";
    public static final int S3_UPLOAD_AVATAR = 0;
    public static final int S3_UPLOAD_CHAT_IMAGE = 1;
    public static final String IMAGE_FORMAT = ".jpg";
    public static final int IMAGE_COMPRESS_RATE = 60;
    public static final int IMAGE_NO_COMPRESS_RATE = 100;

}
