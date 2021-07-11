package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Title extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        Button ti_button_start = findViewById(R.id.ti_button_start);
        ti_button_start.setOnClickListener(v -> {
            Intent intent_to_mm = new Intent(getApplication(), MainMenu.class);
            Client.startActivity(intent_to_mm);
        });
    }
}