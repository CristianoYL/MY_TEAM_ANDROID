package com.example.cristiano.myteam.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.cristiano.myteam.activity.LoginActivity;

/**
 * Created by Cristiano on 2017/6/24.
 */

public class AppController {

    private static boolean isBackPressed = false;
    /**
     *  this helper method listen for a double tap on back button within 0.5s and minimize the app
     */
    public static void minimizeOnDoubleBack(Activity activity) {
        if (isBackPressed) {
            activity.moveTaskToBack(true);
            return;
        }
        isBackPressed = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackPressed = false;
            }
        }, 500);
    }

    /**
     *  this helper method hide the keyboard from the current screen
     * @param context the current context of the app
     * @param view  the root view of the current screen
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     *  This helper method performs a set of actions before user log out.
     *  1) uncheck the auto-login option for the user
     *  2) clear the default club ID cache for the user,
     *      since he may login to another account next time,
     *      and the previous club should not be visible
     *  3) clear the user's playerID cache
     *  4) update the server to unregister the device token from the topics
     *  5) go to LoginActivity
     * @param context the current context of the app
     */
    public static void logOut(Context context ){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constant.KEY_USER_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.PREF_AUTO_LOGIN,false);  // uncheck the auto-login for user
        editor.putInt(Constant.CACHE_PLAYER_ID,0);   // clear my player ID
        editor.putInt(Constant.CACHE_DEFAULT_CLUB_ID,0); // clear default club ID
        editor.apply();
        // unsubscribe this device from the player's club and tournament chat push notifications
        FCMHelper.getInstance().unsubscribeAllTopics();
        // go to login page
        Intent intent = new Intent(context,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
