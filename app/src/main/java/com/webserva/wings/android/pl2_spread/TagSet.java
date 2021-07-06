package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TagSet extends AppCompatActivity {

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

        Button tg_button_make = findViewById(R.id.tg_button_make);
        tg_button_make.setOnClickListener(v -> {
            boolean transitionFlag = true;

            if(!TextUtils.isEmpty(tg_plainText_room.getText())) {
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
                Intent intent_to_ms = new Intent(getApplication(), MemberSelect.class);
                startActivity(intent_to_ms);
            }
        });
    }
}