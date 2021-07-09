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
        List<Room> list = new ArrayList<>();
        list.add(room1);
        list.add(room2);
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
