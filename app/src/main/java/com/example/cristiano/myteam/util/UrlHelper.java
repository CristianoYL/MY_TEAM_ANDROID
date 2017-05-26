package com.example.cristiano.myteam.util;

/**
 * Created by Cristiano on 2017/4/17.
 */

public class UrlHelper {
//    private static final String URL = "https://my-team-rest-api.herokuapp.com";
//    private static final String URL = "https://my-team-rest-api.herokuapp.com";
    private static final String URL = "http://192.168.1.6:5000";

    public static String urlLogin(){
        return URL + "/auth";
    }

    public static String urlRegister(){
        return URL + "/user";
    }

    public static String urlGetPlayerInfo(int playerID){
        return URL + "/player_info/id/" + playerID;
    }

    public static String urlGetPlayerInfo(String playerEmail){
        return URL + "/player_info/email/" + playerEmail;
    }

    public static String urlGetPlayerClubInfo(int clubID, int playerID){
        return URL + "/player_info/" + playerID + "/club/" + clubID;
    }

    public static String urlPutPlayer(String playerEmail){
        return URL + "/player/" + playerEmail;
    }

    public static String urlPostRegPlayer(int clubID){
        return URL + "/player/club/" + clubID;
    }

    public static String urlGetClubInfo(int clubID){
        return URL + "/club_info/id/" + clubID;
    }

    public static String urlPostRegClub(int playerID){
        return URL + "/club/player/" + playerID;
    }

    public static String urlPostSquad() {
        return URL + "/squad";
    }

    public static String urlGetClubTeamsheet(int clubID){
        return URL + "/teamsheet/club/" + clubID;
    }

    public static String urlPostRegTournament(int clubID, int playerID){
        return URL + "/tournament/club/" + clubID + "/player/" + playerID;
    }

    public static String urlPostTournament(){
        return URL + "/tournament/register";
    }

    public static String urlGetPlayerTournamentStats(int tournamentID, int clubID, int playerID){
        return URL + "/stats/tournament/" + tournamentID + "/club/" + clubID + "/player/" + playerID;
    }

    public static String urlGetClubResults(int clubID){
        return URL + "/result/club/" + clubID;
    }

    public static String urlGetClubTournamentResults(int tournamentID, int clubID){
        return URL + "/result/tournament/"+tournamentID+"/club/" + clubID;
    }

    public static String urlPostClubTournamentResults(int tournamentID, int clubID){
        return URL + "/result/tournament/"+tournamentID+"/club/" + clubID;
    }

    public static String urlGetClubTournaments(int clubID){
        return URL + "/tournament/club/" + clubID;
    }

    public static String urlGetTournamentClubStats (int tournamentID, int clubID) {
        return URL + "/stats/tournament/" + tournamentID + "/club/" + clubID;
    }

    public static String urlGetTournamentClubSquad (int tournamentID, int clubID) {
        return URL + "/squad/tournament/" + tournamentID + "/club/" + clubID;
    }

}
