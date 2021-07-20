package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TagSet extends AppCompatActivity {
    private boolean transitionFlag = true;
    private int[] tagStatus = {0,0,0};
    private int tag;
    private String RoomName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_set);

        EditText tg_plainText_room = findViewById(R.id.tg_plainText_room);
        RadioGroup tg_radioGroup_game = findViewById(R.id.tg_radioGroup_game);
        RadioGroup tg_radioGroup_statusEffect = findViewById(R.id.tg_radioGroup_statusEffect);
        RadioGroup tg_radioGroup_member = findViewById(R.id.tg_radioGroup_member);
        TextView tg_textView_inputAlert = findViewById(R.id.tg_textView_inputAlert);
        TextView tg_textView_radioAlert = findViewById(R.id.tg_textView_radioAlert);
        String radio_alert = getString(R.string.tg_radioAlert);
        RadioButton tg_radioButton_battle = findViewById(R.id.tg_radioButton_battle);
        RadioButton tg_radioButton_on = findViewById(R.id.tg_radioButton_on);
        RadioButton tg_radioButton_known = findViewById(R.id.tg_radioButton_known);

        ImageButton tg_imageButton_gameMode = findViewById(R.id.tg_imageButton_gameMode);

        Button tg_button_make = findViewById(R.id.tg_button_make);
        tg_button_make.setOnClickListener(v -> {
            RoomName = tg_plainText_room.getText().toString();
            if(!RoomName.equals("")) {
                tg_textView_inputAlert.setText("");
            }else {
                transitionFlag = false;
                String input_alert = getString(R.string.tg_inputAlert);
                tg_textView_inputAlert.setText(input_alert);
            }

            if(tg_radioGroup_game.getCheckedRadioButtonId() != -1) {
                tg_radioGroup_game.setBackgroundColor(ContextCompat.getColor(this, R.color.Transparent));
                tg_textView_radioAlert.setText("");
            }else {
                transitionFlag = false;
                tg_radioGroup_game.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red));
                tg_textView_radioAlert.setText(radio_alert);
            }

            if(tg_radioGroup_statusEffect.getCheckedRadioButtonId() != -1) {
                tg_radioGroup_statusEffect.setBackgroundColor(ContextCompat.getColor(this, R.color.Transparent));
                tg_textView_radioAlert.setText("");
            }else {
                transitionFlag = false;
                tg_radioGroup_statusEffect.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red));
                tg_textView_radioAlert.setText(radio_alert);
            }

            if(tg_radioGroup_member.getCheckedRadioButtonId() != -1) {
                tg_radioGroup_member.setBackgroundColor(ContextCompat.getColor(this, R.color.Transparent));
                tg_textView_radioAlert.setText("");
            }else {
                transitionFlag = false;
                tg_radioGroup_member.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red));
                tg_textView_radioAlert.setText(radio_alert);
            }

            if(transitionFlag) {
                if(tg_radioButton_battle.isChecked()) {
                    tagStatus[0] = 0;
                }else {
                    tagStatus[0] = 1;
                }

                if(tg_radioButton_on.isChecked()) {
                    tagStatus[1] = 0;
                }else {
                    tagStatus[1] = 1;
                }

                if(tg_radioButton_known.isChecked()) {
                    tagStatus[2] = 0;
                }else {
                    tagStatus[2] = 1;
                }
                tag = tagStatus[0]*4+tagStatus[1]*2+tagStatus[2];
                Client.sendMessage("newroom$" + RoomName + "$" + tag);
                Intent intent_to_ms = new Intent(getApplication(), MemberSelect.class);
                intent_to_ms.putExtra("tag",tag);
                intent_to_ms.putExtra("HOSTNAME",RoomName);
                Client.startActivity(intent_to_ms);
            }
        });
    }
}