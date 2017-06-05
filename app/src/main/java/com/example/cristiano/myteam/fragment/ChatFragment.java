package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.adapter.ChatListAdapter;
import com.example.cristiano.myteam.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Cristiano on 2017/6/1.
 */

public class ChatFragment extends Fragment {
    private int tournamentID, clubID, playerID;

    private View view_chat;
    private Button btn_send;
    private ListView lv_chat;
    private TextInputEditText et_message;
    ChatListAdapter adapter;

    public static ChatFragment newInstance(int tournamentID, int clubID, int playerID){
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_TOURNAMENT_ID,tournamentID);
        bundle.putInt(Constant.KEY_CLUB_ID,clubID);
        bundle.putInt(Constant.KEY_PLAYER_ID,playerID);
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
            playerID = bundle.getInt(Constant.KEY_PLAYER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_chat = inflater.inflate(R.layout.fragment_chatroom, container, false);
        showChatPage();
        return view_chat;
    }

    private void showChatPage(){
        loadMessage();
        btn_send = (Button) view_chat.findViewById(R.id.btn_send);
        et_message = (TextInputEditText) view_chat.findViewById(R.id.et_message);

        et_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lv_chat.setSelection(adapter.getCount()-1);
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextMessage(et_message.getText().toString());
            }
        });
    }

    private void sendTextMessage(String message) {
        if ( message == null || message.length() < 1 ) {
            return;
        }
        HashMap<String,Object> messageMap= new HashMap<>(3);
        messageMap.put(ChatListAdapter.SELF,R.drawable.avatar_peter);
        messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
        messageMap.put(ChatListAdapter.MESSAGE_CONTENT,message);
        ChatListAdapter adapter = (ChatListAdapter) lv_chat.getAdapter();
        adapter.add(messageMap);
        lv_chat.setAdapter(adapter);
        et_message.setText("");
    }

    private void loadMessage(){
        ArrayList<HashMap<String,Object>> chatArray = new ArrayList<>();
        HashMap<String,Object> messageMap;
        for ( int i=0; i<5; i++ ) {
            messageMap= new HashMap<>(3);
            messageMap.put(ChatListAdapter.SENDER,R.drawable.avatar_rooney);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"Hello!");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SENDER,R.drawable.avatar_rooney);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"My name is Adam");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SENDER,R.drawable.avatar_rooney);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_IMAGE);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"http://www.manutd.com/~/media/510AE241278B45FF97125DC1E1E32CBF.ashx");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SENDER,R.drawable.avatar_scholes);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"Hello Adam, my name is Bob.Nice to meet you.");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SENDER,R.drawable.avatar_scholes);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_IMAGE);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"https://cdn-arkquizzstoragelive.akamaized.net/cdn/1310e09e-7d5e-4efd-97a0-ebf63d923da0_manu.png");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SENDER,R.drawable.avatar_scholes);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"Nice to meet you too, Bob!");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SELF,R.drawable.avatar_peter);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"Hi Adam, it's Chris.");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SELF,R.drawable.avatar_peter);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"Welcome to our team.");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SENDER,R.drawable.avatar_rooney);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"Thanks Chris");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SELF,R.drawable.avatar_peter);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_IMAGE);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"http://video.skysports.com/Rjb2N3NDE6gSmGftlnA3elHE4kKLQ8uN/promo293543117");
            chatArray.add(messageMap);
            messageMap = new HashMap<>(3);
            messageMap.put(ChatListAdapter.SENDER,R.drawable.avatar_scholes);
            messageMap.put(ChatListAdapter.MESSAGE_TYPE,ChatListAdapter.TYPE_TEXT);
            messageMap.put(ChatListAdapter.MESSAGE_CONTENT,"!!");
            chatArray.add(messageMap);
        }
        lv_chat = (ListView) view_chat.findViewById(R.id.lv_chat);
        adapter = new ChatListAdapter(getContext(),R.layout.layout_card_chat,chatArray,lv_chat);
        lv_chat.setAdapter(adapter);
    }
}
