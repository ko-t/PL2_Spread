package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class LevelUp extends AppCompatActivity {
    private int[] newStatus = {0, 0, 0, 0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);

        for(int i=0;i<4;i++) {
            newStatus[i] = Client.myInfo.getStatus()[i];
        }

        TextView lv_textView_east = findViewById(R.id.lv_textView_east);
        lv_textView_east.setText(newStatus[0]);
        TextView lv_textView_south = findViewById(R.id.lv_textView_south);
        lv_textView_south.setText(newStatus[1]);
        TextView lv_textView_west = findViewById(R.id.lv_textView_west);
        lv_textView_west.setText(newStatus[2]);
        TextView lv_textView_north = findViewById(R.id.lv_textView_north);
        lv_textView_north.setText(newStatus[3]);

        ImageButton lv_imageButton_eastUp = findViewById(R.id.lv_imageButton_eastUp);
        lv_imageButton_eastUp.setOnClickListener(v -> {
            newStatus[0] += 1;
            lv_textView_east.setText(newStatus[0]);
        });
        ImageButton lv_imageButton_southUp = findViewById(R.id.lv_imageButton_southUp);
        lv_imageButton_southUp.setOnClickListener(v -> {
            newStatus[1] += 1;
            lv_textView_south.setText(newStatus[1]);
        });
        ImageButton lv_imageButton_westUp = findViewById(R.id.lv_imageButton_westUp);
        lv_imageButton_westUp.setOnClickListener(v -> {
            newStatus[2] += 1;
            lv_textView_west.setText(newStatus[2]);
        });
        ImageButton lv_imageButton_northUp = findViewById(R.id.lv_imageButton_northUp);
        lv_imageButton_northUp.setOnClickListener(v -> {
            newStatus[3] += 1;
            lv_textView_north.setText(newStatus[3]);
        });

        Button lv_button_decide = findViewById(R.id.lv_button_decide);
        lv_button_decide.setEnabled(false);
        lv_button_decide.setOnClickListener(v -> {
            Intent intent_to_ge = new Intent(getApplication(), GameEnd.class);
            startActivity(intent_to_ge);
        });

    }
}