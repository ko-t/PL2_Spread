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
    private static int ms_flag;
    private static String ms_roomname,ms_tag,ms_id;
    private Button ms_button_decision;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberselect);
        ms_button_decision=(Button)findViewById(R.id.ms_button_decision);
        ms_button_decision.setOnClickListener(this);

        receiveMessage("add9$room1$tag1$id1");
        Integer ms_tag1 = Integer.parseInt(ms_tag);
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        Room room1 = new Room(ms_roomname, ms_tag1, ms_id);

        //ホストの表示
        List<Room> list_host = new ArrayList<>();
        ListView listview1 = (ListView) findViewById(R.id.ms_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        listview1.setAdapter(adapter_host);

        //メンバーのリスト作成
        List<Room> list_member = new ArrayList<>();
        if(ms_flag==0) {   //追加
            list_member.add(room1);
            System.out.println("ms_メンバリストのメンバが追加されました");
        }else if(ms_flag==1){   //削除
            list_member.remove(list_member.indexOf(room1));
            System.out.println("ms_メンバリストのメンバが退出しました");
        }
        //メンバの表示
        ListView listview2 = (ListView) findViewById(R.id.ms_listview_memberlist);
        MsAdapter adapter_member = new MsAdapter(this, list_member);
        listview1.setAdapter(adapter_member);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add9":
                ms_flag=0;
                System.out.println("ms_サーバからadd9を受け取りました");
                break;
            case "del9":
                ms_flag=1;
                System.out.println("ms_サーバからdel9を受け取りました");
                break;
        }
        ms_roomname=s[1];
        ms_tag=s[2];
        ms_id=s[3];
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
