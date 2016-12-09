package com.example.jang.its_ea.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.example.jang.its_ea.App.AppConfig;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jang on 2016-12-01.
 */

public class RequestQuery  extends AsyncTask<String, Void, String> {

    static OkHttpClient client = new OkHttpClient();
    static String url = AppConfig.EPCIS_SERVER_QUERY;
    public static final MediaType mediaType = MediaType.parse("application/xml; charset=utf-8");
    private static final String TAG = "RequestQuery";
    private Context mContext;
    private Exception mException;
    private OnEventListener<String>  mCallback;

    public RequestQuery(Context context, OnEventListener callback) {
        mCallback = callback;
        mContext = context;
    }
    @Override
    protected String doInBackground(String... params) {

        try {
            String query = url;
            if (!params[0].isEmpty())
                query = query + "?" + params[0];

            Log.d(TAG, "query = " + query);
            Request request = new Request.Builder()
                    .url(query)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
   //     Log.i("result", result);
        if (mCallback != null) {
            mCallback.onSuccess(result);
        } else {
            mCallback.onFailure(mException);
        }
        return ;
    }


}

