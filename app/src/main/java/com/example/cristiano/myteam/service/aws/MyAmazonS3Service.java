package com.example.cristiano.myteam.service.aws;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.support.media.ExifInterface;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.database.LocalDBHelper;
import com.example.cristiano.myteam.util.AppUtils;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by Cristiano on 2017/7/12.
 */

public class MyAmazonS3Service{

    private static final String TAG = "MyAmazonS3Service";
    private static final String DEFAULT_IMAGE_FORMAT = ".jpg";

    public interface OnUploadResultListener extends Serializable{
        void onFinished(int responseCode, String message);
    }

    private OnUploadResultListener onUploadResultListener;
    private int responseCode;
    private String message;
    private Context context;
    private TransferUtility transferUtility;

    private static MyAmazonS3Service instance = null;

    public MyAmazonS3Service(Context context,OnUploadResultListener listener){
        this.context = context;
        onUploadResultListener = listener;
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                Constant.S3_IDENTITY_POOL_ID, // Identity pool ID
                Regions.US_WEST_2 // Region
        );
        AmazonS3Client s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, context);
    }

    public void uploadAvatar(Uri imageUri, int playerID){
        String folder = UrlHelper.s3PlayerAvatarFolder(playerID);
        // always compress avatar
        uploadToS3(imageUri,folder + DEFAULT_IMAGE_FORMAT, true);
    }

    public void uploadChatImage(Uri imageUri, int tournamentID, int clubID, int receiverID,
                                 @IntRange(from=1) int senderID, boolean shouldCompress){
        Log.d(TAG,"upload chat image");
        String folder;
        if ( senderID <= 0 ) {
            responseCode = 400;
            message = context.getString(R.string.error_missing_sender);
            notifyListener(responseCode,message);
            return;
        }
        if ( tournamentID > 0 && clubID > 0 ) { // tournament chat
            folder = UrlHelper.s3TournamentChatFolder(tournamentID,clubID,senderID);
        } else if ( clubID > 0 ) {  // club chat
            folder = UrlHelper.s3ClubChatFolder(clubID,senderID);
        } else if ( receiverID > 0 ) {  // private chat
            folder = UrlHelper.s3PrivateChatFolder(receiverID,senderID);
        } else {
            responseCode = 400;
            message = context.getString(R.string.error_unknown_chat_type);
            notifyListener(responseCode,message);
            return;
        }
        uploadToS3(imageUri,folder + DEFAULT_IMAGE_FORMAT, shouldCompress);
    }

    /**
     *  This method takes an InputStream, which is created from a local image file, reads it and
     *  decode it as a bitmap (JPEG), retrieves its EXIF info and then uploads it to S3.
     *  If upload is successful, cache the image in local DB.
     * @param imageUri   the Uri of the selected image
     * @param fileName  the file name on the AWS S3 server
     * @param shouldCompress whether the image should be compressed
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void uploadToS3(Uri imageUri, final String fileName, boolean shouldCompress) {
        try
        {
            final String localPath = "Cache_"+System.currentTimeMillis()+ Constant.IMAGE_FORMAT; // name the cache file with currentTimeMillis
            File file = new File(AppUtils.getDiskCacheDir(context), /* available cache dir */
                    localPath);
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            // read the image file from inputStream
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            // copy the orientation to the new image
            bitmap = AppUtils.getRotatedBitmap(context,imageUri,bitmap);
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if ( shouldCompress ) {
                bitmap.compress(Bitmap.CompressFormat.JPEG,
                                Constant.IMAGE_COMPRESS_RATE /* compress */,
                                bos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG,
                                Constant.IMAGE_NO_COMPRESS_RATE /* max quality */,
                                bos);
            }
            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

            // upload to AWS S3
            TransferObserver observer = transferUtility.upload(
                    Constant.S3_BUCKET,     /* The bucket to upload to */
                    fileName,    /* The default format key for the uploaded avatar */
                    file    /* The file where the data to upload exists */
            );
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if ( state == TransferState.COMPLETED ) {
                        responseCode = 200;
                        message = Constant.S3_ROOT_FOLDER + fileName;   // the created file url
                        LocalDBHelper.getInstance(context).cacheImage(message,localPath);
                        notifyListener(responseCode,message);
                        Log.d(TAG,"complete");
                    } else if ( state == TransferState.CANCELED ) {
                        Log.d(TAG,"canceled");
                        responseCode = 400;
                        message = context.getString(R.string.prompt_upload_canceled);
                        notifyListener(responseCode,message);
                    } else if ( state == TransferState.FAILED ) {
                        Log.e(TAG,"failed");
                        responseCode = 400;
                        message = context.getString(R.string.error_upload_failed);
                        notifyListener(responseCode,message);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e(TAG,"failed");
                    responseCode = 400;
                    message = ex.getLocalizedMessage();
                    notifyListener(responseCode,message);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"failed");
            responseCode = 400;
            message = e.getLocalizedMessage();
            notifyListener(responseCode,message);
        }
    }

    public void downloadFromS3(final String url, boolean isCache){
        String localPath;
        if ( isCache ) {
            localPath = AppUtils.getDiskCacheDir(context);
        } else {
            localPath = AppUtils.getDiskFileDir(context);
        }
        String fileName = "Cache_" + System.currentTimeMillis() + Constant.IMAGE_FORMAT;
        final File localFile = new File(localPath,fileName);
        String fileKey = url.replace(Constant.S3_ROOT_FOLDER,"");
        TransferObserver observer = transferUtility.download(
                Constant.S3_BUCKET,     /* The bucket to download from */
                fileKey,    /* The key for the object to download */
                localFile        /* The file to download the object to */
        );
        LocalDBHelper.getInstance(context).cacheImage(url,localFile.getAbsolutePath());
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if ( state == TransferState.COMPLETED ) {
                    Log.d(TAG,"complete");
                    responseCode = 200;
                    message = context.getString(R.string.prompt_upload_completed);
                    LocalDBHelper.getInstance(context).cacheImage(url,localFile.getAbsolutePath());
                    notifyListener(responseCode,message);
                } else if ( state == TransferState.CANCELED ) {
                    Log.d(TAG,"canceled");
                    responseCode = 400;
                    message = context.getString(R.string.prompt_upload_canceled);
                    notifyListener(responseCode,message);
                } else if ( state == TransferState.FAILED ) {
                    Log.e(TAG,"failed");
                    responseCode = 400;
                    message = context.getString(R.string.error_upload_failed);
                    notifyListener(responseCode,message);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG,"failed");
                responseCode = 400;
                message = ex.getLocalizedMessage();
                notifyListener(responseCode,message);
            }
        });
    }

    /**
     * use the call back to notify the caller
     * @param responseCode 200 if upload succeeded, 400 if failed
     * @param message contains url if succeeded, error message if failed
     */
    private void notifyListener(int responseCode, String message){
        onUploadResultListener.onFinished(responseCode,message);
    }
}
