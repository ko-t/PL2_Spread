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
    private static int ri_flag;
    private static String ri_roomname,ri_tag,ri_id;
    private Button ri_button_quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roominfo);
        ri_button_quit=(Button)findViewById(R.id.ri_button_quit);
        ri_button_quit.setOnClickListener(this);

        receiveMessage("add10$room1$tag1$id1");
        Integer ri_tag1 = Integer.parseInt(ri_tag);
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        Room room1 = new Room(ri_roomname, ri_tag1, ri_id);

        //ホストの表示
        List<Room> list_host = new ArrayList<>();
        ListView listview1 = (ListView) findViewById(R.id.ri_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        listview1.setAdapter(adapter_host);

        //メンバーのリスト作成
        List<Room> list_member = new ArrayList<>();
        if(ri_flag==0) {   //追加
            list_member.add(room1);
        }else if(ri_flag==1){   //削除
            list_member.remove(list_member.indexOf(room1));
        }
        //メンバの表示
        ListView listview2 = (ListView) findViewById(R.id.ri_listview_member);
        Rw_Ri_Tsr_Adapter adapter_member = new Rw_Ri_Tsr_Adapter(this, list_member);
        listview1.setAdapter(adapter_member);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add10":
                ri_flag=0;
                break;
            case "del10":
                ri_flag=1;
                break;
            case "broken":
                break;
            case "confirm":
                break;
        }
        ri_roomname=s[1];
        ri_tag=s[2];
        ri_id=s[3];
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
