package com.glit.jsontask;

import android.os.AsyncTask;
import android.util.Base64;

import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by kali on 6/16/17.
 */

public class PostJsonTask extends AsyncTask<String, Integer, String> {
    private String mUrlString = "";
    private String mJson = "";
    private String mBasicAuth;

    public interface AsyncResponse {
        void processFinish(String output);
        void progressUpdate(Integer... values);
        void preExecute();
    }

    public AsyncResponse mDelegate = null;

    public PostJsonTask(String urlString, String json, AsyncResponse delegate, String... auth) {
        if(auth.length == 2) {
            String a = auth[0] + ":" + auth[1];
            mBasicAuth = "Basic " + Base64.encodeToString(a.getBytes(), Base64.NO_WRAP);
        } else
            mBasicAuth = "";
        mUrlString = urlString;
        mJson = json;
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
            conn.setRequestProperty("Content-Type", "application/json charset=UTF-8");
            if(!mBasicAuth.equals(""))
                conn.setRequestProperty("Authorization", mBasicAuth);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(mJson);
            wr.flush();

            int code = conn.getResponseCode();
            BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = serverAnswer.readLine()) != null) {
                System.out.println("LINE: " + line); //<--If any response from server
                sb.append(line);
            }

            wr.close();
            serverAnswer.close();

        } catch (Exception e) {
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