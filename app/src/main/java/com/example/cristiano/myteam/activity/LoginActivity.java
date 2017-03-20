package com.example.cristiano.myteam.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

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
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.cristiano.myteam.R;

import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.ParamFactory;

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
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView et_email;
    private EditText et_password;
    private EditText et_passwordConfirm;
    private Button btn_signIn;
    private Button btn_register;
    private View pb_progress;
    private View mLoginFormView;
    private boolean isLogin;
    HashMap<String,String> playerInfo = new HashMap<>();


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
        mLoginFormView = findViewById(R.id.login_form);
        pb_progress = findViewById(R.id.login_progress);
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
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        et_email.setError(null);
        et_password.setError(null);

        // Store values at the time of the login attempt.
        playerInfo.clear();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            et_email.setError(getString(R.string.error_field_required));
            focusView = et_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            et_email.setError(getString(R.string.error_invalid_email));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            playerInfo.put(Constant.PLAYER_EMAIL,email);
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private final String mEmail;
        private final String mPassword;

        class LoginCredential {
            String email;
            String password;
            LoginCredential(String email, String password) {
                this.email = email;
                this.password = password;
            }
        }

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection httpURLConnection = null;
            String response = null;
            try {
                URL url = new URL(Constant.URL_LOGIN);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, Constant.SERVER_CHARSET));
                ParamFactory.put("email",mEmail);
                ParamFactory.put("password",mPassword);
                String data = ParamFactory.parseParams();
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ( (response = reader.readLine()) != null ) {
                    stringBuilder.append(response);
                    stringBuilder.append("\n");
                }
                response = stringBuilder.toString();
                Log.d("RESPONSE",stringBuilder.toString());
            } catch (MalformedURLException e) {
                Log.d("problem", "p");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("problem2", "p");
                e.printStackTrace();
            } finally {
                if ( httpURLConnection != null ) {
                    httpURLConnection.disconnect();
                }
                Log.d("TEST","Send request");
            }
            return response;
//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    if ( pieces[1].equals(mPassword) ) {
//                        Log.d("TEST","Login succeeded!");
//                    } else {
//                        Log.d("TEST","Login failed!");
//                    }
//                    return pieces[1].equals(mPassword);
//                }
//            }
//
//            // TODO: register the new account here.
//            Log.d("TEST","Registering...");
        }

        @Override
        protected void onPostExecute(final String response) {
            mAuthTask = null;
            showProgress(false);
            // use sample data to display TODO: use user data retrieved from server
            if ( response != null && response.contains("login success")) {
                playerInfo.put(Constant.PLAYER_DISPLAY_NAME,"Peter Griffin");
                playerInfo.put(Constant.PLAYER_ROLE,"Forward");
                playerInfo.put(Constant.PLAYER_CLUB,"New England");
                playerInfo.put(Constant.PLAYER_AGE,"40");
                playerInfo.put(Constant.PLAYER_WEIGHT,"250");
                playerInfo.put(Constant.PLAYER_HEIGHT,"180");
                playerInfo.put(Constant.STATS_APPEARANCE,"10");
                playerInfo.put(Constant.STATS_WIN,"0");
                playerInfo.put(Constant.STATS_DRAW,"0");
                playerInfo.put(Constant.STATS_LOSS,"10");
                playerInfo.put(Constant.STATS_GOAL,"0");
                playerInfo.put(Constant.STATS_ASSIST,"0");
                playerInfo.put(Constant.STATS_YELLOW,"6");
                playerInfo.put(Constant.STATS_RED,"10");
                ArrayList<String> selectedStats = new ArrayList<>(8);
                selectedStats.add(Constant.STATS_APPEARANCE);
                selectedStats.add(Constant.STATS_WIN);
                selectedStats.add(Constant.STATS_DRAW);
                selectedStats.add(Constant.STATS_LOSS);
                selectedStats.add(Constant.STATS_GOAL);
                selectedStats.add(Constant.STATS_ASSIST);
                selectedStats.add(Constant.STATS_YELLOW);
                selectedStats.add(Constant.STATS_RED);
                // put sample data into Intent and navigate to PlayerActivity
                Intent intent = new Intent(LoginActivity.this,PlayerActivity.class);
                intent.putExtra(Constant.PLAYER_SELECTED_STATS,selectedStats);
                intent.putExtra(Constant.PLAYER_INFO, playerInfo);
                startActivity(intent);
            } else if ( response != null && response.contains("does not exist") ) {
                et_email.setError(getString(R.string.error_account_not_exists));
                et_email.requestFocus();
            } else if ( response != null && response.contains("not matching") ){
                et_password.setError(getString(R.string.error_incorrect_password));
                et_password.requestFocus();
            } else if ( response != null && response.contains("unable to connect") ){
                et_password.setError(getString(R.string.error_connection_fail));
                et_password.requestFocus();
            } else {
                et_password.setError(getString(R.string.error_incorrect_password));
                et_password.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

