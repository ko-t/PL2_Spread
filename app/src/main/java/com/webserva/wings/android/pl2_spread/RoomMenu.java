package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RoomMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_menu);

        Button rmn_button_join = findViewById(R.id.rmn_button_join);
        rmn_button_join.setOnClickListener(v -> {
            Intent intent_to_rl = new Intent(getApplication(), MainMenu.class);
            startActivity(intent_to_rl);
        });

        Button rmn_button_make = findViewById(R.id.rmn_button_make);
        rmn_button_make.setOnClickListener(v -> {
            Intent intent_to_tg = new Intent(getApplication(), MainMenu.class);
            startActivity(intent_to_tg);
        });
    }
}