package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MemberSelect extends AppCompatActivity implements View.OnClickListener {

    private Button ms_button_decision;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberselect);

        ms_button_decision=(Button)findViewById(R.id.ms_button_decision);
        ms_button_decision.setOnClickListener(this);

        Room room1 = new Room("room1",1,"19641");
        Room room2 = new Room("room2",2,"19642");
        List<Room> list = new ArrayList<>();
        list.add(room1);
        list.add(room2);
        ListView listview = (ListView) findViewById(R.id.ms_listview_memberlist);
        MsAdapter adapter = new MsAdapter(this, list);
        listview.setAdapter(adapter);
    }

    //画面遷移
    @Override
    public void onClick(View v) {
        if(v==ms_button_decision){
            Intent intent = new Intent(this,RoomInfo.class);
            startActivity(intent);
        }
    }
}
