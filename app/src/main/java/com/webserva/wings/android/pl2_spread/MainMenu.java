package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageButton mm_imageButton_game = findViewById(R.id.mm_imageButton_game);
        mm_imageButton_game.setOnClickListener(v -> {
            Intent intent_to_rmn = new Intent(getApplication(), RoomMenu.class);
            Client.startActivity(intent_to_rmn);
        });

        ImageButton mm_ImageButton_profile = findViewById(R.id.mm_ImageButton_profile);
        mm_ImageButton_profile.setOnClickListener(v -> {
            Intent intent_to_pr = new Intent(getApplication(), Profile.class);
            Client.startActivity(intent_to_pr);
        });

        ImageButton mm_ImageButton_record = findViewById(R.id.mm_ImageButton_record);
        mm_ImageButton_record.setOnClickListener(v -> {
            Intent intent_to_rmn = new Intent(getApplication(), Ranking.class);
            Client.startActivity(intent_to_rmn);
        });
    }
}