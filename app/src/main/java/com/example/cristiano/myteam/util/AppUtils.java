package com.example.cristiano.myteam.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.activity.LoginActivity;

import java.io.File;

/**
 * Created by Cristiano on 2017/6/24.
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

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

    public static final int MENU_PLAYER = 0;
    public static final int MENU_CLUB = 1;
    public static final int MENU_REGISTER = 2;
    public static void setNavigationMenu(Activity activity, int menuType){
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view_player);
        Menu menu = navigationView.getMenu();
        menu.clear();
        switch ( menuType ) {
            case MENU_PLAYER:
                navigationView.inflateMenu(R.menu.menu_player_profile);
                navigationView.setCheckedItem(R.id.nav_player_profile);
                break;
            case MENU_CLUB:
                navigationView.inflateMenu(R.menu.menu_club);
                navigationView.setCheckedItem(R.id.nav_club);
                break;
            case MENU_REGISTER:
                navigationView.inflateMenu(R.menu.menu_player_registration);
                break;
            default:
                Log.e(TAG,"setNavigationMenu with invalid menu type.");
                navigationView.inflateMenu(R.menu.menu_player_profile);
                break;
        }
    }

    /**
     *  Get the available cache directory on the device.
     *  Always prefer to store on external cache than memory
     * @param context the application context
     * @return the available cache dir
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        File file;
        if ( (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable() )
                && (file = context.getExternalCacheDir()) != null ) {    // external cache available
            cachePath = file.getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     *  Get the available file directory on the device.
     *  Always prefer to store on external storage than memory
     * @param context the application context
     * @return the available file dir
     */
    public static String getDiskFileDir(Context context) {
        String cachePath = null;
        File file;
        if ( ( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable() )
                && (file = context.getExternalFilesDir(null)) != null ) {    // external storage available
            cachePath = file.getPath();
        } else {
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }

    public static Bitmap getRotatedBitmap(Context context, Uri photoUri, Bitmap bitmap){
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
        int orientation = -1;
        if ( cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            orientation = cursor.getInt(0);
            cursor.close();
        }
        Log.d(TAG,"media orientation:"+orientation);
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        return bitmap;

    }

}
