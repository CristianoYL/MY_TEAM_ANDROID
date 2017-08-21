package com.example.cristiano.myteam.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.cristiano.myteam.R;

import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.structure.User;
import com.example.cristiano.myteam.structure.UserCredential;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.util.FCMHelper;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RequestAction requestAction = null;

    private static final String TAG = "LoginActivity";

    // UI references.
    private AutoCompleteTextView et_email;
    private EditText et_password,et_passwordConfirm;
    private View layout_passwordConfirm;
    private Button btn_left, btn_right;
    private View pb_progress, layout_login;
    private boolean isLogin, rememberUsername,autoLogin;
    private CheckBox cb_remember, cb_auto;

    private String email, password, jwtToken;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_email = (AutoCompleteTextView) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        layout_passwordConfirm =  findViewById(R.id.layout_passwordConfirm);
        et_passwordConfirm =  (EditText) findViewById(R.id.et_confirm);
        btn_left = (Button) findViewById(R.id.email_sign_in_button);
        btn_right = (Button) findViewById(R.id.email_register_button);
        layout_login = findViewById(R.id.layout_login);
        pb_progress = findViewById(R.id.login_progress);
        cb_remember = (CheckBox) findViewById(R.id.cb_remember);
        cb_auto = (CheckBox) findViewById(R.id.cb_autoLogin);
        FCMHelper.getInstance().subscribeToAppNotification();
        // Set up the login form.
        setRegistrationMode(false);
//        populateAutoComplete();
        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if ( isLogin && id == EditorInfo.IME_ACTION_GO ) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        et_passwordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( !isLogin && actionId == EditorInfo.IME_ACTION_GO) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        btn_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    attemptLogin();
                } else {
                    attemptRegister();
                }
            }
        });

        btn_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    setRegistrationMode(true);  // set to register mode
                } else {
                    setRegistrationMode(false); // set to login mode
                }
            }
        });
        // retrieve the user's login preference. e.g. auto login
        sharedPreferences = getSharedPreferences(Constant.KEY_USER_PREF,MODE_PRIVATE);
        rememberUsername = sharedPreferences.getBoolean(Constant.PREF_REMEMBER_USERNAME,false);
        autoLogin = sharedPreferences.getBoolean(Constant.PREF_AUTO_LOGIN,false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("FCM NOTIFICATION", "Key: " + key + " Value: " + value);
            }
        }
        cb_remember.setChecked(rememberUsername);
        cb_auto.setChecked(autoLogin);
        if ( autoLogin ) {
            et_email.setText(sharedPreferences.getString(Constant.KEY_USERNAME,""));
            et_password.setText(sharedPreferences.getString(Constant.KEY_PASSWORD,""));
            attemptLogin();
        } else if (rememberUsername) {
            et_email.setText(sharedPreferences.getString(Constant.KEY_USERNAME,""));
            et_password.requestFocus();
        }
    }

    //    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//        getLoaderManager().initLoader(0, null, this);
