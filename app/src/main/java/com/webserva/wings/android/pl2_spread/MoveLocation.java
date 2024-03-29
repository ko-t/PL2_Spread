package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MoveLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_location);
        TextView ml_textView_decide =findViewById(R.id.ml_textView_decide);
        TextView ml_textView_east = findViewById(R.id.ml_textView_east);
        ml_textView_east.setText(String.valueOf(Client.myInfo.getStatus().get(0)));
        TextView ml_textView_south = findViewById(R.id.ml_textView_south);
        ml_textView_south.setText(String.valueOf(Client.myInfo.getStatus().get(1)));
        TextView ml_textView_west = findViewById(R.id.ml_textView_west);
        ml_textView_west.setText(String.valueOf(Client.myInfo.getStatus().get(2)));
        TextView ml_textView_north = findViewById(R.id.ml_textView_north);
        ml_textView_north.setText(String.valueOf(Client.myInfo.getStatus().get(3)));

        RadioGroup ml_radioGroup_direction = findViewById(R.id.ml_radioGroup_direction);
        RadioButton ml_radioButton_east = findViewById(R.id.ml_radioButton_east);
        RadioButton ml_radioButton_south = findViewById(R.id.ml_radioButton_south);
        RadioButton ml_radioButton_west = findViewById(R.id.ml_radioButton_west);
        RadioButton ml_radioButton_north = findViewById(R.id.ml_radioButton_north);

        ImageButton ml_imageButton_decide = findViewById(R.id.ml_imageButton_decide);
        ml_imageButton_decide.setOnClickListener(v -> {
            if(ml_radioGroup_direction.getCheckedRadioButtonId() != -1) {
                if(ml_radioButton_east.isChecked()) {
                    Client.sendMessage("move$1");
                }else if(ml_radioButton_south.isChecked()) {
                    Client.sendMessage("move$2");
                }else if(ml_radioButton_west.isChecked()) {
                    Client.sendMessage("move$3");
                }else if(ml_radioButton_north.isChecked()) {
                    Client.sendMessage("move$4");
                }
            }
            ml_textView_decide.setText(R.string.ml_send_fin);//送信済みチェックのため変更
        });
    }

    @Override
    public void onBackPressed() {
    }
}