package com.example.jang.its_ea.helper;

import android.os.AsyncTask;
import android.util.Log;


import com.example.jang.its_ea.App.AppConfig;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by jang on 2016-12-01.
 */

public class RequestQuery  extends AsyncTask<String, Void, String> {

    static OkHttpClient client = new OkHttpClient();
    static String url = AppConfig.EPCIS_SERVER_QUERY;
    public static final MediaType mediaType = MediaType.parse("application/xml; charset=utf-8");
    private static final String TAG = "RequestQuery";

    private static String var1 = "TEST";
    @Override
    protected String doInBackground(String... params) {

        try {
            //String url = params[0];
            String url = AppConfig.EPCIS_SERVER_QUERY;
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("result", result);
        return ;
    }
}

