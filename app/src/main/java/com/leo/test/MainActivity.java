package com.leo.test;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private   SmsContentObserver observer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        initContentObserver();
    }

    private void initContentObserver() {
        observer = new SmsContentObserver(this, new Handler(Looper.myLooper()), new SmsContentObserver.SmsListener() {
            @Override
            public void onResult(String result) {
                editText.setText(result);
            }
        });
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, observer);
    }

    @Override
    protected void onDestroy() {
        this.getContentResolver().unregisterContentObserver(observer);
        super.onDestroy();
    }
}