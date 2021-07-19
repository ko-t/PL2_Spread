package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class ResultExp extends AppCompatActivity {
    private int levelup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_exp);

        TextView re_textView_level = findViewById(R.id.re_textView_level);
        TextView re_textView_exp = findViewById(R.id.re_textView_exp);
        TextView re_textView_nextLevel = findViewById(R.id.re_textView_nextLevel);
        TextView re_textView_east = findViewById(R.id.re_textView_east);
        re_textView_east.setText(String.valueOf(Client.myInfo.getStatus()[0]));
        TextView re_textView_south = findViewById(R.id.re_textView_south);
        re_textView_south.setText(String.valueOf(Client.myInfo.getStatus()[1]));
        TextView re_textView_west = findViewById(R.id.re_textView_west);
        re_textView_west.setText(String.valueOf(Client.myInfo.getStatus()[2]));
        TextView re_textView_north = findViewById(R.id.re_textView_north);
        re_textView_north.setText(String.valueOf(Client.myInfo.getStatus()[3]));

        Intent intent_from_rm = getIntent();
        int score = intent_from_rm.getIntExtra("SCORE",0);
        re_textView_exp.setText(String.valueOf(score));
        int current_exp = Client.myInfo.getExp();
        int current_level = Client.calcLevel(current_exp);
        int new_exp = current_exp+score;
        Client.myInfo.addExp(new_exp);
        int next_level = Client.calcLevel(new_exp);
        re_textView_level.setText(String.valueOf(next_level));
        re_textView_nextLevel.setText(String.valueOf(Client.calcNextExp(new_exp)));
        levelup = next_level-current_level;

        ImageButton re_imageButton_next = findViewById(R.id.re_imageButton_next);
        re_imageButton_next.setOnClickListener(v -> {
            Intent intent;
            if(levelup == 0) {
                intent = new Intent(getApplication(), GameEnd.class);
            }else {
                intent = new Intent(getApplication(), LevelUp.class);
            }
            Client.startActivity(intent);
        });

    }
}