package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button mm_button_game = findViewById(R.id.mm_button_game);
        mm_button_game.setOnClickListener(v -> {
            Intent intent_to_rmn = new Intent(getApplication(),RoomMenuActivity.class);
            startActivity(intent_to_rmn);
        });

        Button mm_button_profile = findViewById(R.id.mm_button_profile);
        mm_button_profile.setOnClickListener(v -> {
            Intent intent_to_rmn = new Intent(getApplication(),RoomMenuActivity.class);
            startActivity(intent_to_rmn);
        });

        Button mm_button_recode = findViewById(R.id.mm_button_recode);
        mm_button_recode.setOnClickListener(v -> {
            Intent intent_to_rmn = new Intent(getApplication(),RoomMenuActivity.class);
            startActivity(intent_to_rmn);
        });
    }
}