package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TeamSplitResult extends AppCompatActivity implements View.OnClickListener {
    private static int tsr_flag;
    private static String tsr_roomname,tsr_tag,tsr_id;
    private Button tsr_button_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teamsplitresult);
        tsr_button_next=(Button)findViewById(R.id.tsr_button_next);
        tsr_button_next.setOnClickListener(this);

        receiveMessage("gps18$room1$tag1$id1");
        Integer tsr_tag1 = Integer.parseInt(tsr_tag);
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        Room room1 = new Room(tsr_roomname, tsr_tag1, tsr_id);

        List<Room> list_rock = new ArrayList<>();
        ListView listview1 = (ListView) findViewById(R.id.tsr_listview_rock);
        Rw_Ri_Tsr_Adapter adapter_rock = new Rw_Ri_Tsr_Adapter(this, list_rock);
        listview1.setAdapter(adapter_rock);

//        //rock側のリスト生成
//        Room room1 = new Room("room1",1,"19641");
//        Room room2 = new Room("room2",2,"19642");
//        List<Room> list = new ArrayList<>();
//        list.add(room1);
//        list.add(room2);
//        ListView listview = (ListView) findViewById(R.id.tsr_listview_rock);
//        Rw_Ri_Tsr_Adapter adapter = new Rw_Ri_Tsr_Adapter(this, list);
//        listview.setAdapter(adapter);

//        //paper側のリスト生成
//        Room name3 = new Room("name3",1,"19643");
//        List<Room> list_paper = new ArrayList<>();
//        list_paper.add(name3);
//        ListView listview_paper = (ListView) findViewById(R.id.tsr_listview_paper);
//        Rw_Ri_Tsr_Adapter rw_ri_tsr_adapter_paper = new Rw_Ri_Tsr_Adapter(this, list_paper);
//        listview_paper.setAdapter(rw_ri_tsr_adapter_paper);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "gps18":
                break;
        }
    }

    //画面遷移
    @Override
    public void onClick(View v) {
        if(v==tsr_button_next){   //画面15(Game)に遷移
            Intent intent = new Intent(this,RoomInfo.class);
            startActivity(intent);
        }
    }
}
