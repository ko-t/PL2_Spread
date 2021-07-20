package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Ready extends AppCompatActivity {
    private int ruleNumber = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        ImageView rd_imageView_rule = findViewById(R.id.rd_imageView_rule);
        rd_imageView_rule.setImageResource(R.drawable.rule1);
        TextView rd_textView_rule = findViewById(R.id.rd_textView_rule);
        String rule1 = getString(R.string.rule1);
        String rule2 = getString(R.string.rule2);
        String rule3 = getString(R.string.rule3);
        rd_textView_rule.setText(rule1);
        TextView rd_textView_ruleNumber = findViewById(R.id.rd_textView_ruleNumber);
        String ruleNumber1 = getString(R.string.ruleNumber1);
        String ruleNumber2 = getString(R.string.ruleNumber2);
        String ruleNumber3 = getString(R.string.ruleNumber3);
        rd_textView_ruleNumber.setText(ruleNumber1);
        TextView rd_textView_waiting = findViewById(R.id.rd_textView_waiting);
        String waiting = getString(R.string.rd_waiting);
        ImageButton rd_imageButton_left = findViewById(R.id.rd_imageButton_left);
        ImageButton rd_imageButton_right = findViewById(R.id.rd_imageButton_right);
        rd_imageButton_left.setEnabled(false);
        rd_imageButton_left.setOnClickListener(v -> {
            ruleNumber -= 1;
            if(ruleNumber == 1) {
                rd_imageButton_left.setEnabled(false);
                rd_imageView_rule.setImageResource(R.drawable.rule1);
                rd_textView_rule.setText(rule1);
                rd_textView_ruleNumber.setText(ruleNumber1);
            }else if(ruleNumber == 2) {
                rd_imageView_rule.setImageResource(R.drawable.rule2);
                rd_textView_rule.setText(rule2);
                rd_textView_ruleNumber.setText(ruleNumber2);
                rd_imageButton_right.setEnabled(true);
            }
        });
        rd_imageButton_right.setOnClickListener(v -> {
            ruleNumber += 1;
            if(ruleNumber == 2) {
                rd_imageView_rule.setImageResource(R.drawable.rule2);
                rd_textView_rule.setText(rule2);
                rd_textView_ruleNumber.setText(ruleNumber2);
                rd_imageButton_left.setEnabled(true);
            }else if(ruleNumber == 3) {
                rd_imageView_rule.setImageResource(R.drawable.rule3);
                rd_textView_rule.setText(rule3);
                rd_textView_ruleNumber.setText(ruleNumber3);
                rd_imageButton_right.setEnabled(false);
            }
        });

        ImageButton rd_imageButton_ready = findViewById(R.id.rd_imageButton_ready);
        rd_imageButton_ready.setOnClickListener(v -> {
            Client.sendMessage("ready");
            rd_imageButton_ready.setEnabled(false);
            rd_textView_waiting.setText(waiting);
        });
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "start":
                Client.finishActivity();
                Intent intent_to_gm = new Intent(Client.context, Game.class);
                Client.startActivity(intent_to_gm);
                break;
        }
    }
}