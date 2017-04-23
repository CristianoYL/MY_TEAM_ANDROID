package com.example.cristiano.myteam.request;

import android.os.AsyncTask;
import android.util.Log;

import com.example.cristiano.myteam.util.Constant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Cristiano on 2017/3/28.
 *
 *  This helper class creates an AsyncTask that
 *  sends the request and process the response in the background
 */

public class BackgroundRequest extends AsyncTask<String, Object, String> {

    private int responseCode = 400;
    private InputStream inputStream;
    private OutputStream outputStream;
    private RequestAction action = null;

    public BackgroundRequest(RequestAction action){
        this.action = action;
    }

    @Override
    protected String doInBackground(String... params) {
        String method = params[0];
        String url = params[1];
        String data;
        if ( method.equals(Constant.METHOD_GET) ) {
            data = null;
        } else {
            data = params[2];
        }
        return sendRequest(url,method,data);
    }

    @Override
    protected void onPreExecute() {
        if (action != null) {
            action.actOnPre();
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String response) {;
        if (action != null) {
            action.actOnPost(this.responseCode, response);
        }
        super.onPostExecute(response);
    }
    private String sendRequest(String url, String method, String jsonData){
        Log.d("SendRequest","sending " + method +" request to " + url);
        String response = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
            httpURLConnection.setRequestMethod(method);
            if (jsonData != null) {
                httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                this.outputStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, Constant.SERVER_CHARSET));
                writer.write(jsonData);
                writer.flush();
                writer.close();
            }
            this.responseCode = httpURLConnection.getResponseCode();
            if (this.responseCode < 400) {
                this.inputStream = httpURLConnection.getInputStream();
            } else {
                this.inputStream = httpURLConnection.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(this.inputStream, Constant.SERVER_CHARSET), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            line = reader.readLine();
            if (line != null) {
                stringBuilder.append(line);
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append("\n");
                    stringBuilder.append(line);
                    Log.d("REQUEST_SENDER", "readLine=" + line + ";");
                }
            }
            inputStream.close();
            response = stringBuilder.toString();
            Log.d("RESPONSE", response + ";");
        } catch (SocketTimeoutException e) {
            response = Constant.MSG_TIME_OUT;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( httpURLConnection != null ) {
                httpURLConnection.disconnect();
            }
        }
        return response;
    }
}
