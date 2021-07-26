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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

public class TitleDebug extends AppCompatActivity {

    String id = "iMitoma", name = "nMitoma";

    EditText idText, nameText;
    private static String TAG = "TitleDebug";
    Button idCheckButton;
    FirebaseFirestore db;
    Intent i;
    boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titledebug);

        i = new Intent(getApplication(), MainMenu.class);
        i.putExtra("levelup", 2);

        Client.myInfo = new MemberInfo(name, id);
        Client.myInfo.setStatus(Arrays.asList(150,150,150,150));

        idText = findViewById(R.id.tid_editText_id);
        idText.setText(Client.myInfo.getId());
        nameText = findViewById(R.id.tid_editText_name);
        nameText.setText(Client.myInfo.getName());
        //newRegister = findViewById(R.id.tid_switch2);

        String text = null;

        db = FirebaseFirestore.getInstance();

        Button ti_button_start = findViewById(R.id.tid_button_start);
        ti_button_start.setOnClickListener(v -> {
            Client.myInfo.setId(idText.getText().toString());
            Client.myInfo.setName(nameText.getText().toString());

            int flag = 0, nameLim = 20;
            String nameError[] = {
                    "ID・名前に$を使うことはできません",
                    "ID・名前は" + nameLim + "文字以下にしてください",
                    "ID、名前を入力してください"
            };
            if (Client.myInfo.getId().contains("$") || Client.myInfo.getName().contains("$"))
                flag |= 1;
            if (Math.max(Client.myInfo.getId().length(), Client.myInfo.getName().length()) >= nameLim + 1)
                flag |= 1 << 1;
            if (Client.myInfo.getId().isEmpty() || Client.myInfo.getName().isEmpty())
                flag |= 1 << 2;
            for (int i = 0; i < nameError.length; i++) {
                if ((flag & (1 << i)) != 0)
                    Toast.makeText(this, nameError[i], Toast.LENGTH_SHORT).show();
            }

            if (flag == 0) {
                db.collection("memberList").document(idText.getText().toString()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            Log.d(TAG, "Document Exists: " + document.getData());

                            //固有ストレージに書き込み
                            File file = new File(this.getFilesDir(), "userInfo");
                            try (FileWriter writer = new FileWriter(file)) {
                                StringJoiner sj = new StringJoiner("$");
                                //name id exp status
                                sj.add(Client.myInfo.getName());
                                sj.add(Client.myInfo.getId());
                                sj.add(String.valueOf(0));
                                sj.add(String.valueOf(Client.myInfo.getStatus().get(0)));
                                sj.add(String.valueOf(Client.myInfo.getStatus().get(1)));
                                sj.add(String.valueOf(Client.myInfo.getStatus().get(2)));
                                sj.add(String.valueOf(Client.myInfo.getStatus().get(3)));
                                writer.write(sj.toString());
                            }
                            catch (IOException e) {
                                Log.i(TAG, "2");
                                e.printStackTrace();
                            }

                            Client.init(idText.getText().toString(), true);
                            Client.startActivity(i);
                        } else {
                            Toast.makeText(this, "IDはすでに存在します", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        idCheckButton = findViewById(R.id.tid_button_checkID);
        idCheckButton.setOnClickListener(v -> {
            db.collection("memberList").document(idText.getText().toString()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(this, "IDはすでに存在します", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "IDはまだ存在しません", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

    boolean flag;

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
