package com.glit.jsontask;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gli on 6/23/2017.
 */

public class GetJsonTask extends AsyncTask<String, Integer, String> {
    private String mUrlString = "";
    private String mBasicAuth;

    public interface AsyncResponse {
        void processFinish(String output);
        void progressUpdate(Integer... values);
        void preExecute();
    }

    public AsyncResponse mDelegate = null;

    public GetJsonTask(String urlString, AsyncResponse delegate, String... auth) {
        if(auth.length == 2) {
            String a = auth[0] + ":" + auth[1];
            mBasicAuth = "Basic " + Base64.encodeToString(a.getBytes(), Base64.NO_WRAP);
        } else
            mBasicAuth = "";
        mUrlString = urlString;
        mDelegate = delegate;
    }

    @Override
    public void onPreExecute() {
        mDelegate.preExecute();
    }

    @Override
    public String doInBackground(String... params) {
        URL url = null;
        StringBuilder sb = new StringBuilder("");
        try {
            url = new URL(mUrlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            if(!mBasicAuth.equals("")) {
                conn.setRequestProperty("Authorization", mBasicAuth);
            }
            conn.setDoInput(true);

            int code = conn.getResponseCode();
            BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while((line = serverAnswer.readLine()) != null) {
                sb.append(line);
            }

            serverAnswer.close();
        }catch (Exception e) {
            e.printStackTrace();
            sb.setLength(0);
        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        mDelegate.processFinish(result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mDelegate.progressUpdate(values);
    }
}
