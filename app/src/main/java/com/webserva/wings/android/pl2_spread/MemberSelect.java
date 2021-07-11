package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MemberSelect extends AppCompatActivity implements View.OnClickListener {
    private static String ms_roomname,ms_tag,ms_id;
    private Button ms_button_decision;
    private static List<MemberInfo> list_member = new ArrayList<>();
    private static MemberInfo member;
    private static int size;
    private static String str_cnf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberselect);
        ms_button_decision=(Button)findViewById(R.id.ms_button_decision);
        ms_button_decision.setOnClickListener(this);

        receiveMessage("add9$room1$tag1$id1");
        Integer ms_tag1 = Integer.parseInt(ms_tag);
        //ルームのインスタンスを生成
        Room room = new Room(ms_roomname, ms_tag1, ms_id);

        //ホストの表示
        List<Room> list_host = new ArrayList<>();
        ListView listview1 = (ListView) findViewById(R.id.ms_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        listview1.setAdapter(adapter_host);

        //メンバの表示
        ListView listview2 = (ListView) findViewById(R.id.ms_listview_memberlist);
        MsAdapter adapter_member = new MsAdapter(this, list_member);
        listview1.setAdapter(adapter_member);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add9":
                //add9$ユーザ名$ユーザID
                member= new MemberInfo(s[1],s[2]);
                Client.sendMessage("accept$"+s[1]);
                list_member.add(member);
                Log.i("ms_onCreate","メンバリストのメンバが追加されました");
                break;
            case "del9":
                //add9$ユーザID
                list_member.remove(list_member.indexOf(member));
                Log.i("ms_onCreate","メンバリストのメンバが退出しました");
                break;
        }
    }

    //画面遷移
    @Override
    public void onClick(View v) {
        if(v==ms_button_decision){
            size=list_member.size();
            for(int i=0;i<size;i++){
                str_cnf=str_cnf+list_member.get(i);
                if(i==size-1){
                    break;
                }
                str_cnf=str_cnf+"$";
            }
            Client.sendMessage("confirm$"+size+"$"+str_cnf);
            Intent intent = new Intent(this,RoomInfo.class);
            startActivity(intent);
        }
    }
}
