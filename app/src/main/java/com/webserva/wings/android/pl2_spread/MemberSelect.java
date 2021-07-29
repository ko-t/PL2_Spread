package com.webserva.wings.android.pl2_spread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MemberSelect extends AppCompatActivity {
    private static List<MemberInfo> list_member = new ArrayList<>();
    private static int size;

    private static String str_name, str_id;
    private static MsAdapter adapter_member;
    private static int[] ms_tag_1 = new int[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberselect);

        //ルームタグ設定(ゲームモード、ステータス効果、メンバー)
        Intent i = getIntent();
        int ms_tag = i.getIntExtra("TAG", 0);
        String ms_id = i.getStringExtra("HOSTID");
        String ms_hostname = i.getStringExtra("HOSTNAME");

        for (int j = 0; j < 3; j++) {
            ms_tag_1[2 - j] = ms_tag & (1 << j);
        }

        TextView ms_roomname = findViewById(R.id.ms_textview_roomname);
        TextView ms_gm = findViewById(R.id.ms_textview_select1);
        TextView ms_se = findViewById(R.id.ms_textview_select2);
        TextView ms_m = findViewById(R.id.ms_textview_select3);

        Room room = new Room(ms_hostname, ms_tag, ms_id, ms_hostname);
        ms_roomname.setText(room.getRoomName());

        ms_roomname.setText(ms_hostname);

        if (ms_tag_1[0] == 0) {
            ms_gm.setText(R.string.tg_battle);
        } else {
            ms_gm.setText(R.string.tg_cooperation);
        }
        if (ms_tag_1[1] == 0) {
            ms_se.setText(R.string.tg_on);
        } else {
            ms_se.setText(R.string.tg_off);
        }
        if (ms_tag_1[2] == 0) {
            ms_m.setText(R.string.tg_known);
        } else {
            ms_m.setText(R.string.tg_unknown);
        }


        //ホストの表示
        List<MemberInfo> list_host = new ArrayList<>();
        list_host.add(new MemberInfo(Client.myInfo.getName(), ms_id));
        ListView listview1 = findViewById(R.id.ms_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        listview1.setAdapter(adapter_host);   //listview(host)に追加

        //メンバの表示
        ListView listview2 = findViewById(R.id.ms_listview_memberlist);

        adapter_member = new MsAdapter(this, list_member, new MsAdapter.ListItemButtonClickListener() {
            public void onItemButtonClick(int position, View view) {
                ((Button) view).setEnabled(false);

                //承認されたときの処理
                //String userId = (list_host.get(position)).getName();
                String userId = (list_member.get(position)).getId();
                Client.sendMessage("accept$" + userId);
                Log.i("ms_onCreate", userId + "を承認しました");
            }
        });
        listview2.setAdapter(adapter_member);


        //メンバーが決定したら画面遷移
        ImageButton ms_imageButton_decision = findViewById(R.id.ms_imageButton_decision);
        ms_imageButton_decision.setOnClickListener(v -> {
            size = list_member.size();

            /* 一時保管
            for (int j = 0; j < size; j++) {
                str_name = str_name + (list_member.get(j)).getName();   //str_cnf= str_cnf + メンバ名
                str_id = str_id + (list_member.get(j)).getId();
                if (j == size - 1) {
                    break;
                }
                str_name = str_name + "$";
                str_id = str_id + "$";
            }
            Client.sendMessage("confirm$" + size + "$" + str_name);
            */

            Client.sendMessage("confirm");
            Intent intent;

            if (ms_tag >= 4) {
                intent = new Intent(this, HReady.class);
            } else {
                intent = new Intent(this, TeamSplit.class);
                //メンバーの名前とIDの文字列の作る
                for(int k=0;k<list_member.size()-1;k++){
                    str_name=list_member.get(k).getName()+"$";
                    str_id=list_member.get(k).getId()+"$";
                }
                str_name=list_member.get(list_member.size()-1).getName();
                str_id=list_member.get(list_member.size()-1).getId();
            }



            //データ渡す　 人数・ユーザ名(連結)・ユーザID(連結)
            intent.putExtra("MEMBER_NUM", size);
            intent.putExtra("MEMBER_NAME", str_name);
            intent.putExtra("MEMBER_ID", str_id);
            intent.putExtra("STATUS_TAG", ms_tag_1[1]);

            Log.i("ms_onClick", "メンバ情報が渡されました");
            Client.startActivity(intent);
        });

        //receiveMessage("add9$name1$id1");
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add9":
                //add9$ユーザ名$ユーザID
                MemberInfo member = new MemberInfo(s[1], s[2]);
                list_member.add(member);

                Log.i("ms_onCreate", "メンバリストのメンバが追加されました");
//                Client.sendMessage("accept$"+s[1]);
//                Log.i("ms_onCreate","メンバが承認されました");
                break;

            case "del9":
                //del9$ユーザID
                size = list_member.size();
                int k = 0;
                while (k < size) {
                    //ユーザID == リストk番目のid
                    if (s[1].equals(list_member.get(k).getId())) {
                        list_member.remove(k);
                        break;
                    }
                    k++;
                }

                Log.i("ms_onCreate", "メンバリストのメンバが退出しました");

                break;
        }
        if (adapter_member != null) adapter_member.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MemberSelect.this)
                .setTitle(R.string.general_message)
                .setMessage(R.string.ms_back_confirm)
                .setPositiveButton(R.string.general_ok, (dialog, which) -> {
                    Client.sendMessage("roomdel");
                    super.onBackPressed();
                })
                .setNegativeButton(R.string.general_no, null)
                .show();
    }
}
