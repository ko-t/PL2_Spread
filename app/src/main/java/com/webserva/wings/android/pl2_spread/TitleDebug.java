package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.*;

public class TitleDebug extends AppCompatActivity {

    String id = "ID_Mitoma", name = "NAME_tmt";

    Switch sw2;
    EditText idText;
    private static String TAG = "TitleDebug";
    Button idCheckButton;
    FirebaseFirestore db;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titledebug);

        i = new Intent(getApplication(), Title.class);
        i.putExtra("levelup", 2);

        Client.myInfo = new MemberInfo(name, id);

        idText = findViewById(R.id.tid_editText_id);
        idText.setText(Client.myInfo.getId());
        sw2 = findViewById(R.id.tid_switch2);

        db = FirebaseFirestore.getInstance();

        Button ti_button_start = findViewById(R.id.tid_button_start);
        ti_button_start.setOnClickListener(v -> {
            Client.myInfo.setId(idText.getText().toString());
            Client.init(this, idText.getText().toString());
            if (sw2.isChecked()) {
                useDummyLocations();
            }
            Client.startActivity(i);
        });
        idCheckButton = findViewById(R.id.tid_button_checkID);
        idCheckButton.setOnClickListener(v -> {
            checkId(Client.myInfo.getId());
        });
    }

    void useDummyLocations() {
        Client.myInfo.setTeam(1);
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

    void checkId(String id) {
        db.collection("memberList").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Document Exists: " + document.getData());
                    Toast.makeText(getApplication(), "重複しています", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getApplication(), "重複していません", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    @Override
//    protected void onStop() {
//        Log.i(TAG, "onStop");
//        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
//        super.onStop();
//    }

//    @Override
//    protected void onDestroy() {
//        Log.i(TAG, "onDestroy");
//        db.collection("memberList").document(Client.myInfo.getId()).update("state", "offline");
//        if(Client.myInfo.getId().equals(Client.myInfo.getRoomId())){
//            db.collection("roomList").document(Client.myInfo.getId()).delete();
//        } else if(!Client.myInfo.getRoomId().isEmpty()){
//            db.collection("roomList").document(Client.myInfo.getRoomId())
//                    .collection("member").document(Client.myInfo.getId()).update("team", -9);
//        }
//        super.onDestroy();
//    }
}
