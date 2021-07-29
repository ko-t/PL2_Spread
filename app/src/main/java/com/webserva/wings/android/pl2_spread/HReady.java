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
    private static ImageButton hr_imageButton_startGame;
    private static TextView hr_textView_gameReady;
    private int ruleTotal;
    private static boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hready);

        Intent intent_from_ms = getIntent();
        int status_tag = intent_from_ms.getIntExtra("STATUS_TAG",0);
        ImageView hr_imageView_rule = findViewById(R.id.hr_imageView_rule);
        hr_imageView_rule.setImageResource(R.drawable.rule1);
        TextView hr_textView_rule = findViewById(R.id.hr_textView_rule);
        String rule1 = getString(R.string.rule1);
        String rule2 = getString(R.string.rule2);
        String rule3 = getString(R.string.rule3);
        hr_textView_rule.setText(rule1);
        TextView hr_textView_ruleNumber = findViewById(R.id.hr_textView_ruleNumber);
        ImageButton hr_imageButton_left = findViewById(R.id.hr_imageButton_left);
        ImageButton hr_imageButton_right = findViewById(R.id.hr_imageButton_right);
        hr_imageButton_left.setEnabled(false);

        if(status_tag == 0) {
            ruleTotal = 3;
            hr_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
            hr_imageButton_left.setOnClickListener(v -> {
                ruleNumber -= 1;
                if(ruleNumber == 1) {
                    hr_imageButton_left.setEnabled(false);
                    hr_imageView_rule.setImageResource(R.drawable.rule1);
                    hr_textView_rule.setText(rule1);
                    hr_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                }else if(ruleNumber == 2) {
                    hr_imageView_rule.setImageResource(R.drawable.rule2);
                    hr_textView_rule.setText(rule2);
                    hr_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    hr_imageButton_right.setEnabled(true);
                }
            });
            hr_imageButton_right.setOnClickListener(v -> {
                ruleNumber += 1;
                if(ruleNumber == 2) {
                    hr_imageView_rule.setImageResource(R.drawable.rule2);
                    hr_textView_rule.setText(rule2);
                    hr_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    hr_imageButton_left.setEnabled(true);
                }else if(ruleNumber == 3) {
                    hr_imageView_rule.setImageResource(R.drawable.rule3);
                    hr_textView_rule.setText(rule3);
                    hr_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    hr_imageButton_right.setEnabled(false);
                }
            });
        }else {
            ruleTotal = 2;
            hr_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
            hr_imageButton_left.setOnClickListener(v -> {
                ruleNumber -= 1;
                if(ruleNumber == 1) {
                    hr_imageButton_left.setEnabled(false);
                    hr_imageView_rule.setImageResource(R.drawable.rule1);
                    hr_textView_rule.setText(rule1);
                    hr_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    hr_imageButton_right.setEnabled(true);
                }
            });
            hr_imageButton_right.setOnClickListener(v -> {
                ruleNumber += 1;
                if(ruleNumber == 2) {
                    hr_imageButton_right.setEnabled(false);
                    hr_imageView_rule.setImageResource(R.drawable.rule2);
                    hr_textView_rule.setText(rule2);
                    hr_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    hr_imageButton_left.setEnabled(true);
                }
            });
        }

        hr_imageButton_startGame = findViewById(R.id.hr_imageButton_startGame);
        if(flag){
            hr_imageButton_startGame.setEnabled(true);
            hr_textView_gameReady.setText(gameReady);
        }
        hr_imageButton_startGame.setEnabled(false);
        hr_imageButton_startGame.setOnClickListener(v -> {
            Client.sendMessage("start");
            Client.finishActivity();
            Intent intent_to_gm = new Intent(Client.context, Game.class);
            intent_to_gm.putExtra("STATUS_TAG", intent_from_ms.getIntExtra("STATUS_TAG", 0));
            Client.startActivity(intent_to_gm);
        });
        hr_textView_gameReady = findViewById(R.id.hr_textView_gameReady);
        gameReady = getString(R.string.hr_gameReady);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "readyall":
                if(hr_imageButton_startGame != null){
                    hr_imageButton_startGame.setEnabled(true);
                    hr_textView_gameReady.setText(gameReady);
                } else {
                    flag = true;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}