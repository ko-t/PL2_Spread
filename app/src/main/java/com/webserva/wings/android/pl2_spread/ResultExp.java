package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultExp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_exp);

        TextView re_textView_level = findViewById(R.id.re_textView_level);
        re_textView_level.setText(Client.myInfo.getLevel());
        TextView re_textView_east = findViewById(R.id.re_textView_east);
        re_textView_east.setText(Client.myInfo.getStatus()[0]);
        TextView re_textView_south = findViewById(R.id.re_textView_south);
        re_textView_south.setText(Client.myInfo.getStatus()[1]);
        TextView re_textView_west = findViewById(R.id.re_textView_west);
        re_textView_west.setText(Client.myInfo.getStatus()[2]);
        TextView re_textView_north = findViewById(R.id.re_textView_north);
        re_textView_north.setText(Client.myInfo.getStatus()[3]);

        Intent intent_from_rm = getIntent();
        int score = intent_from_rm.getIntExtra("SCORE",0);





    }
}