package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class GameEnd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        Button ge_button_replay = findViewById(R.id.ge_button_replay);
        ge_button_replay.setOnClickListener(v -> {
            Client.sendMessage("resume$1");
            Client.finishActivity();
            Intent intent_to_rmn = new Intent(getApplication(), RoomMenu.class);
            startActivity(intent_to_rmn);
        });
        Button ge_button_endGame = findViewById(R.id.ge_button_endGame);
        ge_button_endGame.setOnClickListener(v -> {
            Client.sendMessage("resume$0");
            Client.finishActivity();
            Intent intent_to_mm = new Intent(getApplication(), MainMenu.class);
            startActivity(intent_to_mm);
        });

    }
}