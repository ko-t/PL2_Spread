package com.webserva.wings.android.pl2_spread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomWait extends AppCompatActivity implements View.OnClickListener {
    private Button rw_button_quit;  //退出→4 RoomListに戻る
    private static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = new Intent();
        int rw_tag1 = i.getIntExtra("TAG",0);
        String rw_id = i.getStringExtra("HOSTID");
        String rw_hostname = i.getStringExtra("HOSTNAME");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomwait);
        rw_button_quit=(Button)findViewById(R.id.rw_button_quit);
        rw_button_quit.setOnClickListener(this);

        receiveMessage("approved$room1$tag1$id1");
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        Room room1 = new Room(rw_hostname, rw_tag1, rw_id);

        List<Room> list = new ArrayList<>();
        ListView listview = (ListView) findViewById(R.id.rw_listview_hostname);
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
                Client.startActivity(intent);
                break;
            //
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