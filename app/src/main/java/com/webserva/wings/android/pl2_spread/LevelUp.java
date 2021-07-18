package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class LevelUp extends AppCompatActivity {
    private int[] newStatus = {0, 0, 0, 0};
    private int statusUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);

        Intent intent_from_re = getIntent();
        int levelup = intent_from_re.getIntExtra("levelup",0);
        TextView lv_textView_level = findViewById(R.id.lv_textView_level);
        int current_level = Client.calcLevel(Client.myInfo.getExp());
        lv_textView_level.setText(current_level-levelup + "→" + current_level);
        statusUp = levelup*3;

        for(int i=0;i<4;i++) {
            newStatus[i] = Client.myInfo.getStatus()[i];
        }

        TextView lv_textView_east = findViewById(R.id.lv_textView_east);
        lv_textView_east.setText(String.valueOf(newStatus[0]));
        TextView lv_textView_south = findViewById(R.id.lv_textView_south);
        lv_textView_south.setText(String.valueOf(newStatus[1]));
        TextView lv_textView_west = findViewById(R.id.lv_textView_west);
        lv_textView_west.setText(String.valueOf(newStatus[2]));
        TextView lv_textView_north = findViewById(R.id.lv_textView_north);
        lv_textView_north.setText(String.valueOf(newStatus[3]));

        ImageButton lv_imageButton_eastUp = findViewById(R.id.lv_imageButton_eastUp);
        ImageButton lv_imageButton_southUp = findViewById(R.id.lv_imageButton_southUp);
        ImageButton lv_imageButton_westUp = findViewById(R.id.lv_imageButton_westUp);
        ImageButton lv_imageButton_northUp = findViewById(R.id.lv_imageButton_northUp);
        Button lv_button_decide = findViewById(R.id.lv_button_decide);
        lv_button_decide.setEnabled(false);
        lv_imageButton_eastUp.setOnClickListener(v -> {
            statusUp -= 1;
            newStatus[0] += 1;
            lv_textView_east.setText(String.valueOf(newStatus[0]));
            if(statusUp == 0) {
                lv_imageButton_eastUp.setEnabled(false);
                lv_imageButton_northUp.setEnabled(false);
                lv_imageButton_westUp.setEnabled(false);
                lv_imageButton_southUp.setEnabled(false);
                lv_button_decide.setEnabled(true);
            }
        });
        lv_imageButton_southUp.setOnClickListener(v -> {
            statusUp -= 1;
            newStatus[1] += 1;
            lv_textView_south.setText(String.valueOf(newStatus[1]));
            if(statusUp == 0) {
                lv_imageButton_eastUp.setEnabled(false);
                lv_imageButton_northUp.setEnabled(false);
                lv_imageButton_westUp.setEnabled(false);
                lv_imageButton_southUp.setEnabled(false);
                lv_button_decide.setEnabled(true);
            }
        });
        lv_imageButton_westUp.setOnClickListener(v -> {
            statusUp -= 1;
            newStatus[2] += 1;
            lv_textView_west.setText(String.valueOf(newStatus[2]));
            if(statusUp == 0) {
                lv_imageButton_eastUp.setEnabled(false);
                lv_imageButton_northUp.setEnabled(false);
                lv_imageButton_westUp.setEnabled(false);
                lv_imageButton_southUp.setEnabled(false);
                lv_button_decide.setEnabled(true);
            }
        });
        lv_imageButton_northUp.setOnClickListener(v -> {
            statusUp -= 1;
            newStatus[3] += 1;
            lv_textView_north.setText(String.valueOf(newStatus[3]));
            if(statusUp == 0) {
                lv_imageButton_eastUp.setEnabled(false);
                lv_imageButton_northUp.setEnabled(false);
                lv_imageButton_westUp.setEnabled(false);
                lv_imageButton_southUp.setEnabled(false);
                lv_button_decide.setEnabled(true);
            }
        });

        lv_button_decide.setOnClickListener(v -> {
            Client.sendMessage("newstatus$" + newStatus[0] + "$" + newStatus[1] + "$"
                    + newStatus[2] + "$" + newStatus[3]);
            Intent intent_to_ge = new Intent(getApplication(), GameEnd.class);
            Client.startActivity(intent_to_ge);
        });

    }
}