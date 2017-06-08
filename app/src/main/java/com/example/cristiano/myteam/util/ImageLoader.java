package com.example.cristiano.myteam.util;

/**
 * Created by Cristiano on 2017/6/1.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.cristiano.myteam.adapter.ChatListAdapter;
import com.example.cristiano.myteam.database.LocalDBHelper;
import com.example.cristiano.myteam.request.RequestAction;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageLoader extends AsyncTask<Void, Void, Bitmap> {
    String url;
    ImageView imageView;
    ProgressBar progressBar;
    LocalDBHelper dbHelper;

    public ImageLoader(ImageView imageView, String url, ProgressBar progressBar, Context context){
        this.imageView = imageView;
        this.progressBar = progressBar;
        this.dbHelper = new LocalDBHelper(context);
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if ( progressBar != null ) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        // try to use local cache first
        Bitmap bitmap = this.dbHelper.getCachedImage(url);
        if ( bitmap != null ) {
            Log.d("ImageLoader","Use cached image.");
            return bitmap;
        }
        // if not cache found, load from internet
        Log.d("ImageLoader","Load image from internet.");
        InputStream is;
        try {
            is = new URL(url).openStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 0;
            bitmap = BitmapFactory.decodeStream(is,null,options);
            if ( !this.dbHelper.cacheImage(url,bitmap) ) {
                Log.e("ImageLoader","Cache image failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if ( progressBar != null ) {
            progressBar.setVisibility(View.GONE);
        }
        if ( bitmap != null && imageView.getVisibility() == View.VISIBLE ) {
            imageView.setImageBitmap(bitmap);
        } else {
            if ( bitmap == null ) {
                Log.e("ImageLoader","Null bitmap loaded");
            }
        }
    }
}

