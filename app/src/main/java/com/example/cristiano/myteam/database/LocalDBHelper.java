package com.example.cristiano.myteam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created by Cristiano on 2017/6/8.
 */

public class LocalDBHelper extends SQLiteOpenHelper {

    public static final String LOCAL_DATABASE_NAME = "MY_TEAM_DB.db";
    public static final String TABLE_IMAGE = "IMAGE_CACHE";
    public static final String IMAGE_COL_URL = "url";
    public static final String IMAGE_COL_BITMAP = "bitmap";

    Context context;
    SQLiteDatabase db;

    public LocalDBHelper(Context context) {
        super(context, LOCAL_DATABASE_NAME, null, 1);
        this.db = this.getWritableDatabase();
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_IMAGE + " ("
                + IMAGE_COL_URL + " TEXT PRIMARY KEY, "
                + IMAGE_COL_BITMAP + " BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(db);
    }

    public Bitmap getCachedImage(String url) {
        Cursor cursor = db.rawQuery("select * from "+ TABLE_IMAGE + " where "
                + IMAGE_COL_URL + "=?",new String[] {url});
        if ( cursor.getCount() == 1 ) {
            cursor.moveToNext();
            byte[] bytes = cursor.getBlob(1);  // col 1 is bitmap
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    public boolean cacheImage(String url, Bitmap bitmap) {
        byte[] image;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if ( bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream) ) {
            image = stream.toByteArray();
            ContentValues cv = new  ContentValues();
            cv.put(IMAGE_COL_URL,url);
            cv.put(IMAGE_COL_BITMAP,image);
            if ( db.insert(TABLE_IMAGE, null, cv) != -1 ) {
                return true;
            }
        }
        return false;
    }

    public void clearImageCache(){
        SQLiteDatabase db = this.getWritableDatabase();
        Toast.makeText(context, "Cleared " + db.delete(TABLE_IMAGE,"1",null) +
                "  cached image(s)", Toast.LENGTH_SHORT).show();
    }
}
