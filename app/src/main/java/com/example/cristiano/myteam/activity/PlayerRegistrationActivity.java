package com.example.cristiano.myteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.database.LocalDBHelper;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.service.aws.MyAmazonS3Service;
import com.example.cristiano.myteam.service.image.ImageLoader;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;


/**
 *  This activity handles the player profile registration/update requests
 */
public class PlayerRegistrationActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 1;
    private static final String TAG = "PlayerRegActivity";

    private String jwt;
    private Player player;

    private String avatarUrl = "default";
    private Bitmap avatarBitmap;

    private EditText et_firstName, et_lastName, et_displayName, et_age, et_phone, et_height, et_weight;
    private Switch sw_unit, sw_leftFooted;
    private Spinner sp_role, sp_position;
    private Button btn_selectAvatar;
    private ImageView iv_avatar;
    private ArrayAdapter<String> roleAdapter,positionAdapter;
    private Resources resources;
    private MyAmazonS3Service s3Service;
    private MyAmazonS3Service.OnUploadResultListener onUploadResultListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_registration);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        btn_selectAvatar = (Button) findViewById(R.id.btn_select_avatar);
        et_firstName = (EditText) findViewById(R.id.et_firstName);
        et_lastName = (EditText) findViewById(R.id.et_lastName);
        et_displayName = (EditText) findViewById(R.id.et_displayName);
        et_age = (EditText) findViewById(R.id.et_age);
        et_height = (EditText) findViewById(R.id.et_height);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_phone = (EditText) findViewById(R.id.et_phone);
        sw_unit = (Switch) findViewById(R.id.sw_unit);
        sw_leftFooted = (Switch) findViewById(R.id.sw_leftFooted);
        sp_role = (Spinner) findViewById(R.id.sp_role);
        sp_position = (Spinner) findViewById(R.id.sp_position);
        resources = getResources();
        this.roleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.array_user_roles));
        sp_role.setAdapter(this.roleAdapter);
        this.positionAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.array_player_positions));
        sp_role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ( (sp_role.getItemAtPosition(i)).equals(resources.getString(R.string.role_player))){
                    sp_position.setVisibility(View.VISIBLE);
                } else {
                    sp_position.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_position.setAdapter(this.positionAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_club);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if ( bundle == null ) {
            Log.e("PlayerRegActivity","Missing player bundle");
            return;
        }
        this.jwt = bundle.getString(Constant.USER_ACCESS_TOKEN,null);  // creating the player for the first time
        if ( bundle.containsKey(Constant.KEY_PLAYER) ) {    // updating an existing player
            setTitle("Update Profile");
            this.player = new Gson().fromJson(bundle.getString(Constant.KEY_PLAYER),Player.class);
            if ( this.player == null ) {
                Log.e("PlayerRegActivity","Missing player info");
                return;
            }
            et_firstName.setText(this.player.getFirstName());
            et_lastName.setText(this.player.getLastName());
            et_displayName.setText(this.player.getDisplayName());
            et_age.setText(String.format(Locale.US,"%d",player.getAge()));
            et_phone.setText(this.player.getPhone());
            et_height.setText(String.format(Locale.US,"%f",player.getHeight()));
            et_weight.setText(String.format(Locale.US,"%f",player.getWeight()));
            sw_leftFooted.setChecked(this.player.isLeftFooted());
            if ( this.player.getRole().equals(Constant.ROLE_MANAGER) ) {
                sp_role.setSelection(roleAdapter.getPosition(Constant.ROLE_MANAGER));
                sp_position.setVisibility(View.INVISIBLE);
            } else {
                sp_role.setSelection(roleAdapter.getPosition(Constant.ROLE_PLAYER));
                sp_position.setVisibility(View.VISIBLE);
                sp_position.setSelection(positionAdapter.getPosition(this.player.getRole()));
            }
        }
        btn_selectAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAvatar();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_upload);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPlayerInfo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( onUploadResultListener == null ) {
            onUploadResultListener= new MyAmazonS3Service.OnUploadResultListener() {
                @Override
                public void onFinished(int responseCode, String message) {
                    Log.d(TAG,"responseCode=" + responseCode + ", message=" + message);
                    if ( responseCode == 200 ) { // upload image to S3 succeeded
                        avatarUrl = message;
                        ImageLoader loader = new ImageLoader(iv_avatar,message,null,
                                PlayerRegistrationActivity.this);
                        loader.loadImage();
                        LocalDBHelper.getInstance(PlayerRegistrationActivity.this).clearImageCache(message);
                    } else {    // upload image to S3 failed
                        Toast.makeText(PlayerRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        if ( s3Service == null ) {
            s3Service = new MyAmazonS3Service(PlayerRegistrationActivity.this,onUploadResultListener);
        }
        Log.d(TAG,"Register upload result listener");
    }

    /**
     * send a PUT request to register/update the player's profile
     */
    private void uploadPlayerInfo() {
        int id = 0;
        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String displayName = et_displayName.getText().toString();
        // firstname and lastname cannot be empty
        if ( firstName.length() == 0 || lastName.length() == 0 ) {
            Toast.makeText(PlayerRegistrationActivity.this,R.string.empty_name_msg,Toast.LENGTH_SHORT).show();
            return;
        }
        // display name is full name by default
        if ( displayName.length() == 0 ) {
            displayName = firstName + " " + lastName;
        }
        String role = sp_role.getSelectedItem().toString();
        if ( role.equals(Constant.ROLE_PLAYER) ) {
            role =  sp_position.getSelectedItem().toString();
        }
        String phone = et_phone.getText().toString();
        int age = 0;
        if ( et_age.getText().length() > 0 ) {
            age = Integer.parseInt(et_age.getText().toString());
        }
        float weight = 0;
        if ( et_weight.getText().length() > 0 ) {
            weight = Float.parseFloat(et_weight.getText().toString());
        }
        float height = 0;
        if ( et_height.getText().length() > 0 ) {
            height = Float.parseFloat(et_height.getText().toString());
        }
        if ( sw_unit.isChecked() ) {
            weight = weight * 0.45359237f;  // convert lbs to kg
            height = height * 30.48f;   // convert ft to cm
        }
        boolean leftFooted = sw_leftFooted.isChecked();
        int userID = 0;

        Player player = new Player(id,userID,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatarUrl);
        RequestAction actionRegPlayer = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {    // player profile updated
                    // simply finish the registration activity and go back to previous activity
                    Toast.makeText(PlayerRegistrationActivity.this,R.string.player_profile_updated,Toast.LENGTH_SHORT).show();
                } else if ( responseCode == 201 ){  // created new player profile
                    Toast.makeText(PlayerRegistrationActivity.this,R.string.player_profile_created,Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonPlayer = new JSONObject(response);
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
                        Intent intent = new Intent(PlayerRegistrationActivity.this,MainActivity.class);
                        intent.putExtra(Constant.TABLE_PLAYER, player.toJson());
                        startActivity(intent);  // start the MainActivity with the created player's ID
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {    // error
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constant.KEY_MSG);
                        Toast.makeText(PlayerRegistrationActivity.this,message,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PlayerRegistrationActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                    Log.e("MainActivity","Error in loading player profile.\nError Message:\n" + response);
                }
                finish();   // finish PlayerRegistration activity after request sent, clear it in stack to prevent navigate back to it
            }
        };
        String url;
        if ( this.player != null ) {
            url = UrlHelper.urlPlayerByID(this.player.getId());
            RequestHelper.sendPutRequest(url, player.toJson() ,actionRegPlayer);
        } else if ( this.jwt != null ){
            url = UrlHelper.urlPlayerByToken();
            RequestHelper.sendPostRequest(url, player.toJson(), jwt ,actionRegPlayer);
        }
    }

    private void selectAvatar(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
    }

    /**
     *  After user selecting the image from gallery, this method will be called with the
     *  selection result. Use the selection to create a InputStream and use the InputStream
     *  to upload the selected image to S3 asynchronously.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK
                && data != null ) {
            AsyncUploadAvatar asyncUploadAvatar = new AsyncUploadAvatar(data.getData());
            asyncUploadAvatar.upload();
        }
    }
    private class AsyncUploadAvatar extends AsyncTask<Void,Void,Void>{
        Uri uri;

        AsyncUploadAvatar(Uri uri) {
            this.uri = uri;
        }

        public void upload(){
            execute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            s3Service.uploadAvatar(uri,player.getId());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            avatarBitmap = null;
        }

    }
}
