package com.glit.communication_android;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.glit.jsontask.GetJsonTask;

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
                progressDialog.setMessage("Downloading");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
            }

            @Override
            public void progressUpdate(Integer... values) {
                textView.setText(output);
                progressDialog.dismiss();
            }

            @Override
            public void preExecute() {

            }
        }
    }
}
