package com.example.cristiano.myteam.util;

/**
 * Created by Cristiano on 2017/6/1.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    String url;
    ImageView imageView;

    public ImageLoader(ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        InputStream is;
        Bitmap bitmap = null;
        url = params[0];
        try {
            is = new URL(url).openStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 0;
            bitmap = BitmapFactory.decodeStream(is,null,options);
            Log.d("IMAGE LOADER","load image");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if ( bitmap != null ) {
            imageView.setImageBitmap(bitmap);
        } else {
            Log.e("IMAGE LOADER","Null bitmap loaded");
        }
    }
}

