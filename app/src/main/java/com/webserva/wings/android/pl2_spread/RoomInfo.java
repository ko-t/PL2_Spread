package com.webserva.wings.android.pl2_spread;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

//確定した(承認された)プレイヤの表示画面
public class RoomInfo extends AppCompatActivity implements View.OnClickListener {
    private static ImageButton ri_imageButton_quit;
    private static Intent intent;
    private static List<MemberInfo> list_member;

    private static int[] ri_tag_1 = new int[3];
    static Rw_Ri_Tsr_Adapter adapter_member;
    private static String str_name, str_id;

    static ListView listview2;
    static Context c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roominfo);
        ri_imageButton_quit=findViewById(R.id.ri_imageButton_quit);
        ri_imageButton_quit.setOnClickListener(this);
        roomInfo = this;
        c = this;

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
        list_host.add(new MemberInfo(ri_hostname, ri_id));
        ListView listview1 = findViewById(R.id.ri_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        listview1.setAdapter(adapter_host);

        //メンバーのリスト作成
        list_member = new ArrayList<>();
        listview2 = findViewById(R.id.ri_listview_member);
        adapter_member = new Rw_Ri_Tsr_Adapter(this, list_member);
        listview2.setAdapter(adapter_member);
    }

    static Activity roomInfo;

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add10":
                //add10$ユーザ名$ユーザID
//                Log.i("ri_receiveMessage","サーバからadd10を受け取りました");
//                MemberInfo member = new MemberInfo(s[1], s[2]);
//                list_member.add(member);
//                adapter_member.notifyDataSetChanged();
//                Log.i("ri_receiveMessage","メンバリストのメンバが追加されました" + list_member.toString());

                list_member = new ArrayList<>();
                adapter_member.clear();
                for(int i=0; i<Integer.parseInt(s[1]); i++){
                    MemberInfo member = new MemberInfo(s[2*i+2], s[2*i+3]);
                    list_member.add(member);
                }
                for(MemberInfo x :list_member){
                    Log.i("RoomInfo", x.getId());
                }
                adapter_member = new Rw_Ri_Tsr_Adapter(c, list_member);
                listview2.setAdapter(adapter_member);
                new testThread().start();
                break;

            case "del10":
                //delete10$ユーザID
                Log.i("ri_receiveMessage","サーバからdel10を受け取りました");
                int size = list_member.size();
                int k=0;
                while(k< size){
                    //ユーザID == リストk番目のid
                    Log.i("ri_receiveMessage", list_member.get(k).getId());
                    if(s[1].equals (list_member.get(k).getId()) ){
                        list_member.remove(k);
                        break;
                    }
                    k++;
                }
                adapter_member.notifyDataSetChanged();
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
                                Log.i("mm_setOnClickListener","RoomList画面に遷移");
                            }
                        }).show();
                break;

            //ホストがメンバの確定を押したとき
            //Ready画面(Ready.java)に遷移
            case "confirm":
                //confirm
                Log.i("ri_receiveMessage","メンバが確定されました");
                Client.finishActivity();
                if(ri_tag_1[0]>=4) {
                    intent = new Intent(Client.context, Ready.class);
                    intent.putExtra("STATUS_TAG",ri_tag_1[1]);
                }
                else {
                    //メンバーの名前とIDの文字列の作る
                    StringJoiner sjName = new StringJoiner("$"), sjId = new StringJoiner("$");
                    for(int x=0;x<list_member.size();x++){
                        sjName.add(list_member.get(x).getName());
                        sjId.add(list_member.get(x).getId());
                    }

                    intent = new Intent(Client.context, TeamSplit.class);
                    intent.putExtra("MEMBER_NUM",list_member.size());
                    intent.putExtra("MEMBER_NAME", sjName.toString());
                    intent.putExtra("MEMBER_ID", sjId.toString());
                    intent.putExtra("STATUS_TAG", ri_tag_1[1]);

                    Log.i("MemberSelect", list_member.size() + "/" + sjName.toString() + "/" + sjId.toString() + "/" + ri_tag_1[1]);

                }
                Client.startActivity(intent);
                Log.i("mm_setOnClickListener","Ready画面に遷移");
                break;
        }


    }

    static class testThread extends Thread{
        @Override
        public void run() {
            try {
                int i=0;
                while(++i<=5){
                    Thread.sleep(1000);
                    Log.i("RoomInfo", list_member.size() + "");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog(){

    }

    @Override
    public void onClick(View v) {
        //退出するとき
        if(v==ri_imageButton_quit){
            Client.sendMessage("leave");
            Intent intent = new Intent(this,RoomList.class);
            Client.startActivity(intent);
            Log.i("mm_setOnClickListener","RoomList画面に遷移");
        }
    }

    @Override
    public void onBackPressed() {
        onClick(ri_imageButton_quit);
    }
}
