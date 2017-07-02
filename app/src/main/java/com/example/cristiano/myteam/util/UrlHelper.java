package com.example.cristiano.myteam.util;

/**
 * Created by Cristiano on 2017/4/17.
 *
 * this class offers the url to different APIs
 */

public class UrlHelper {
    //AWS FLASK_URL
//    private static final String FLASK_URL = "https://my-team-rest-api.herokuapp.com";
    // Heroku FLASK_URL
    private static final String FLASK_URL = "https://my-team-rest-api.herokuapp.com";
    //local testing FLASK_URL
//    private static final String FLASK_URL = "http://192.168.1.5:5000";

    public static String urlLogin(){
        return FLASK_URL + "/auth";
    }

    public static String urlRegister(){
        return FLASK_URL + "/user";
    }

    public static String urlPutToken(int playerID){
        return FLASK_URL + "/token/player/" + playerID;
    }

    public static String urlPlayerInfoByID(int playerID){
        return FLASK_URL + "/player_info/id/" + playerID;
    }

    public static String urlPlayerByID(int playerID){
        return FLASK_URL + "/player/id/" + playerID;
    }

    public static String urlPlayerByToken(){
        return FLASK_URL + "/player/token";
    }

    public static String urlPlayerInfoByUser(int userID){
        return FLASK_URL + "/player_info/user/" + userID;
    }

    public static String urlPlayerClubInfo(int clubID, int playerID){
        return FLASK_URL + "/player_info/" + playerID + "/club/" + clubID;
    }

    public static String urlPostRegPlayer(int clubID){
        return FLASK_URL + "/player/club/" + clubID;
    }

    public static String urlClubByID(int clubID){
        return FLASK_URL + "/club/id/" + clubID;
    }

    public static String urlClubByName(String name){
        return FLASK_URL + "/club/name/" + name;
    }

    public static String urlClubInfoByID(int clubID){
        return FLASK_URL + "/club_info/id/" + clubID;
    }

    public static String urlEventByClub(int clubID, int limit, int offset){
        return FLASK_URL + "/event/club/" + clubID + "/limit/" + limit + "/offset/" + offset;
    }

    public static String urlMemberRequest(int clubID) {
        return FLASK_URL + "/member/request/" + clubID;
    }

    public static String urlMemberManagement(int clubID, int playerID, boolean isPromotion) {
        if ( isPromotion ) {
            return FLASK_URL + "/member/manage/club/" + clubID + "/player/" + playerID + "/promote/true";
        } else {
            return FLASK_URL + "/member/manage/club/" + clubID + "/player/" + playerID + "/promote/false";
        }
    }

    public static String urlRegClubFromPlayer(int playerID){
        return FLASK_URL + "/club/player/" + playerID;
    }

    public static String urlSquad() {
        return FLASK_URL + "/squad";
    }

    public static String urlMembersByClub(int clubID){
        return FLASK_URL + "/member/club/" + clubID;
    }

    public static String urlRegTournamentFromClub(int clubID, int playerID){
        return FLASK_URL + "/tournament/club/" + clubID + "/player/" + playerID;
    }

    public static String urlTournamentSquad(int tournamentID, int clubID){
        return FLASK_URL + "/tournament/" + tournamentID + "/club/" + clubID;
    }

    public static String urlStatsByTournamentClubPlayer(int tournamentID, int clubID, int playerID){
        return FLASK_URL + "/stats/tournament/" + tournamentID + "/club/" + clubID + "/player/" + playerID;
    }

    public static String urlResultsByTournamentClub(int tournamentID, int clubID){
        return FLASK_URL + "/result/tournament/"+tournamentID+"/club/" + clubID;
    }

    public static String urlTournamentsByClub(int clubID){
        return FLASK_URL + "/tournament/club/" + clubID;
    }

    public static String urlStatsByTournamentClub(int tournamentID, int clubID) {
        return FLASK_URL + "/stats/tournament/" + tournamentID + "/club/" + clubID;
    }

    public static String urlSquadByTournamentClub (int tournamentID, int clubID) {
        return FLASK_URL + "/squad/tournament/" + tournamentID + "/club/" + clubID;
    }

    public static String urlChat(int tournamentID, int clubID, int receiverID, int senderID, int limit, int beforeID, int afterID) {
        return FLASK_URL + "/chat/tournament/" + tournamentID + "/club/" + clubID + "/receiver/"
                + receiverID + "/sender/" + senderID + "/limit/" + limit
                + "/before/" + beforeID + "/after/" + afterID;
    }

    public static String urlChatByTournament(int tournamentID, int clubID) {
        return FLASK_URL + "/chat/tournament/" + tournamentID + "/club/" + clubID;
    }

    public static String urlChatByClub(int clubID) {
        return FLASK_URL + "/chat/club/" + clubID;
    }

    public static String urlPrivateChat(int receiverID) {
        return FLASK_URL + "/chat/private/" + receiverID;
    }

    public static String urlLocationByClubPlayer(int clubID, int playerID) {
        return FLASK_URL + "/location/club/" + clubID + "/player/" + playerID;
    }

    public static String urlLocationByClub(int clubID) {
        return FLASK_URL + "/location/club/" + clubID;
    }

}
