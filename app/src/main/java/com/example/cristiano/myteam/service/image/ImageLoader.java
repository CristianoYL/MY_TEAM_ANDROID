package com.example.cristiano.myteam.service.image;

/**
 * Created by Cristiano on 2017/6/1.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.database.LocalDBHelper;
import com.example.cristiano.myteam.service.aws.MyAmazonS3Service;

public class ImageLoader extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = "ImageLoader";

    String url;
    ImageView imageView;
    ProgressBar progressBar;
    LocalDBHelper dbHelper;
    Context context;
    MyAmazonS3Service myAmazonS3Service;
    MyAmazonS3Service.OnUploadResultListener listener;
    Bitmap bitmap;

    public ImageLoader(ImageView imageView, String url, ProgressBar progressBar, Context context){
        this.imageView = imageView;
        this.progressBar = progressBar;
        this.dbHelper = LocalDBHelper.getInstance(context);
        this.context = context;
        this.url = url;
    }

    public void loadImage(){
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        if ( progressBar != null ) {
//            progressBar.setVisibility(View.VISIBLE);
//        }
        this.imageView.setImageResource(R.drawable.ic_image_default_background);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        // try to use local cache first
        bitmap = this.dbHelper.getCachedImage(url);
        if ( bitmap != null ) {
            Log.d(TAG,"Use cached image for " + url);
            return bitmap;
        }
        // if no cache found, load from internet
        Log.d(TAG,"Load image from internet.");
        listener = new MyAmazonS3Service.OnUploadResultListener() {
            @Override
            public void onFinished(int responseCode, String message) {
                if ( responseCode == 200 ) {
                    bitmap = dbHelper.getCachedImage(url);
                    imageView.setImageBitmap(bitmap);
                    Log.d(TAG,message);
                } else {
                    Log.e(TAG,"Failed to load image:"+url);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        };
        this.myAmazonS3Service = new MyAmazonS3Service(context,listener);
        myAmazonS3Service.downloadFromS3(url,true);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
//        if ( progressBar != null ) {
//            progressBar.setVisibility(View.INVISIBLE);
//        }
        if ( bitmap != null && imageView.getVisibility() == View.VISIBLE ) {
            imageView.setImageBitmap(bitmap);
        }
    }
}

