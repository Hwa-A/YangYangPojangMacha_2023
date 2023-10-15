package com.yuhan.yangpojang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PochainfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pochainfo);

        Intent intent = getIntent();

        if(intent != null){
            String receivedData = intent.getStringExtra("primaryKey");

            if(receivedData!=null){
                TextView textView = findViewById(R.id.text);
                textView.setText(receivedData);
            }
        }

    }
}