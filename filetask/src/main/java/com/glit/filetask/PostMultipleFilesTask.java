package com.glit.filetask;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by baracouta on 8/15/2017.
 */

public class PostMultipleFilesTask extends AsyncTask<String, String, String> {
    private String mUrlString = "";
    private String mFilePath = "";
    private String mFileName = "";
    private String mBasicAuth = "";
    private File[] mFiles = null;

    public interface AsyncResponse {
        void onPostExecute(String output);
        void onProgressUpdate(String... values);
        void onPreExecute();
    }

    public AsyncResponse mDelegate = null;

    public PostMultipleFilesTask(AsyncResponse delegate, File[] files, String... auth) {
        if(auth.length == 2) {
            String a = auth[0] + ":" + auth[1];
            mBasicAuth = "Basic " + Base64.encodeToString(a.getBytes(), Base64.NO_WRAP);
        } else {
            mBasicAuth = "";
        }
        mDelegate = delegate;
        mFiles = files;
    }

    @Override
    protected String doInBackground(String... params) {
        mUrlString = params[0];

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String inputLine = "";
        StringBuffer sb = null;
        int i = 0;

        HttpURLConnection connection = null;
        BufferedReader br = null;
        BufferedInputStream is = null;
        FileInputStream fileInputStream = null;
        DataOutputStream outputStream = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try {
            URL url = new URL(mUrlString);
            for(i = 0; i < mFiles.length; i++) {
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Set HTTP method to POST
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                outputStream = new DataOutputStream(connection.getOutputStream());

                // Post file
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + mFiles[i].getName() + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                fileInputStream = new FileInputStream(mFiles[i]);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                is = new BufferedInputStream(connection.getInputStream());
                br = new BufferedReader(new InputStreamReader(is));
                sb = new StringBuffer();
                while((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
                is.close();
                br.close();

                publishProgress(mFiles[i].getPath() + mFiles[i].getName(),
                        String.format("%d", i+1),
                        String.format("%d", mFiles.length));
            }

        }catch(MalformedURLException e) {
            e.printStackTrace();
            sb.setLength(0);
        }catch(IOException e) {
            e.printStackTrace();
            sb.setLength(0);
        }

        return sb.toString();
    }

    @Override
    protected void onPreExecute() { mDelegate.onPreExecute(); }

    @Override
    protected void onPostExecute(String result) {
        mDelegate.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mDelegate.onProgressUpdate(values);
    }
}
