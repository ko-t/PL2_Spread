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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomlist);
        findViewById(R.id.rl_button_search).setOnClickListener(this);

        Room room1 = new Room("room1",1,"19641");
        Room room2 = new Room("room2",2,"19642");
        Room room3 = new Room("room3",3,"19643");
        Room room4 = new Room("room4",4,"19644");
        Room room5 = new Room("room5",5,"19642");
        Room room6 = new Room("room6",6,"19642");
        Room room7 = new Room("room7",7,"19642");
        Room room8 = new Room("room8",8,"19642");
        Room room9 = new Room("room9",9,"19642");
        Room room10 = new Room("room10",10,"19642");
        Room room11 = new Room("room11",11,"19642");
        Room room12 = new Room("room12",12,"19642");

        List<Room> list = new ArrayList<>();
        list.add(room1);
        list.add(room2);
        list.add(room3);
        list.add(room4);
        list.add(room5);
        list.add(room6);
        list.add(room7);
        list.add(room8);
        list.add(room9);
        list.add(room10);
        list.add(room11);
        list.add(room12);
        
        ListView listview = (ListView) findViewById(R.id.rl_listview_roominfo);
        RlAdapter rl_adapter = new RlAdapter(this, list);
        listview.setAdapter(rl_adapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,RoomWait.class);
        startActivity(intent);
    }
}
