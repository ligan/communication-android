package com.glit.filetask;

import android.os.AsyncTask;

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
 * Created by Gan on 7/4/2017.
 */

public class PostFileTask extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
        FileInputStream fileInputStream = null;
        DataOutputStream outputStream = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        StringBuffer sb = null;

        String urlServer = params[0];
        String filePath = params[1];
        String filename = params[2];
        String line = "";
        String inputLine = "";
        HttpURLConnection connection = null;
        BufferedReader br = null;
        BufferedInputStream is = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try {
            URL url = new URL(urlServer);
            connection = (HttpURLConnection)url.openConnection();

            // Allow Input & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            // Post file
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + filename + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            fileInputStream = new FileInputStream(new File(filePath + filename));

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

        }catch(MalformedURLException e) {
            e.printStackTrace();
            sb.setLength(0);
        }catch(IOException e) {
            e.printStackTrace();
            sb.setLength(0);
        }

        return sb.toString();
    }
}
