package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MemberSelect extends AppCompatActivity {
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
        Intent i = getIntent();
        int ms_tag = i.getIntExtra("TAG",101);
        int[] ms_tag_1 = new int[3];
        ms_tag_1[0]=ms_tag/100;
        ms_tag_1[1]=(ms_tag - (ms_tag_1[0]*100))/10;
        ms_tag_1[2]=ms_tag - (ms_tag_1[0]*100)-(ms_tag_1[1]*10);

        TextView ms_roomname = findViewById(R.id.ms_textview_roomname);
        TextView ms_gm = findViewById(R.id.ms_textview_select1);
        TextView ms_se = findViewById(R.id.ms_textview_select2);
        TextView ms_m = findViewById(R.id.ms_textview_select3);
        ms_roomname.setText("room1");
        String str_tag = String.valueOf(ms_tag);

        if(ms_tag_1[0]==0){ ms_gm.setText("対戦");
        }else{ ms_gm.setText("協力"); }
        if(ms_tag_1[1]==0){ ms_se.setText("あり");
        }else{ ms_se.setText("なし"); }
        if(ms_tag_1[2]==0){ ms_m.setText("知ってる人のみ");
        }else{ ms_m.setText("知らない人もOK"); }

        //決定したら画面遷移
        ms_button_decision=findViewById(R.id.ms_button_decision);
        ms_button_decision.setOnClickListener(v -> {
            size=list_member.size();
            for(int j=0;j<size;j++){
                str_cnf=str_cnf+list_member.get(j);
                member.getName();
                if(j==size-1){
                    break;
                }
                str_cnf=str_cnf+"$";
            }
            Client.sendMessage("confirm$"+size+"$"+str_cnf);

            Intent intent = new Intent(this,RoomInfo.class);
            //データ渡す 人数・ユーザ名(連結)・ユーザID(連結)
            intent.putExtra("MEMBER_NUM",size);
//            intent.putExtra("MEMBER_NAME",name);
//            intent.putExtra("MEMBER_ID",id);
            Log.i("ms_onClick","メンバ情報が渡されました");
            Client.startActivity(intent);
        });

        //ルームのインスタンスを生成
        Room room = new Room("room1", ms_tag, "id1");

        //ホストの表示
        List<MemberInfo> list_host = new ArrayList<>();
        ListView listview1 = (ListView) findViewById(R.id.ms_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        room.getHostId();
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
                list_member.add(member);
                Log.i("ms_onCreate","メンバリストのメンバが追加されました");
                Client.sendMessage("accept$"+s[1]);
                Log.i("ms_onCreate","メンバが承認されました");
                break;
            case "del9":
                //del9$ユーザID
                list_member.remove(list_member.indexOf(member));
                Log.i("ms_onCreate","メンバリストのメンバが退出しました");
                break;
        }
    }

}
