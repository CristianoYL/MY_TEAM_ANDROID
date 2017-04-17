package com.example.cristiano.myteam.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.example.cristiano.myteam.structure.User;
import com.example.cristiano.myteam.structure.UserCredential;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RequestAction requestAction = null;

    // UI references.
    private AutoCompleteTextView et_email;
    private EditText et_password,et_passwordConfirm;
    private Button btn_signIn,btn_register;
    private View pb_progress, mLoginView;
    private boolean isLogin, rememberUsername,autoLogin;
    private CheckBox cb_remember, cb_auto;

    private String email, password;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        isLogin = true;
        et_email = (AutoCompleteTextView) findViewById(R.id.et_email);
        populateAutoComplete();

        et_password = (EditText) findViewById(R.id.et_password);
        et_passwordConfirm =  (EditText) findViewById(R.id.et_confirm);
        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        btn_signIn = (Button) findViewById(R.id.email_sign_in_button);
        btn_signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    attemptLogin();
                } else {
                    //TODO: register
                    attemptRegister();
                }
            }
        });

        btn_register = (Button) findViewById(R.id.email_register_button);
        btn_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    // go to registration page, set button to "Register" and "Cancel"
                    btn_register.setText(R.string.cancel);
                    btn_signIn.setText(R.string.action_register);
                    et_passwordConfirm.setVisibility(View.VISIBLE);
                } else {
                    // go to sign in page, set button to "Sign in" and "Register"
                    et_passwordConfirm.setVisibility(View.GONE);
                    btn_register.setText(R.string.action_register);
                    btn_signIn.setText(R.string.action_sign_in);
                }
                isLogin = !isLogin;
            }
        });
        mLoginView = findViewById(R.id.layout_login);
        pb_progress = findViewById(R.id.login_progress);
        cb_remember = (CheckBox) findViewById(R.id.cb_remember);
        cb_auto = (CheckBox) findViewById(R.id.cb_autoLogin);

        sharedPreferences = getSharedPreferences(Constant.KEY_USER_PREF,MODE_PRIVATE);
        rememberUsername = sharedPreferences.getBoolean(Constant.KEY_REMEMBER,false);
        autoLogin = sharedPreferences.getBoolean(Constant.KEY_AUTO_LOGIN,false);
        cb_remember.setChecked(rememberUsername);
        cb_auto.setChecked(autoLogin);
        if ( autoLogin ) {
            et_email.setText(sharedPreferences.getString(Constant.KEY_USERNAME,""));
            et_password.setText(sharedPreferences.getString(Constant.USER_PASSWORD,""));
            attemptLogin();
        } else if (rememberUsername) {
            et_email.setText(sharedPreferences.getString(Constant.KEY_USERNAME,""));
            et_password.requestFocus();
        }

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(et_email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptRegister() {
        if (requestAction != null) {
            Log.d("REGISTER","another request in being sent...");
            return;
        }

        // Reset errors.
        et_email.setError(null);
        et_password.setError(null);

        this.email = et_email.getText().toString();
        this.password = et_password.getText().toString();
        String confirm_pwd = et_passwordConfirm.getText().toString();


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
        if (!password.equals(confirm_pwd)) {
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
            User user = new User(this.email,this.password);
            String credentials = user.toJson();
            requestAction = new RequestAction() {
                @Override
                public void actOnPre() {
                    showProgress(true);
                }

                @Override
                public void actOnPost(int responseCode, String response) {
                    Log.d("Register response",response);
                    showProgress(false);
                    if ( responseCode == 201 ) {
                        isLogin = true;
                        et_passwordConfirm.setVisibility(View.GONE);
                        btn_register.setText(R.string.action_register);
                        btn_signIn.setText(R.string.action_sign_in);
                        et_password.requestFocus();
                        Toast.makeText(LoginActivity.this,"Registration Succeeded!",Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString(Constant.KEY_MSG);
                            et_email.setError(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("LOGIN","unexpected response");
                            et_email.setError(response);
                        }
                        et_email.requestFocus();
                    }
                    requestAction = null;
                }
            };
            Log.d("LOGIN_TEST","registering...");
            String url = UrlHelper.urlRegister();
            RequestHelper.sendPostRequest(url,credentials,requestAction);
        }

    }

    private void attemptLogin() {
        if (requestAction != null) {
            Log.d("LOGIN","another request in being sent...");
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
            editor.putBoolean(Constant.KEY_REMEMBER,cb_remember.isChecked());
            editor.putBoolean(Constant.KEY_AUTO_LOGIN,cb_auto.isChecked());
            if ( cb_auto.isChecked() ) {
                editor.putString(Constant.KEY_USERNAME,email);
                editor.putString(Constant.USER_PASSWORD,password);
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
                    Log.d("Response",response);
                    showProgress(false);
                    if ( responseCode == 200 ) {
                        Intent intent = new Intent(LoginActivity.this,PlayerActivity.class);
                        intent.putExtra(Constant.PLAYER_EMAIL,email);
                        startActivity(intent);
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
                            Log.e("LOGIN","unexpected response");
                            et_password.setError(response);
                        }
                        et_password.requestFocus();
                    }
                    requestAction = null;
                }
            };
            Log.d("LOGIN_TEST","login...");
            String url = UrlHelper.urlLogin();
            RequestHelper.sendPostRequest(url,credentials,requestAction);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

            mLoginView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginView.setVisibility(show ? View.GONE : View.VISIBLE);
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
}