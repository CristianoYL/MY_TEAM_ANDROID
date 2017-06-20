package com.example.cristiano.myteam.service;

import android.content.Intent;
import android.net.ParseException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.cristiano.myteam.util.Constant;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Cristiano on 2017/6/18.
 */

public class MyFirebaseMessageService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    /**
     *  this method will be called when new message (notification/data payload) arrives
     * @param remoteMessage the message to receive
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Intent intent = new Intent(Constant.INTENT_NEW_MESSAGE);
            int clubID = 0,tournamentID = 0;
            String clubIDData,tournamentIDData;
            clubIDData = remoteMessage.getData().get("clubID");
            tournamentIDData = remoteMessage.getData().get("tournamentID");
            try{
                if ( clubIDData != null ) {
                    clubID = Integer.parseInt(clubIDData);
                }
                if ( tournamentIDData != null ) {
                    tournamentID = Integer.parseInt(tournamentIDData);
                }
            }catch (NumberFormatException e) {
                e.printStackTrace();
            }
            intent.putExtra("clubID",clubID);
            intent.putExtra("tournamentID",tournamentID);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
