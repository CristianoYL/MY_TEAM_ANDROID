package com.example.cristiano.myteam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerRegistrationActivity extends AppCompatActivity {

    private String email;
    private Player player;

    private EditText et_firstName, et_lastName, et_displayName, et_age, et_phone, et_height, et_weight;
    private Switch sw_unit, sw_leftFooted;
    private Spinner sp_role, sp_position;
    private ArrayAdapter<String> roleAdapter,positionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_registration);
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
        this.roleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, Constant.roles);
        sp_role.setAdapter(this.roleAdapter);
        this.positionAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, Constant.positions);
        sp_role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ( (sp_role.getItemAtPosition(i)).equals(Constant.ROLE_PLAYER)){
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
        this.email = bundle.getString(Constant.PLAYER_EMAIL,null);
        if ( bundle.containsKey(Constant.KEY_PLAYER_INFO) ) {
            setTitle("Update Profile");
            this.player = (Player) bundle.get(Constant.KEY_PLAYER_INFO);
            if ( this.player == null ) {
                Log.e("PlayerRegActivity","Missing player info bundle");
                return;
            }
            et_firstName.setText(this.player.getFirstName());
            et_lastName.setText(this.player.getLastName());
            et_displayName.setText(this.player.getDisplayName());
            et_age.setText(player.getAge()+"");
            et_phone.setText(this.player.getPhone());
            et_height.setText(player.getHeight()+"");
            et_weight.setText(player.getWeight()+"");
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_upload);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPlayerInfo();
            }
        });
    }

    private void uploadPlayerInfo() {
        int id = 0;
        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String displayName = et_displayName.getText().toString();
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
            weight = (weight / 1.6f);
            height = height * 30.3f;
        }
        boolean leftFooted = sw_leftFooted.isChecked();
        int avatar = 0;

        Player player = new Player(id,email,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar);
        RequestAction actionPutPlayer = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 201 || responseCode == 200 ) {    // player created or updated
                    if ( responseCode == 200 ) {
                        Toast.makeText(PlayerRegistrationActivity.this,"Profile updated!",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayerRegistrationActivity.this,"Profile created!",Toast.LENGTH_SHORT).show();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String email = jsonObject.getString(Constant.PLAYER_EMAIL);
                        Intent intent = new Intent(PlayerRegistrationActivity.this,PlayerActivity.class);
                        intent.putExtra(Constant.PLAYER_EMAIL, email);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {    // unknown error
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        Toast.makeText(PlayerRegistrationActivity.this,message,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PlayerRegistrationActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                    Log.e("PlayerActivity","Error in loading player profile.\nError Message:\n" + response);
                }
            }
        };
        String url = UrlHelper.urlPutPlayer(email);
        RequestHelper.sendPutRequest(url, player.toJson() ,actionPutPlayer);
        finish();
    }
}
