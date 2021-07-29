package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

public class GameEnd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        ImageButton ge_imageButton_replay = findViewById(R.id.ge_imageButton_replay);
        ge_imageButton_replay.setOnClickListener(v -> {
            Client.sendMessage("resume$1");
            Client.finishActivity();
            Intent intent_to_rmn = new Intent(getApplication(), RoomMenu.class);
            intent_to_rmn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Client.startActivity(intent_to_rmn);
            Log.i("mm_setOnClickListener","RoomMenu画面に遷移");
        });
        ImageButton ge_imageButton_endGame = findViewById(R.id.ge_imageButton_endGame);
        ge_imageButton_endGame.setOnClickListener(v -> {
            Client.sendMessage("resume$0");
            Client.finishActivity();
            Intent intent_to_mm = new Intent(getApplication(), MainMenu.class);
            intent_to_mm.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Client.startActivity(intent_to_mm);
            Log.i("mm_setOnClickListener","MainMenu画面に遷移");
        });

    }
}