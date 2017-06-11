package com.example.cristiano.myteam.util;

/**
 * Created by Cristiano on 2017/4/17.
 *
 * this class offers the url to different APIs
 */

public class UrlHelper {
    //AWS URL
//    private static final String URL = "https://my-team-rest-api.herokuapp.com";
    // Heroku URL
//    private static final String URL = "https://my-team-rest-api.herokuapp.com";
    //local testing URL
    private static final String URL = "http://192.168.1.9:5000";

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

    public static String urlPutPlayer(int playerID){
        return URL + "/player/id/" + playerID;
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

    public static String urlPutSquad() {
        return URL + "/squad";
    }

    public static String urlGetClubMembers(int clubID){
        return URL + "/member/club/" + clubID;
    }

    public static String urlPostRegTournament(int clubID, int playerID){
        return URL + "/tournament/club/" + clubID + "/player/" + playerID;
    }

    public static String urlPostTournament(){
        return URL + "/tournament/register";
    }

    public static String urlPostTournamentSquad(int tournamentID, int clubID){
        return URL + "/tournament/" + tournamentID + "/club/" + clubID;
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

    public static String urlGetChat (int tournamentID, int clubID, int receiverID, int senderID, int limit, int offset) {
        return URL + "/chat/tournament/" + tournamentID + "/club/" + clubID + "/receiver/"
                + receiverID + "/sender/" + senderID + "/limit/" + limit + "/offset/" + offset;
    }

    public static String urlPostTournamentChat (int tournamentID, int clubID) {
        return URL + "/chat/tournament/" + tournamentID + "/club/" + clubID;
    }

    public static String urlPostClubChat (int clubID) {
        return URL + "/chat/club/" + clubID;
    }

    public static String urlPostPrivateChat (int receiverID) {
        return URL + "/chat/private/" + receiverID;
    }

    public static String urlPutLocation (int clubID, int playerID) {
        return URL + "/location/club/" + clubID + "/player/" + playerID;
    }

    public static String urlGetAllLocations (int clubID) {
        return URL + "/location/club/" + clubID;
    }

}
