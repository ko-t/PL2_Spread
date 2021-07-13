package com.webserva.wings.android.pl2_spread;

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
    private static String ri_roomname,ri_tag,ri_id;
    private static Button ri_button_quit;
    private static Intent intent;
    private static List<MemberInfo> list_member;
    private static Room room;
    private static TextView text_name,text_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roominfo);
        ri_button_quit=(Button)findViewById(R.id.ri_button_quit);
        ri_button_quit.setOnClickListener(this);

        Integer ri_tag1 = Integer.parseInt(ri_tag);
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        room = new Room(ri_roomname, ri_tag1, ri_id);

        //ホストの表示
        List<MemberInfo> list_host = new ArrayList<>();
        ListView listview1 = (ListView) findViewById(R.id.ri_listview_host);
        Rw_Ri_Tsr_Adapter adapter_host = new Rw_Ri_Tsr_Adapter(this, list_host);
        listview1.setAdapter(adapter_host);

        //メンバーのリスト作成
        list_member = new ArrayList<MemberInfo>();

        //メンバの表示
        ListView listview2 = (ListView) findViewById(R.id.ri_listview_member);
        Rw_Ri_Tsr_Adapter adapter_member = new Rw_Ri_Tsr_Adapter(this, list_member);
        listview1.setAdapter(adapter_member);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add10":
                //add10$ユーザ名$ユーザID
                //list_member.add();
                Log.i("ri_receiveMessage","サーバからadd10を受け取りました");
                break;

            case "del10":
                //delete10$ユーザID
                //while
                //list_member.remove(list_member.indexOf());
                Log.i("ri_receiveMessage","サーバからdel9を受け取りました");
                break;

            //ホストの接続が切れたとき
            //部屋にいた人に通知、部屋選択(RoomList.java)に遷移
            case "broken":
                //broken
                RoomInfo ri = new RoomInfo();
                AlertDialog.Builder builder = new AlertDialog.Builder(ri);
                builder.setMessage("ホストの接続が切れました。\n部屋選択画面に移動します。")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //OKボタンで、Roomlist画面に遷移
                                Client.finishActivity();
                                intent = new Intent(Client.context, RoomList.class);
                                Client.startActivity(intent);
                            }
                        });
                builder.show();

                break;

            //ホストがメンバの確定を押したとき
            //Ready画面(Ready.java)に遷移
            case "confirm":
                //confirm
                Log.i("ri_receiveMessage","メンバが確定されました");
                Client.finishActivity();
                intent = new Intent(Client.context, Ready.class);
                Client.startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        //退出するとき
        if(v==ri_button_quit){
            Client.sendMessage("leave");
            Intent intent = new Intent(this,RoomList.class);
            startActivity(intent);
        }
    }

}
