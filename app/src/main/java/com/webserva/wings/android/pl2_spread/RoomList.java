package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RoomList extends AppCompatActivity implements View.OnClickListener {
    private static int rl_flag;
    private static String rl_roomname,rl_tag,rl_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomlist);
        findViewById(R.id.rl_button_search).setOnClickListener(this);

        receiveMessage("add4$room1$tag1$id1");
        Integer rl_tag1 = Integer.parseInt(rl_tag);
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        Room room1 = new Room(rl_roomname, rl_tag1, rl_id);

        room1.getMember();
        List<Room> list = new ArrayList<>();
        if(rl_flag==0) {     //追加
            list.add(room1);
        }else if(rl_flag==1){    //削除
            list.remove(list.indexOf(room1));
        }

        ListView listview = (ListView) findViewById(R.id.rl_listview_roominfo);
        RlAdapter rl_adapter = new RlAdapter(this, list);
        listview.setAdapter(rl_adapter);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add4":
                rl_flag=0;
                break;
            case "del":
                rl_flag=1;
                break;
        }
        rl_roomname=s[1];
        rl_tag=s[2];
        rl_id=s[3];
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,RoomWait.class);
        startActivity(intent);
    }
}
