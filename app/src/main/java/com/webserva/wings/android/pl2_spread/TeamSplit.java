package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class TeamSplit extends AppCompatActivity {
    private int MEMBER_NUM;
    private String MEMBER_NAME;
    private String MEMBER_ID;
    private int limit = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_split);

        Intent intent_from_ms = getIntent();
        MEMBER_NUM = intent_from_ms.getIntExtra("MEMBER_NUM",0);
        MEMBER_NAME = intent_from_ms.getStringExtra("MEMBER_NAME");
        MEMBER_ID = intent_from_ms.getStringExtra("MEMBER_ID");
        TextView ts_textView_limit = findViewById(R.id.ts_textView_limit);
        ts_textView_limit.setText(limit);

        ImageButton ts_imageButton_g = findViewById(R.id.ts_imageButton_g);
        ts_imageButton_g.setOnClickListener(v -> {
            Client.sendMessage("gp$0");
            limit -= 1;
            ts_textView_limit.setText(limit);
        });
        ImageButton ts_imageButton_p = findViewById(R.id.ts_imageButton_p);
        ts_imageButton_p.setOnClickListener(v -> {
            Client.sendMessage("gp$1");
            limit -= 1;
            ts_textView_limit.setText(limit);
        });

    }

    static void receiveMessage(String message){
        String[] s=message.split("\\$");
        switch(s[0]) {
            case "gps17":
        }
    }
}