//    }
//
//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(et_email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptRegister() {
        if (requestAction != null) {
            Log.d(TAG,"another request in being sent...");
            return;
        }

        // Reset errors.
        et_email.setError(null);
        et_password.setError(null);

        this.email = et_email.getText().toString();
        this.password = et_password.getText().toString();
        String confirmPassword = et_passwordConfirm.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(this.email)) {
            et_email.setError(getString(R.string.error_field_required));
            focusView = et_email;
            cancel = true;
        } else if (!isEmailValid(this.email)) {
            et_email.setError(getString(R.string.error_invalid_email));
            focusView = et_email;
            cancel = true;
        }

        //check password match up
        if (!password.equals(confirmPassword)) {
            et_passwordConfirm.setError(getString(R.string.error_match_failed));
            focusView = et_passwordConfirm;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            requestAction = new RequestAction() {
                @Override
                public void actOnPre() {
                    showProgress(true);
                }

                @Override
                public void actOnPost(int responseCode, String response) {
                    Log.d(TAG,response);
                    showProgress(false);
                    if ( responseCode == 201 ) {
                        Toast.makeText(LoginActivity.this,"Registration Succeeded!",Toast.LENGTH_SHORT).show();
                        isLogin = true;
                        layout_passwordConfirm.setVisibility(View.GONE);
                        btn_right.setText(R.string.action_register);
                        btn_left.setText(R.string.action_sign_in);
                        cb_auto.setChecked(true);
                        cb_remember.setChecked(true);
                        // clear reference and try to login
                        requestAction = null;
                        attemptLogin();
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString(Constant.KEY_MSG);
                            et_email.setError(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,"unexpected response");
                            et_email.setError(response);
                        }
                        et_email.requestFocus();
                    }
                    requestAction = null;
                }
            };
            Log.d(TAG,"registering...");
            String url = UrlHelper.urlRegister();
            User user = new User(this.email,this.password);
            RequestHelper.sendPostRequest(url,user.toJson(),requestAction);
        }

    }

    private void attemptLogin() {
        if (requestAction != null) {
            Log.d(TAG,"another request in being sent...");
            return;
        }

        // Reset errors.
        et_email.setError(null);
        et_password.setError(null);

        // Store values at the time of the login attempt.
        this.email = et_email.getText().toString();
        this.password = et_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(this.password) && !isPasswordValid(this.password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(this.email)) {
            et_email.setError(getString(R.string.error_field_required));
            focusView = et_email;
            cancel = true;
        } else if (!isEmailValid(this.email)) {
            et_email.setError(getString(R.string.error_invalid_email));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // remember user's choice and input
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.PREF_REMEMBER_USERNAME,cb_remember.isChecked());
            editor.putBoolean(Constant.PREF_AUTO_LOGIN,cb_auto.isChecked());
            if ( cb_auto.isChecked() ) {
                editor.putString(Constant.KEY_USERNAME,email);
                editor.putString(Constant.KEY_PASSWORD,password);
            } else if (cb_remember.isChecked() ) {
                editor.putString(Constant.KEY_USERNAME,email);
            }
            editor.apply();
            UserCredential userCredential = new UserCredential(this.email,this.password);
            String credentials = userCredential.toJson();
            requestAction = new RequestAction() {
                @Override
                public void actOnPre() {
                    showProgress(true);
                }

                @Override
                public void actOnPost(int responseCode, String response) {
                    showProgress(false);
                    if ( responseCode == 200 ) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jwtToken = jsonObject.getString(Constant.USER_ACCESS_TOKEN);
                            navigateToNextPage();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,"Missing access_token");
                            return;
                        }
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = "Unknown Error!";
                            if ( jsonObject.has(Constant.KEY_DESC) ) {
                                message = jsonObject.getString(Constant.KEY_DESC);
                            } else if ( jsonObject.has(Constant.KEY_MSG) ) {
                                message = jsonObject.getString(Constant.KEY_MSG);
                            }
                            et_password.setError(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,"unexpected response");
                            if ( response == null || response.length() < 1 ) {
                                et_password.setError(getString(R.string.error_no_connection));
                            } else {
                                et_password.setError(response);
                            }
                        }
                        et_password.requestFocus();
                    }
                    requestAction = null;
                }
            };
            String url = UrlHelper.urlLogin();
            RequestHelper.sendPostRequest(url,credentials,requestAction);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    /**
     * password requirement length of 6-20
     * @param password the new password to test
     * @return if the password is valid
     */
    private boolean isPasswordValid(String password) {
        boolean isValid = true;
        if ( password.length() < 6 || password.length() > 20 ) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            layout_login.setVisibility(show ? View.GONE : View.VISIBLE);
            layout_login.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layout_login.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            pb_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            pb_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    pb_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            pb_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            layout_login.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        et_email.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void setRegistrationMode(boolean isRegister) {
        if ( isRegister ) { // turn to registration mode
            isLogin = false;
            layout_passwordConfirm.setVisibility(View.VISIBLE);
            // reset all error info and focuses
            et_email.setError(null);
            et_email.clearFocus();
            et_password.setError(null);
            et_password.clearFocus();
            et_passwordConfirm.setText("");
            et_passwordConfirm.clearFocus();
            // change button text accordingly, set button to "Register" and "Cancel"
            btn_right.setText(R.string.label_cancel);
            btn_left.setText(R.string.action_register);
            layout_passwordConfirm.setVisibility(View.VISIBLE);
            // define IME Actions
            et_password.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {    // set to login mode
            isLogin = true;
            // hide and clear password confirm field
            layout_passwordConfirm.setVisibility(View.GONE);
            et_passwordConfirm.setText("");
            et_passwordConfirm.clearFocus();
            // reset all error info and focus on the first blank field
            et_email.setError(null);
            et_password.setError(null);

            if ( TextUtils.isEmpty(et_email.getText()) ) {
                et_email.requestFocus();
            } else if ( TextUtils.isEmpty(et_password.getText()) ) {
                et_password.requestFocus();
            }
            // change button text accordingly, set button to "Sign in" and "Register"
            layout_passwordConfirm.setVisibility(View.GONE);
            btn_right.setText(R.string.action_register);
            btn_left.setText(R.string.action_sign_in);
            // define IME Actions
            et_password.setImeOptions(EditorInfo.IME_ACTION_GO);
        }
    }

    /**
     * Send a GET request to retrieve player info,
     * and use the response to decide which page to navigate to.
     * (PlayerRegistration, Player Profile or Club Page)
     */
    private void navigateToNextPage(){
        RequestAction actionGetPlayer = new RequestAction() {
            @Override
            public void actOnPre() {
                showProgress(true);
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                showProgress(false);
                if ( responseCode == 200 ) {  // player is found
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonPlayer = jsonObject.getJSONObject(Constant.TABLE_PLAYER);
                        int playerID = jsonPlayer.getInt(Constant.PLAYER_ID);
                        int userID = jsonPlayer.getInt(Constant.PLAYER_USER_ID);
                        String firstName = jsonPlayer.getString(Constant.PLAYER_FIRST_NAME);
                        String lastName = jsonPlayer.getString(Constant.PLAYER_LAST_NAME);
                        String displayName = jsonPlayer.getString(Constant.PLAYER_DISPLAY_NAME);
                        String role = jsonPlayer.getString(Constant.PLAYER_ROLE);
                        String phone = jsonPlayer.getString(Constant.PLAYER_PHONE);
                        int age = jsonPlayer.getInt(Constant.PLAYER_AGE);
                        float weight = (float) jsonPlayer.getDouble(Constant.PLAYER_WEIGHT);
                        float height = (float) jsonPlayer.getDouble(Constant.PLAYER_HEIGHT);
                        boolean leftFooted = jsonPlayer.getBoolean(Constant.PLAYER_FOOT);
                        String avatar = jsonPlayer.getString(Constant.PLAYER_AVATAR);
                        // use the retrieve info to create a Player instance
                        Player player = new Player(playerID,userID,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar);
                        JSONArray jsonTopics = jsonObject.getJSONArray(Constant.KEY_TOPICS);
                        String[] topics = new String[jsonTopics.length()];
                        for ( int i = 0; i < jsonTopics.length(); i++ ) {
                            topics[i] = jsonTopics.getString(i);
                        }
                        FCMHelper.getInstance().subscribeToTopics(topics);
                        int defaultClubID = sharedPreferences.getInt(Constant.CACHE_DEFAULT_CLUB_ID,0);
                        Log.d(TAG,"Default Club:" + defaultClubID);
                        if ( defaultClubID != 0 ) { // if default club has been set
                            getClub(defaultClubID, player); // go to default club's page
                        } else {    // if not set
                            showPlayerPage(player); // go to player's profile page
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ( responseCode == 404 ) { // if player not found, go to registration page
                    Log.d(TAG,"New user login");
                    showRegistrationPage(jwtToken);
                } else {
                    Toast.makeText(LoginActivity.this, "Unknown Error!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        String url = UrlHelper.urlPlayerByToken();
        RequestHelper.sendGetRequest(url,jwtToken,actionGetPlayer); // try to use access_token to get the player
    }

    private void getClub(int clubID, final Player player) {
        RequestAction actionGetClub = new RequestAction() {
            @Override
            public void actOnPre() {
                showProgress(true);
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                showProgress(false);
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int id = jsonObject.getInt(Constant.CLUB_ID);
                        String name = jsonObject.getString(Constant.CLUB_NAME);
                        String info = jsonObject.getString(Constant.CLUB_INFO);
                        Club club = new Club(id,name,info);
                        showClubPage(club,player);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {    // if failed to load club
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(Constant.CACHE_DEFAULT_CLUB_ID,0);    // clear cached clubID
                    editor.apply();
                    showPlayerPage(player); // go to the player's profile page instead
                }
            }
        };
        String url = UrlHelper.urlClubByID(clubID);
        RequestHelper.sendGetRequest(url,actionGetClub);
    }

    private void showPlayerPage(Player player) {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra(Constant.TABLE_PLAYER, player.toJson());
        startActivity(intent);
    }

    private void showClubPage(Club club, Player player) {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra(Constant.TABLE_PLAYER, player.toJson());
        intent.putExtra(Constant.TABLE_CLUB, club.toJson());
        startActivity(intent);
    }

    private void showRegistrationPage(String jwt) {
        Intent intent = new Intent(LoginActivity.this,PlayerRegistrationActivity.class);
        intent.putExtra(Constant.USER_ACCESS_TOKEN,jwt);
        startActivity(intent);
    }
}