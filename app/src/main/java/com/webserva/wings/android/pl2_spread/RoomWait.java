package com.webserva.wings.android.pl2_spread;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class RoomWait extends AppCompatActivity implements View.OnClickListener {
    private Button rw_button_quit;  //退出→4 RoomListに戻る
    private static Intent intent;
    private static String rw_hostname, rw_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomwait);
        rw_button_quit=findViewById(R.id.rw_button_quit);
        rw_button_quit.setOnClickListener(this);

        Intent i = new Intent();
        int rw_tag = i.getIntExtra("TAG",0);
        rw_id = i.getStringExtra("HOSTID");
        rw_hostname = i.getStringExtra("HOSTNAME");

        //タグ取得
        int[] rw_tag_1 = new int[3];
        for (int j = 0; j < 3; j++) {
            rw_tag_1[2-j] = rw_tag & (1 << j);
        }
        TextView rw_roomname = findViewById(R.id.rw_textview_roomname);
        TextView rw_gm = findViewById(R.id.rw_textview_select1);
        TextView rw_se = findViewById(R.id.rw_textview_select2);
        TextView rw_m = findViewById(R.id.rw_textview_select3);
        if(rw_tag_1[0]==0){ rw_gm.setText("対戦");
        }else{ rw_gm.setText("協力"); }
        if(rw_tag_1[1]==0){ rw_se.setText("あり");
        }else{ rw_se.setText("なし"); }
        if(rw_tag_1[2]==0){ rw_m.setText("知ってる人のみ");
        }else{ rw_m.setText("知らない人もOK"); }




        //receiveMessage("approved$room1$tag1$id1");
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        Room room1 = new Room(rw_hostname, rw_tag, rw_id, rw_hostname);
        rw_roomname.setText(room1.getRoomName());

        //ホストの表示
        List<MemberInfo> list = new ArrayList<>();
        ListView listview = findViewById(R.id.rw_listview_hostname);
        Rw_Ri_Tsr_Adapter rw_ri_tsr_adapter = new Rw_Ri_Tsr_Adapter(this, list);
        listview.setAdapter(rw_ri_tsr_adapter);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            //承認をうけたら画面10(RoomInfo)に移動
            case "approved":
                Log.i("rw_receiveMessage","入室を承認されました");
                Client.finishActivity();
                intent = new Intent(Client.context, RoomInfo.class);
                intent.putExtra("HOSTID",rw_hostname);
                intent.putExtra("HOSTNAME",rw_id);
                Client.startActivity(intent);
                break;

            case "declined":
                Log.i("rw_receiveMessage","入室を拒否されました");
                Client.finishActivity();
                intent = new Intent(Client.context, RoomList.class);
                Client.startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if(v==rw_button_quit){    //退出する場合
            Client.sendMessage("leave");
            intent = new Intent(this,RoomList.class);
            startActivityForResult(intent,0);
        }
    }
}