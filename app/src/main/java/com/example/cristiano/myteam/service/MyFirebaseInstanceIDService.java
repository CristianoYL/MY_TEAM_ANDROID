package com.example.cristiano.myteam.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Token;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cristiano on 2017/6/17.
 *
 * On initial startup of this app, the FCM SDK generates a registration token
 *  for the client app instance.
 * This service will listen for the token creation and update,
 *  and store and upload the token to both local cache and server
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        storeAndUploadToken(refreshedToken);
    }

    /**
     *  This method will try to store the token to local cache,
     *  and if user's playerID has been cached, this method will use both info (playerID and token)
     *  to update the server database.
     *  if user's playerID hasn't been cached yet,
     *  it means it's either a new user or the user has logged out.
     *  Thus do not send the update to server yet and wait for another event to trigger the update.
     * @param instanceToken the new instance ID token
     */
    private void storeAndUploadToken(String instanceToken){
        SharedPreferences preferences
                = getSharedPreferences(Constant.KEY_USER_PREF,MODE_PRIVATE);
        String cachedToken = preferences.getString(Constant.CACHE_IDD_TOKEN,null);
        // if token not cached or cached token not matching new ones, update cache and sent to server
        if ( cachedToken == null || !cachedToken.equals(instanceToken) ) {
            // update cache
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constant.CACHE_IDD_TOKEN,instanceToken);
            editor.apply();

            // see if user's playerID is cached
            int myPlayerID = preferences.getInt(Constant.CACHE_PLAYER_ID,0);
            if ( myPlayerID == 0 ) {
                /**
                 *  if playerID not cached, it means this is a new user or user not logged in yet
                 *  do not send the token to server for now
                 *  wait till the user login/register as player and then send the token to app server
                 */
                Log.d(TAG,"Not logged in yet. No playerID available");
                return;
            }
            // else the playerID is cached, upload the token to server
            Token token = new Token(myPlayerID,instanceToken);
            RequestAction actionPutToken = new RequestAction() {
                @Override
                public void actOnPre() {
                    Log.d(TAG, "Preparing to send new token to server");
                }

                @Override
                public void actOnPost(int responseCode, String response) {
                    if ( responseCode == 201 ) {
                        Log.d(TAG, "New token created!");
                    } else if ( responseCode == 200 ) {
                        Log.d(TAG, "Token updated!");
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString(Constant.KEY_MSG);
                            Log.e(TAG, "Uploading token failed with response message:" + message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Uploading token failed with response message:" + response);
                        }
                    }
                }
            };
            String url = UrlHelper.urlPutToken(myPlayerID);
            RequestHelper.sendPutRequest(url,token.toJson(),actionPutToken);
        }
    }
}
