package com.example.cristiano.myteam.adapter;

/**
 * Created by Cristiano on 2017/6/1.
 */

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.util.ImageLoader;

import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter {
    public static final String SENDER = "sender";
    public static final String SELF = "self";
    public static final String MESSAGE_CONTENT = "message";
    public static final String MESSAGE_TYPE = "type";
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2;
    int layoutRes;
    private Context context;


    class ViewHolder {
        ImageView iv_sender_icon, iv_self_icon, iv_image_received, iv_image_sent;
        TextView tv_text_received, tv_text_sent;
    }

    public ChatListAdapter(@NonNull Context context, @LayoutRes int resource,@NonNull List objects) {
        super(context, resource, objects);
        this.layoutRes = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if ( convertView == null ) {
            convertView = LayoutInflater.from(context).inflate(layoutRes,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_sender_icon = (ImageView) convertView.findViewById(R.id.iv_sender);
            viewHolder.iv_self_icon = (ImageView) convertView.findViewById(R.id.iv_self);
            viewHolder.tv_text_received = (TextView) convertView.findViewById(R.id.tv_message_received);
            viewHolder.tv_text_sent = (TextView) convertView.findViewById(R.id.tv_message_sent);
            viewHolder.iv_image_received = (ImageView) convertView.findViewById(R.id.iv_image_received);
            viewHolder.iv_image_sent = (ImageView) convertView.findViewById(R.id.iv_image_sent);
            convertView.setTag(viewHolder);
            Log.d("ADAPTER","New view");
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            Log.d("ADAPTER","Cached view");
        }
        try {
            HashMap<String,Object> messageMap = (HashMap<String, Object>) getItem(position);
            if ( messageMap != null ) {
                int senderRes, selfRes;
                TextView textView;
                ImageView imageView;
                boolean isReceived, isText;
                if ( messageMap.containsKey(SENDER)) {  // received message
                    senderRes = (int) messageMap.get(SENDER);
                    viewHolder.iv_sender_icon.setImageResource(senderRes);
                    isReceived = true;
                    textView = viewHolder.tv_text_received;
                    imageView = viewHolder.iv_image_received;
                } else {    // sent message
                    selfRes = (int) messageMap.get(SELF);
                    viewHolder.iv_self_icon.setImageResource(selfRes);
                    isReceived = false;
                    textView = viewHolder.tv_text_sent;
                    imageView = viewHolder.iv_image_sent;
                }
                int type = (int) messageMap.get(MESSAGE_TYPE);
                String message = (String) messageMap.get(MESSAGE_CONTENT);
                switch ( type ) {
                    case TYPE_TEXT:
                        isText = true;
                        setVisibility(isReceived,isText,viewHolder);
                        textView.setText(message);
                        break;
                    case TYPE_IMAGE:
                        isText = false;
                        setVisibility(isReceived,isText,viewHolder);
                        ImageLoader imageLoader = new ImageLoader(imageView);
                        imageLoader.execute(message);
                        break;
                }
            }
        } catch (ClassCastException e ) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void setVisibility(boolean isReceived, boolean isText, ViewHolder viewHolder) {
        if ( isText ) { // the message is text
            // hide ImageViews
            viewHolder.iv_image_received.setVisibility(View.GONE);
            viewHolder.iv_image_sent.setVisibility(View.GONE);
            if ( isReceived ) { // if it's a received text message
                viewHolder.iv_sender_icon.setVisibility(View.VISIBLE);  // show sender icon
                viewHolder.iv_self_icon.setVisibility(View.INVISIBLE);  // hide self icon
                viewHolder.tv_text_received.setVisibility(View.VISIBLE);    // show received TextView
                viewHolder.tv_text_sent.setVisibility(View.GONE);       // hide self TextView
            } else {    // if it's a sent text message
                viewHolder.iv_sender_icon.setVisibility(View.INVISIBLE);  // hide sender icon
                viewHolder.iv_self_icon.setVisibility(View.VISIBLE);  // show self icon
                viewHolder.tv_text_received.setVisibility(View.GONE);   // hide received TextView
                viewHolder.tv_text_sent.setVisibility(View.VISIBLE);    // show self TextView
            }
        } else {
            // hide TextViews
            viewHolder.tv_text_received.setVisibility(View.GONE);
            viewHolder.tv_text_sent.setVisibility(View.GONE);
            if ( isReceived ) { // if it's a received image message
                viewHolder.iv_sender_icon.setVisibility(View.VISIBLE);  // show sender icon
                viewHolder.iv_self_icon.setVisibility(View.INVISIBLE);  // hide self icon
                viewHolder.iv_image_received.setVisibility(View.VISIBLE);    // show received ImageView
                viewHolder.iv_image_sent.setVisibility(View.GONE);       // hide self ImageView
            } else {    // if it's a sent image message
                viewHolder.iv_sender_icon.setVisibility(View.INVISIBLE);  // hide sender icon
                viewHolder.iv_self_icon.setVisibility(View.VISIBLE);  // show self icon
                viewHolder.iv_image_received.setVisibility(View.GONE);   // hide received ImageView
                viewHolder.iv_image_sent.setVisibility(View.VISIBLE);    // show self ImageView
            }
        }
    }
}
