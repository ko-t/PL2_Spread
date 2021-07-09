package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

public class TitleDebug extends AppCompatActivity {

    Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titledebug);

        sw = findViewById(R.id.tid_switch);

        Button ti_button_start = findViewById(R.id.tid_button_start);
        ti_button_start.setOnClickListener(v -> {
            forDebug();
            Client.init();

            if(sw.isChecked()){
                Client.init_connection();
            }

            Intent i = new Intent(getApplication(), ResultMap.class);
            startActivity(i);
        });
    }

    void forDebug(){
        Client.goal = new LatLng(30.1, 21.44);
        Client.goal = new LatLng(30.2, 21.34);
        Client.sendMessage("startpos");
        Client.sendMessage("goalpos");
    }
}
