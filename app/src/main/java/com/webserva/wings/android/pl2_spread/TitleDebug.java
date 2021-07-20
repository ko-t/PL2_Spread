package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

public class TitleDebug extends AppCompatActivity {

    Switch sw, sw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titledebug);

        Intent i = new Intent(getApplication(), TagSet.class);
        i.putExtra("levelup", 2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        for(int j=0; j<15; j++){
//            Score s = new Score();
//            s.setScore(j*12345);
//            s.setScoreId(j);
//            db.collection("ranking").document("dummyRank" + j).set(
//                    s
//            );
//        }

        sw = findViewById(R.id.tid_switch);
        sw2 = findViewById(R.id.tid_switch2);

        Button ti_button_start = findViewById(R.id.tid_button_start);
        ti_button_start.setOnClickListener(v -> {
            Client.init(this);
            if (sw.isChecked()) {
                //Client.init_connection();
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (sw2.isChecked()) {
                useDummyLocations();
            }

            Client.startActivity(i);
        });
    }

    void useDummyLocations() {
        Client.myInfo.setTeam(1);
        Client.myInfo.setRoomId("dummyHostId");
        Client.start = new LatLng(35.48116258624266, 139.58656026336882);
        Client.goal = new LatLng(35.4639497213053, 139.58540154911645);
        TeamResultMap.receiveMessage("otherpos19$3$3$" +
                "35.473150761836074$139.5898111005647$" +
                "35.470206193894484$139.59149552789043$" +
                "35.47275757651042$139.58385659675125$" +
                "35.47586805675942$139.5814962528599$" +
                "35.477895046540276$139.5837278507208$" +
                "35.475553519281966$139.59767533735135$" +
                "35.47146442009641$139.60059358070788$" +
                "35.467654730325606$139.60020734261659$" +
                "35.466571207610414$139.59475709399482$" +
                "35.47810473222136$139.59282590353826$" +
                "35.467759586587846$139.57677556507727$" +
                "35.47125471709664$139.5795221470599");
        //ResultMap.receiveMessage("score12$30.2, 22.44");
//        Client.start = new LatLng(30.1, 21.44);

//        Client.sendMessage("startpos");
//        Client.sendMessage("goalpos");
    }
}
