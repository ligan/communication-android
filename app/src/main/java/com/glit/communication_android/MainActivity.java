package com.glit.communication_android;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.glit.jsontask.GetJsonTask;
import com.glit.filetask.PostFileTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonTest = (Button)findViewById(R.id.button_test);
        final TextView textView = (TextView)findViewById(R.id.textview);

        final GetJsonTask.AsyncResponse response = new GetJsonTask.AsyncResponse() {
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

            @Override
            public void processFinish(String output) {
                textView.setText(output);
                progressDialog.dismiss();
            }

            @Override
            public void progressUpdate(Integer... values) {
                progressDialog.setMessage("Downloading");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
            }

            @Override
            public void preExecute() {

            }
        };

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                GetJsonTask gjt = new GetJsonTask("http://pin.qpim.net/api/android/main_pole_layer/midsouth/midsouth/poles2017",

                        response, "test", "test");
                gjt.execute();
                */
                PostFileTask.AsyncResponse response = new PostFileTask.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        File uploaded = new File("/storage/sdcard/com.glit.inspection/midsouth.zip");
                        uploaded.delete();
                    }

                    @Override
                    public void progressUpdate(Integer... values) {

                    }

                    @Override
                    public void preExecute() {

                    }
                };

                PostFileTask pft = new PostFileTask(response);
                pft.execute("http://pin.qpim.net/api/android/backup", "/storage/sdcard/com.glit.inspection/", "midsouth.zip");
            }
        });
    }
}
