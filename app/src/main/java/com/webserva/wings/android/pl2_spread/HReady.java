package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class HReady extends AppCompatActivity {
    private int ruleNumber = 1;
    private static String gameReady;
    private static Button hr_button_startGame;
    private static TextView hr_textView_gameReady;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hready);

        ImageView hr_imageView_rule = findViewById(R.id.hr_imageView_rule);
        hr_imageView_rule.setImageResource(R.drawable.rule1);
        TextView hr_textView_rule = findViewById(R.id.hr_textView_rule);
        String rule1 = getString(R.string.rule1);
        String rule2 = getString(R.string.rule2);
        String rule3 = getString(R.string.rule3);
        hr_textView_rule.setText(rule1);
        TextView hr_textView_ruleNumber = findViewById(R.id.hr_textView_ruleNumber);
        String ruleNumber1 = getString(R.string.ruleNumber1);
        String ruleNumber2 = getString(R.string.ruleNumber2);
        String ruleNumber3 = getString(R.string.ruleNumber3);
        hr_textView_ruleNumber.setText(ruleNumber1);
        ImageButton hr_imageButton_left = findViewById(R.id.hr_imageButton_left);
        ImageButton hr_imageButton_right = findViewById(R.id.hr_imageButton_right);
        hr_imageButton_left.setEnabled(false);
        hr_imageButton_left.setOnClickListener(v -> {
            ruleNumber -= 1;
            if(ruleNumber == 1) {
                hr_imageButton_left.setEnabled(false);
                hr_imageView_rule.setImageResource(R.drawable.rule1);
                hr_textView_rule.setText(rule1);
                hr_textView_ruleNumber.setText(ruleNumber1);
            }else if(ruleNumber == 2) {
                hr_imageView_rule.setImageResource(R.drawable.rule2);
                hr_textView_rule.setText(rule2);
                hr_textView_ruleNumber.setText(ruleNumber2);
                hr_imageButton_right.setEnabled(true);
            }
        });
        hr_imageButton_right.setOnClickListener(v -> {
            ruleNumber += 1;
            if(ruleNumber == 2) {
                hr_imageView_rule.setImageResource(R.drawable.rule2);
                hr_textView_rule.setText(rule2);
                hr_textView_ruleNumber.setText(ruleNumber2);
                hr_imageButton_left.setEnabled(true);
            }else if(ruleNumber == 3) {
                hr_imageView_rule.setImageResource(R.drawable.rule3);
                hr_textView_rule.setText(rule3);
                hr_textView_ruleNumber.setText(ruleNumber3);
                hr_imageButton_right.setEnabled(false);
            }
        });

        hr_button_startGame = findViewById(R.id.hr_button_startGame);
        hr_button_startGame.setEnabled(false);
        hr_button_startGame.setOnClickListener(v -> {
            Client.sendMessage("start");
            Client.finishActivity();
            Intent intent_to_gm = new Intent(Client.context, Game.class);
            Client.startActivity(intent_to_gm);
        });
        hr_textView_gameReady = findViewById(R.id.hr_textView_gameReady);
        gameReady = getString(R.string.hr_gameReady);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "readyall":
                hr_button_startGame.setEnabled(true);
                hr_textView_gameReady.setText(gameReady);
                break;
        }
    }

}