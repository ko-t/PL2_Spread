package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class RoomMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_menu);

        ImageButton rmn_imageButton_join = findViewById(R.id.rmn_imageButton_join);
        rmn_imageButton_join.setOnClickListener(v -> {
            Intent intent_to_rl = new Intent(getApplication(), RoomList.class);
            Client.startActivity(intent_to_rl);
        });

        ImageButton rmn_imageButton_make = findViewById(R.id.rmn_imageButton_make);
        rmn_imageButton_make.setOnClickListener(v -> {
            Intent intent_to_tg = new Intent(getApplication(), TagSet.class);
            Client.startActivity(intent_to_tg);
        });
    }
}