package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.adapter.ChatListAdapter;
import com.example.cristiano.myteam.database.LocalDBHelper;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Chat;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cristiano on 2017/6/1.
 */

public class ChatFragment extends Fragment {
    private int tournamentID, clubID, receiverID, selfID, limit, offset;
    private ArrayList<Chat> chatArray;

    private final static int CHAT_LIMIT = 20;

    private View view_chat,view_functions;
    private FloatingActionButton fab_send,fab_more,fab_less,fab_gallery,fab_camera,fab_location;
    private ListView lv_chat;
    private EditText et_message;
    private ChatListAdapter adapter;

    /**
     * when creating a ChatFragment, use tournamentID, clubID, receiverID to specify whether
     * it's a tournament chat, club chat or private chat. And use selfID to identify oneself
     * @param tournamentID  ID of the tournament, if it's a tournament chat. set to 0 if not.
     * @param clubID    ID of the club, if it's a tournament/club chat. set to 0 if not.
     * @param receiverID    playerID of the receiver, if it's a private chat. set to 0 if not.
     * @param selfID    playerID of the user
     * @return  the ChatFragment
     */
    public static ChatFragment newInstance(int tournamentID, int clubID, int receiverID, int selfID){
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_TOURNAMENT_ID,tournamentID);
        bundle.putInt(Constant.KEY_CLUB_ID,clubID);
        bundle.putInt(Constant.KEY_RECEIVER_ID,receiverID);
        bundle.putInt(Constant.KEY_SENDER_ID,selfID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tournamentID = bundle.getInt(Constant.KEY_TOURNAMENT_ID);
            clubID = bundle.getInt(Constant.KEY_CLUB_ID);
            receiverID = bundle.getInt(Constant.KEY_RECEIVER_ID);
            selfID = bundle.getInt(Constant.KEY_SENDER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_chat = inflater.inflate(R.layout.fragment_chatroom, container, false);
        showChatPage();
        return view_chat;
    }

    /**
     * render the chat page and show chat messages
     */
    private void showChatPage(){
        loadChatHistory(CHAT_LIMIT,0);  // load CHAT_LIMIT (20) latest (0 offset) messages
        fab_send = (FloatingActionButton) view_chat.findViewById(R.id.fab_send);
        fab_more = (FloatingActionButton) view_chat.findViewById(R.id.fab_more);
        fab_less = (FloatingActionButton) view_chat.findViewById(R.id.fab_less);
        view_functions = view_chat.findViewById(R.id.layout_function_menu);
        et_message = (EditText) view_chat.findViewById(R.id.et_message);

        et_message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( hasFocus ) {
                    if ( adapter != null ) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextMessage(et_message.getText().toString());
            }
        });

        fab_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_more.setVisibility(View.GONE);
                view_functions.setVisibility(View.VISIBLE);
            }
        });

        fab_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_functions.setVisibility(View.GONE);
                fab_more.setVisibility(View.VISIBLE);
            }
        });

        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ( s != null && s.length() > 0 ) {
                    fab_send.setVisibility(View.VISIBLE);
                } else {
                    fab_send.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showMessages() {
        lv_chat = (ListView) view_chat.findViewById(R.id.lv_chat);
        adapter = new ChatListAdapter(getContext(),chatArray, selfID);
        lv_chat.setAdapter(adapter);
    }

    /**
     * send a text chat message and post it to the server
     * @param message the message to be sent
     */
    private void sendTextMessage(String message) {
        if ( message == null || message.length() < 1 ) {
            return;
        }
        DateFormat localDateFormat = Constant.getServerDateFormat();
        final Chat chat = new Chat(0,tournamentID,clubID,receiverID, selfID,"Me",
                Constant.MESSAGE_TYPE_TEXT,message,localDateFormat.format(new Date()));
        RequestAction actionPostChatText = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                chatArray.add(chat);
                adapter.notifyDataSetChanged();
                lv_chat.setSelection(adapter.getCount()-1);
                adapter.notifyDataSetChanged();
                et_message.setText("");
            }
        };
        String url;
        if ( tournamentID != 0 ) {  // tournament chat
            url = UrlHelper.urlPostTournamentChat(tournamentID,clubID);
        } else if ( clubID != 0 ) {// club chat
            url = UrlHelper.urlPostClubChat(clubID);
        } else if ( receiverID != 0 ) { // private chat
            url = UrlHelper.urlPostPrivateChat(receiverID);
        } else {
            Toast.makeText(getContext(), "Unknown error!", Toast.LENGTH_SHORT).show();
            Log.e("Chat Error","Unspecified chat type.");
            return;
        }
        RequestHelper.sendPostRequest(url,chat.toJson(),actionPostChatText);
    }

    /**
     * load the according chat messages
     * @param limit number of chat messages to load
     * @param offset  the starting point of the load
     */
    private void loadChatHistory(int limit, int offset){
        RequestAction actionGetChat = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.TABLE_CHAT);
                        chatArray = new ArrayList<>(jsonArray.length());
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            JSONObject jsonChat = json.getJSONObject(Constant.TABLE_CHAT);
                            int id = jsonChat.getInt(Constant.CHAT_ID);
                            int tournamentID = jsonChat.getInt(Constant.CHAT_TOURNAMENT_ID);
                            int clubID = jsonChat.getInt(Constant.CHAT_CLUB_ID);
                            int receiverID = 0;
                            int senderID = jsonChat.getInt(Constant.CHAT_SENDER_ID);
                            String messageType = jsonChat.getString(Constant.CHAT_MESSAGE_TYPE);
                            String messageContent = jsonChat.getString(Constant.CHAT_MESSAGE_CONTENT);
                            String time = jsonChat.getString(Constant.CHAT_TIME);
                            String senderName = json.getString(Constant.CHAT_SENDER_NAME);
                            chatArray.add(0,new Chat(id,tournamentID,clubID,receiverID,senderID,
                                    senderName,messageType,messageContent,time));
                        }
                        showMessages();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        String url = UrlHelper.urlGetChat(tournamentID,clubID,receiverID,selfID,limit,offset);
        RequestHelper.sendGetRequest(url,actionGetChat);
    }
}
