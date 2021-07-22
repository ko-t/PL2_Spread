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
    private static List<MemberInfo> list_member = new ArrayList<>();
    private static int size;
    private static String str_name,str_id;
    private static MsAdapter adapter_member;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberselect);

        //ルームタグ設定(ゲームモード、ステータス効果、メンバー)
        Intent i = getIntent();
        int ms_tag = i.getIntExtra("TAG",101);
        String ms_id = i.getStringExtra("HOSTID");
        String ms_hostname = i.getStringExtra("HOSTNAME");

        int[] ms_tag_1 = new int[3];
        ms_tag_1[0]=ms_tag/100;
        ms_tag_1[1]=(ms_tag - (ms_tag_1[0]*100))/10;
        ms_tag_1[2]=ms_tag - (ms_tag_1[0]*100)-(ms_tag_1[1]*10);

        TextView ms_roomname = findViewById(R.id.ms_textview_roomname);
        TextView ms_gm = findViewById(R.id.ms_textview_select1);
        TextView ms_se = findViewById(R.id.ms_textview_select2);
        TextView ms_m = findViewById(R.id.ms_textview_select3);

        Room room = new Room(ms_hostname, ms_tag, ms_id);
        ms_roomname.setText(room.getRoomName());

        ms_roomname.setText(ms_hostname);

        if(ms_tag_1[0]==0){ ms_gm.setText("対戦");
        }else{ ms_gm.setText("協力"); }
        if(ms_tag_1[1]==0){ ms_se.setText("あり");
        }else{ ms_se.setText("なし"); }
        if(ms_tag_1[2]==0){ ms_m.setText("知ってる人のみ");
        }else{ ms_m.setText("知らない人もOK"); }

        //receiveMessage("add9$name1$id1");

        //ホストの表示
        List<MemberInfo> list_host = new ArrayList<>();
        ListView listview1 = findViewById(R.id.ms_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        listview1.setAdapter(adapter_host);   //listview(host)に追加

        //メンバの表示
        ListView listview2 = findViewById(R.id.ms_listview_memberlist);
        adapter_member = new MsAdapter(this, list_member, new MsAdapter.ListItemButtonClickListener(){
            public void onItemButtonClick(int position, View view){
                //承認されたときの処理
                //String userId = (list_host.get(position)).getName();
                String userId = (list_member.get(position)).getName();
                Client.sendMessage("accept$"+userId);
                Log.i("ms_onCreate", userId+"を承認しました");
            }
        });
        listview2.setAdapter(adapter_member);


        //メンバーが決定したら画面遷移
        Button ms_button_decision = findViewById(R.id.ms_button_decision);
        ms_button_decision.setOnClickListener(v -> {
            size=list_member.size();
            for(int j=0;j<size;j++){
                str_name=str_name+(list_member.get(j)).getName();   //str_cnf= str_cnf + メンバ名
                str_id=str_id+(list_member.get(j)).getId();
                if(j==size-1){
                    break;
                }
                str_name=str_name+"$";
                str_id=str_id+"$";
            }
            Client.sendMessage("confirm$"+size+"$"+str_name);

            Intent intent = new Intent(this,RoomInfo.class);
            //データ渡す　 人数・ユーザ名(連結)・ユーザID(連結)
            intent.putExtra("MEMBER_NUM",size);
            intent.putExtra("MEMBER_NAME",str_name);
            intent.putExtra("MEMBER_ID",str_id);
            Log.i("ms_onClick","メンバ情報が渡されました");
            Client.startActivity(intent);
        });

    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add9":
                //add9$ユーザ名$ユーザID
                MemberInfo member = new MemberInfo(s[1], s[2]);
                list_member.add(member);
                Log.i("ms_onCreate","メンバリストのメンバが追加されました");
                Client.sendMessage("accept$"+s[1]);
                Log.i("ms_onCreate","メンバが承認されました");
                break;

            case "del9":
                //del9$ユーザID
                size= list_member.size();
                int k=0;
                while(k<size){
                    //ユーザID == リストk番目のid
                    if(s[1].equals (list_member.get(k).getId()) ){
                        list_member.remove(k);
                        break;
                    }
                    k++;
                }
                Log.i("ms_onCreate","メンバリストのメンバが退出しました");
                break;
        }
        adapter_member.notifyDataSetChanged();

    }

}
