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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomWait extends AppCompatActivity implements View.OnClickListener {
    private Button rw_button_quit;  //退出→4 RoomListに戻る
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomwait);

        rw_button_quit=(Button)findViewById(R.id.rw_button_quit);
        rw_button_quit.setOnClickListener(this);

        Room name1 = new Room("name1",1,"19641");
        Room name2 = new Room("name2",2,"19642");
        List<Room> list = new ArrayList<>();
        list.add(name1);
        list.add(name2);
        ListView listview = (ListView) findViewById(R.id.rw_listview_hostname);
        Rw_Ri_Tsr_Adapter rw_ri_tsr_adapter = new Rw_Ri_Tsr_Adapter(this, list);
        listview.setAdapter(rw_ri_tsr_adapter);
    }

    @Override

    public void onClick(View v) {
        if(v==rw_button_quit){    //退出する場合
            Intent intent = new Intent(this,RoomList.class);
            startActivityForResult(intent,0);
        }
    }
    //承認をうけたら10に移動
}