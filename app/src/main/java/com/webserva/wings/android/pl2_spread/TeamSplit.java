package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class TeamSplit extends AppCompatActivity {
    private static int MEMBER_NUM;
    private static String MEMBER_NAME;
    private static String MEMBER_ID;
    private static int limit = 5;
    private static String[] name_i;
    private static String[] id_i;
    private static String[] name_r;
    private static String[] id_r;
    private static TextView ts_textView_alert;
    private static String alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_split);

        Intent intent_from_ms = getIntent();
        MEMBER_NUM = intent_from_ms.getIntExtra("MEMBER_NUM",0);
        MEMBER_NAME = intent_from_ms.getStringExtra("MEMBER_NAME");
        MEMBER_ID = intent_from_ms.getStringExtra("MEMBER_ID");
        name_i = MEMBER_NAME.split("$");
        id_i = MEMBER_ID.split("$");
        TextView ts_textView_limit = findViewById(R.id.ts_textView_limit);
        ts_textView_limit.setText(limit);

        ImageButton ts_imageButton_g = findViewById(R.id.ts_imageButton_g);
        ts_imageButton_g.setOnClickListener(v -> {
            Client.sendMessage("gp$0");
            limit -= 1;
            ts_textView_limit.setText(limit);
        });
        ImageButton ts_imageButton_p = findViewById(R.id.ts_imageButton_p);
        ts_imageButton_p.setOnClickListener(v -> {
            Client.sendMessage("gp$1");
            limit -= 1;
            ts_textView_limit.setText(limit);
        });

        ts_textView_alert = findViewById(R.id.ts_textView_alert);
        alert = getString(R.string.ts_alert);

    }

    static void receiveMessage(String message){
        String[] s=message.split("\\$");
        switch(s[0]) {
            case "gps17":
                int gCount = 0;
                int pCount = 0;
                int memberNum = Integer.parseInt(s[1]);
                for(int i=2;i<memberNum*2+1;i += 2) {
                    if(Integer.parseInt(s[i]) == 0) {
                        gCount++;
                    }else {
                        pCount++;
                    }
                }

                if(Math.abs(gCount-pCount) <= 1) {
                    String memberName = "";
                    String memberId = "";
                    String memberGp = "";
                    for(int i=1;i<memberNum*2;i += 2) {
                        id_r[i] = s[i];
                        memberId += id_r[i] + "$";
                    }
                    memberId = memberId.substring(0,memberId.length()-1);

                    for(String id_r : id_r) {
                        for(int i=0;i<memberNum;i++) {
                            if(id_r.equals(id_i[i])) {
                                name_r[i] = name_i[i];
                                memberName += name_r[i] + "$";
                                break;
                            }
                        }
                    }
                    memberName = memberName.substring(0,memberName.length()-1);

                    for(int i=2;i<memberNum*2+1;i += 2) {
                        memberGp += s[i] + "$";
                    }
                    memberGp = memberGp.substring(0,memberGp.length()-1);

                    Intent intent_to_tsr = new Intent(Client.context,TeamSplitResult.class);
                    intent_to_tsr.putExtra("MEMBER_NUM",s[1]);
                    intent_to_tsr.putExtra("MEMBER_NAME",memberName);
                    intent_to_tsr.putExtra("MEMBER_ID",memberId);
                    intent_to_tsr.putExtra("MEMBER_GP",memberGp);
                    Client.finishActivity();
                    Client.startActivity(intent_to_tsr);
                }else {
                    if(limit == 0) {
                        String gpDefault = "";
                        for(int i=1;i<=MEMBER_NUM;i++) {
                            if(i%2 == 0) {
                                gpDefault += "0$";
                            }else {
                                gpDefault += "1$";
                            }
                        }
                        gpDefault = gpDefault.substring(0,gpDefault.length()-1);

                        Intent intent_to_tsr = new Intent(Client.context,TeamSplitResult.class);
                        intent_to_tsr.putExtra("MEMBER_NUM",MEMBER_NUM);
                        intent_to_tsr.putExtra("MEMBER_NAME",MEMBER_NAME);
                        intent_to_tsr.putExtra("MEMBER_ID",MEMBER_ID);
                        intent_to_tsr.putExtra("MEMBER_GP",gpDefault);
                        Client.finishActivity();
                        Client.startActivity(intent_to_tsr);
                    }else {
                        ts_textView_alert.setText(alert);
                    }
                }
        }
    }
}