package com.example.jang.its_ea.helper;

import android.util.Log;

import java.io.IOException;

/**
 * Created by jang on 2016-12-01.
 */

import android.os.AsyncTask;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import com.example.jang.its_ea.App.AppConfig;
import com.google.gson.Gson;
import android.util.Log;

import java.io.IOException;

public class RequestCapture extends AsyncTask<String, Void, String>{

    static OkHttpClient client = new OkHttpClient();
    static String url = AppConfig.EPCIS_SERVER_CAPTURE;
    public static final MediaType mediaType = MediaType.parse("application/xml; charset=utf-8");
    private static final String TAG = "RequestCapture";
    @Override
    protected String doInBackground(String... params) {

        try {
            String xml = params[0];
            RequestBody body = RequestBody.create(mediaType, xml);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "doInBackground end");
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "result = " + result);
        return ;
    }
}
