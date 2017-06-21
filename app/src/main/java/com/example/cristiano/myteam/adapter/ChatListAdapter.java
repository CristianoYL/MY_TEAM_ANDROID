package com.example.cristiano.myteam.adapter;

/**
 * Created by Cristiano on 2017/6/1.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.structure.Chat;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.request.ImageLoader;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class ChatListAdapter extends BaseAdapter {

    private int selfID;
    private List<Chat> chatMessages;
    private Context context;


    public class ViewHolder {
        public ImageView iv_otherAvatar,iv_selfAvatar, iv_otherImage, iv_selfImage;
        public TextView tv_otherText, tv_selfText, tv_otherName ,tv_selfName, tv_time;
        public ProgressBar pb_loadChat;

        ViewHolder(View view) {
            this.iv_otherAvatar = (ImageView) view.findViewById(R.id.iv_otherAvatar);
            this.iv_selfAvatar = (ImageView) view.findViewById(R.id.iv_selfAvatar);
            this.tv_otherName = (TextView) view.findViewById(R.id.tv_otherName);
            this.tv_selfName = (TextView) view.findViewById(R.id.tv_selfName);
            this.iv_otherImage = (ImageView) view.findViewById(R.id.iv_otherImage);
            this.iv_selfImage = (ImageView) view.findViewById(R.id.iv_selfImage);
            this.tv_otherText = (TextView) view.findViewById(R.id.tv_otherText);
            this.tv_selfText = (TextView) view.findViewById(R.id.tv_selfText);
            this.tv_time = (TextView) view.findViewById(R.id.tv_time);
            this.pb_loadChat = (ProgressBar) view.findViewById(R.id.pb_loadChat);
        }
    }



    public ChatListAdapter(@NonNull Context context,@NonNull List<Chat> chatMessages, int selfID) {
        this.context = context;
        this.selfID = selfID;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        return this.chatMessages.size();
    }

    @Override
    public Chat getItem(int position) {
        return this.chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        Chat chat = this.chatMessages.get(position);
        boolean isSelf = (chat.senderID == this.selfID );
        if ( convertView == null ) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_card_chat,null,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            Date date = Constant.getServerDateFormat().parse(chat.time);
            String time = Constant.DISPLAY_DATE_FORMAT.format(date);
            viewHolder.tv_time.setText(time);  // set time
        } catch (ParseException e) {
            e.printStackTrace();
            viewHolder.tv_time.setText(chat.time);  // set time
        }
        switch ( chat.messageType ) {
            case Constant.MESSAGE_TYPE_TEXT:
                setVisibility(isSelf,true,viewHolder);
                if ( isSelf ) {
                    viewHolder.tv_selfText.setText(chat.messageContent);
                } else {
                    viewHolder.tv_otherText.setText(chat.messageContent);
                }
                break;
            case Constant.MESSAGE_TYPE_IMAGE:
                setVisibility(isSelf,false,viewHolder);
                ImageLoader imageLoader;
                if ( isSelf ) {
                    imageLoader = new ImageLoader(viewHolder.iv_selfImage,chat.messageContent,viewHolder.pb_loadChat,context);
                } else {
                    imageLoader = new ImageLoader(viewHolder.iv_otherImage,chat.messageContent,viewHolder.pb_loadChat,context);
                }
                imageLoader.execute();
                break;
        }
        if ( isSelf ) {  // received message
            viewHolder.iv_selfAvatar.setImageResource(R.drawable.avatar_peter);
            viewHolder.tv_selfName.setText(chat.senderName);
        } else {    // sent message
            viewHolder.iv_otherAvatar.setImageResource(R.drawable.avatar_rooney);
            viewHolder.tv_otherName.setText(chat.senderName);
        }
        return convertView;
    }

    private void setVisibility(boolean isSelf, boolean isText, ViewHolder viewHolder) {
        if ( isText ) { // the message is text
            // hide ImageViews
            viewHolder.iv_otherImage.setVisibility(View.INVISIBLE);
            viewHolder.iv_selfImage.setVisibility(View.INVISIBLE);
            if ( isSelf ) { // if it's a sent text message
                viewHolder.iv_otherAvatar.setVisibility(View.INVISIBLE);  // hide sender icon
                viewHolder.tv_otherName.setVisibility(View.INVISIBLE);    // hide sender name
                viewHolder.tv_otherText.setVisibility(View.INVISIBLE);   // hide received TextView
                viewHolder.iv_selfAvatar.setVisibility(View.VISIBLE);  // show self icon
                viewHolder.tv_selfName.setVisibility(View.VISIBLE);    // show self name
                viewHolder.tv_selfText.setVisibility(View.VISIBLE);    // show self TextView
            } else {    // if it's a received text message
                viewHolder.iv_otherAvatar.setVisibility(View.VISIBLE);  // show sender icon
                viewHolder.tv_otherName.setVisibility(View.VISIBLE);    // show sender name
                viewHolder.tv_otherText.setVisibility(View.VISIBLE);    // show received TextView
                viewHolder.iv_selfAvatar.setVisibility(View.INVISIBLE);  // hide self icon
                viewHolder.tv_selfName.setVisibility(View.INVISIBLE);    // hide self name
                viewHolder.tv_selfText.setVisibility(View.INVISIBLE);       // hide self TextView
            }
        } else {
            // hide TextViews
            viewHolder.tv_otherText.setVisibility(View.INVISIBLE);
            viewHolder.tv_selfText.setVisibility(View.INVISIBLE);
            if ( isSelf ) { // if it's a sent image message
                viewHolder.iv_otherAvatar.setVisibility(View.INVISIBLE);  // hide sender icon
                viewHolder.tv_otherName.setVisibility(View.INVISIBLE);    // hide sender name
                viewHolder.iv_otherImage.setVisibility(View.INVISIBLE);   // hide received ImageView
                viewHolder.iv_selfAvatar.setVisibility(View.VISIBLE);  // show self icon
                viewHolder.tv_selfName.setVisibility(View.VISIBLE);    // show self name
                viewHolder.iv_selfImage.setVisibility(View.VISIBLE);    // show self ImageView
            } else {    // if it's a received image message
                viewHolder.iv_otherAvatar.setVisibility(View.VISIBLE);  // show sender icon
                viewHolder.tv_otherName.setVisibility(View.VISIBLE);    // show sender name
                viewHolder.iv_otherImage.setVisibility(View.VISIBLE);    // show received ImageView
                viewHolder.iv_selfAvatar.setVisibility(View.INVISIBLE);  // hide self icon
                viewHolder.tv_selfName.setVisibility(View.INVISIBLE);    // hide self name
                viewHolder.iv_selfImage.setVisibility(View.INVISIBLE);       // hide self ImageView
            }
        }
    }
}
