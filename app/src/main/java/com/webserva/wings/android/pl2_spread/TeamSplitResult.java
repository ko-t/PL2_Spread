package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TeamSplitResult extends AppCompatActivity implements View.OnClickListener {
    private static int tsr_flag;
    private static String tsr_roomname,tsr_tag,tsr_id,tsr_player;
    private Button tsr_button_next;
    private static Intent intent;
    private static Integer tsr_rsp;
    private static Room room1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teamsplitresult);
        tsr_button_next=(Button)findViewById(R.id.tsr_button_next);
        tsr_button_next.setOnClickListener(this);

        receiveMessage("gps18$1$playerName$playerId");  //rock
        Integer tsr_tag1 = Integer.parseInt(tsr_tag);

        List<Room> list_rock = new ArrayList<>();
        List<Room> list_paper = new ArrayList<>();

        if(tsr_rsp==0) {
            list_rock.add(room1);
        } else if(tsr_rsp==1){
            list_paper.add(room1);
        }

        ListView listview_rock = (ListView) findViewById(R.id.tsr_listview_rock);
        ListView listview_paper = (ListView) findViewById(R.id.tsr_listview_paper);

        Rw_Ri_Tsr_Adapter adapter_rock = new Rw_Ri_Tsr_Adapter(this, list_rock);
        listview_paper.setAdapter(adapter_rock);

//        //rock側のリスト生成
//        Room room1 = new Room("room1",1,"19641");
//        Room room2 = new Room("room2",2,"19642");
//        List<Room> list = new ArrayList<>();
//        list.add(room1);
//        list.add(room2);
//        ListView listview = (ListView) findViewById(R.id.tsr_listview_rock);

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
            //グー(rock)：0
            //パー(paper)：1
            case "gps18":
                tsr_rsp= Integer.parseInt(s[1]);
                break;
        }
        //arraylist を teamsplit.classからもらう
        tsr_player=intent.getStringExtra(s[2]);
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        room1 = new Room(s[2], 1, tsr_id);
    }

    //画面遷移
    @Override
    public void onClick(View v) {
        if(v==tsr_button_next){   //画面15(Game)に遷移
            Intent intent = new Intent(this,RoomInfo.class);
            Client.finishActivity();
            Client.startActivity(intent);
        }
    }
}
