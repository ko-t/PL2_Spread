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
            Client.init();
            if (sw.isChecked()) {
                Client.init_connection();
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            forDebug();

            Intent i = new Intent(getApplication(), ResultMap.class);
            startActivity(i);
        });
    }

    void forDebug() {
        Client.goal = new LatLng(30.2, 21.34);
        ResultMap.receiveMessage("otherpos12$5$" +
                "34.2$26.5$" +
                "30.173$30.1112$" +
                "33.048$28.51$" +
                "32.83$27.124$" +
                "31.1245$29.53");
        //ResultMap.receiveMessage("score12$30.2, 22.44");
//        Client.start = new LatLng(30.1, 21.44);

//        Client.sendMessage("startpos");
//        Client.sendMessage("goalpos");
    }
}
