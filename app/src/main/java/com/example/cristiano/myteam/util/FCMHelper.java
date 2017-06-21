package com.example.cristiano.myteam.util;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/6/21.
 *
 *  This is a FireBase Cloud Messaging helper class
 */

public class FCMHelper {
    private static FCMHelper instance = new FCMHelper();
    private static FirebaseMessaging firebaseMessaging;
    private static ArrayList<String> currentTopics;
    private FCMHelper() {
        // Exists only to defeat instantiation.
        firebaseMessaging = FirebaseMessaging.getInstance();
        currentTopics = new ArrayList<>();
    }
    public static FCMHelper getInstance() {
        return instance;
    }
    private static final String TAG = "FCMHelper";

    /**
     *  this topic is used for notifying all users for app official news, such as updates
     */
    public void subscribeToAppNotification(){
        // do not add to currentTopics to prevent unsubscription
        firebaseMessaging.subscribeToTopic(Constant.FCM_TOPIC_ALL);
        Log.d(TAG,"Subscribe to App Topic<all>");
    }

    /**
     *  Subscribe this device to given topic
     * @param topic the topic to subscribe
     */
    private void subscribeToTopic(String topic){
        firebaseMessaging.subscribeToTopic(topic);
        currentTopics.add(topic);
        Log.d(TAG,"Subscribe to topic <"+topic+">");
    }

    /**
     *  Subscribe this device to given topics
     * @param topics the topics to subscribe
     */
    public void subscribeToTopics(String[] topics){
        for ( String topic : topics ) {
            firebaseMessaging.subscribeToTopic(topic);
            currentTopics.add(topic);
            Log.d(TAG,"Subscribe to topic <"+topic+">");
        }
    }

    /**
     *  Unsubscribe this device from given topic
     * @param topic the topic to unsubscribe
     */
    public void unsubscribeFromTopic(String topic){
        firebaseMessaging.unsubscribeFromTopic(topic);
        currentTopics.remove(topic);
        Log.d(TAG,"Unsubscribe topic <"+topic+">");
    }

    /**
     *  Unsubscribe this device for all currentTopics except the app official broadcast topic.
     *  This method is designed for user logout behaviour
     */
    public void unsubscribeAllTopics(){
        for ( String topic : currentTopics ) {
            firebaseMessaging.unsubscribeFromTopic(topic);
        }
        currentTopics.clear();
        Log.d(TAG,"Unsubscribe all topics");
    }

    /**
     *  subscribe this device to club chat topic and receive FCM push notifications
      * @param clubID ID of the club to subscribe
     */
    public void subscribeToClubChat(int clubID){
        if ( clubID != 0 ) { // club chat
            subscribeToTopic("club_"+clubID);
            Log.d(TAG,"subscribe to club chat:<club_"+clubID+">");
        }
    }

    /**
     *  unsubscribe this device from club chat topic and stop receiving FCM push notifications
     * @param clubID ID of the club to unsubscribe
     */
    public void unsubscribeFromClubChat(int clubID){
        if ( clubID != 0 ) { // club chat
            unsubscribeFromTopic("club_"+clubID);
            Log.d(TAG,"unsubscribe from club chat:<club_"+clubID+">");
        }
    }

    /**
     *  subscribe this device to tournament chat topic and receive FCM push notifications
     * @param clubID ID of the club which participates in the tournament
     * @param tournamentID ID of the tournament to subscribe
     */
    public void subscribeToTournamentChat(int clubID, int tournamentID){
        if ( tournamentID != 0 && clubID != 0) {    // tournament chat
            subscribeToTopic("club_"+clubID+"_tournament_"+tournamentID);
            Log.d(TAG,"subscribe to tournament chat:<club_"+clubID+"_tournament_"+tournamentID+">");
        }
    }

    /**
     *  unsubscribe this device from club chat topic and stop receiving FCM push notifications
     * @param clubID ID of the club which participates in the tournament
     * @param tournamentID ID of the tournament to subscribe
     */
    public void unsubscribeFromTournamentChat(int clubID, int tournamentID){
        if ( tournamentID != 0 && clubID != 0) {    // tournament chat
            unsubscribeFromTopic("club_"+clubID+"_tournament_"+tournamentID);
            Log.d(TAG,"unsubscribe from tournament chat:<club_"+clubID+"_tournament_"+tournamentID+">");
        }
    }
}
