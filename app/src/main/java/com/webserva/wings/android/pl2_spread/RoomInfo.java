package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//確定した(承認された)プレイヤの表示画面
public class RoomInfo extends AppCompatActivity implements View.OnClickListener {
    private Button ri_button_quit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roominfo);

        ri_button_quit=(Button)findViewById(R.id.ri_button_quit);
        ri_button_quit.setOnClickListener(this);

        Room name1 = new Room("name1",1,"19641");
        Room name2 = new Room("name2",2,"19642");
        List<Room> list = new ArrayList<>();
        list.add(name1);
        list.add(name2);
        ListView listview = (ListView) findViewById(R.id.ri_listview_member);
        MsAdapter adapter = new MsAdapter(this, list);
        listview.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        //退出するとき
        if(v==ri_button_quit){
            Intent intent = new Intent(this,RoomList.class);
            startActivity(intent);
        }
    }

    //データをもらって画面１１(Ready)に遷移
}
