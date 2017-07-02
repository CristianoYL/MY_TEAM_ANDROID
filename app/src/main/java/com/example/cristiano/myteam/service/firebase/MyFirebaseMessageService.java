package com.example.cristiano.myteam.service.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ParseException;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.activity.LoginActivity;
import com.example.cristiano.myteam.util.Constant;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

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
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//            sendNotification(title,body);
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_menu_club)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void handleNotificationMessage(RemoteMessage.Notification notification){

    }
}
