package com.webserva.wings.android.pl2_spread;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//確定した(承認された)プレイヤの表示画面
public class RoomInfo extends AppCompatActivity implements View.OnClickListener {
    private static Button ri_button_quit;
    private static Intent intent;
    private static List<MemberInfo> list_member;
    private static int[] ri_tag_1 = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roominfo);
        ri_button_quit=findViewById(R.id.ri_button_quit);
        ri_button_quit.setOnClickListener(this);
        roomInfo = this;

        Intent intent_from_rw = getIntent();
        int ri_tag = intent_from_rw.getIntExtra("TAG",0);
        String ri_id = intent_from_rw.getStringExtra("HOSTID");
        String ri_hostname = intent_from_rw.getStringExtra("HOSTNAME");

        //タグ取得
        for (int j = 0; j < 3; j++) {
            ri_tag_1[2-j] = ri_tag & (1 << j);
        }
        TextView ri_roomname = findViewById(R.id.ri_textview_roomname);
        TextView ri_gm = findViewById(R.id.ri_textview_select1);
        TextView ri_se = findViewById(R.id.ri_textview_select2);
        TextView ri_m = findViewById(R.id.ri_textview_select3);
        if(ri_tag_1[0]==0){ ri_gm.setText(R.string.tg_battle);
        }else{ ri_gm.setText(R.string.tg_cooperation); }
        if(ri_tag_1[1]==0){ ri_se.setText(R.string.tg_on);
        }else{ ri_se.setText(R.string.tg_off); }
        if(ri_tag_1[2]==0){ ri_m.setText("知ってる人のみ");
        }else{ ri_m.setText("知らない人もOK"); }

        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        Room room = new Room(ri_hostname, ri_tag, ri_id, ri_hostname);
        ri_roomname.setText(room.getRoomName());

        //ホストの表示
        List<MemberInfo> list_host = new ArrayList<>();
        ListView listview1 = findViewById(R.id.ri_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        listview1.setAdapter(adapter_host);

        //メンバーのリスト作成
        list_member = new ArrayList<>();
        ListView listview2 = findViewById(R.id.ri_listview_member);
        Rw_Ri_Tsr_Adapter adapter_member = new Rw_Ri_Tsr_Adapter(this, list_member);
        listview2.setAdapter(adapter_member);
    }

    static Activity roomInfo;

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add10":
                //add10$ユーザ名$ユーザID
                Log.i("ri_receiveMessage","サーバからadd10を受け取りました");
                MemberInfo member = new MemberInfo(s[1], s[2]);
                list_member.add(member);
                Log.i("ri_receiveMessage","メンバリストのメンバが追加されました");
                break;

            case "del10":
                //delete10$ユーザID
                Log.i("ri_receiveMessage","サーバからdel9を受け取りました");
                int size = list_member.size();
                int k=0;
                while(k< size){
                    //ユーザID == リストk番目のid
                    if(s[1].equals (list_member.get(k).getId()) ){
                        list_member.remove(k);
                        break;
                    }
                    k++;
                }
                Log.i("ri_receiveMessage","メンバリストのメンバが退出しました");
                break;

            //ホストの接続が切れたとき
            //部屋にいた人に通知、部屋選択(RoomList.java)に遷移
            case "broken":
                AlertDialog.Builder builder = new AlertDialog.Builder(roomInfo);
                builder.setMessage("ホストの接続が切れました。\n部屋選択画面に移動します。")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //OKボタンで、Roomlist画面に遷移
                                Client.finishActivity();
                                intent = new Intent(Client.context, RoomList.class);
                                Client.startActivity(intent);
                            }
                        }).show();
                break;

            //ホストがメンバの確定を押したとき
            //Ready画面(Ready.java)に遷移
            case "confirm":
                //confirm
                Log.i("ri_receiveMessage","メンバが確定されました");
                Client.finishActivity();
                intent = new Intent(Client.context, Ready.class);
                intent.putExtra("STATUS_TAG",ri_tag_1[1]);
                Client.startActivity(intent);
                break;
        }
    }

    private void showDialog(){

    }

    @Override
    public void onClick(View v) {
        //退出するとき
        if(v==ri_button_quit){
            Client.sendMessage("leave");
            Intent intent = new Intent(this,RoomList.class);
            Client.startActivity(intent);
        }
    }

}
