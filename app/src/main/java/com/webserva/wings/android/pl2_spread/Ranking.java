package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Ranking extends AppCompatActivity {
    //private Button button;
    private static TextView rk_count;
    private static String rk_str_count="初期値";
    private static List list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);

        //rk_textview_countの内容をrk_countで設定
        rk_count = (TextView) findViewById((R.id.rk_textview_count));
        rk_count.setText(rk_str_count);

        //ランキングの作成
<<<<<<< HEAD
=======
        //ランキング情報はサーバから？
        Room room1 = new Room("room1",1,"19641");
        Room room2 = new Room("room2",2,"19642");
        Room room3 = new Room("room3",3,"19643");
        Room room4 = new Room("room4",4,"19644");
        Room room5 = new Room("room5",5,"19642");
        Room room6 = new Room("room6",6,"19642");
        Room room7 = new Room("room7",7,"19642");
        Room room8 = new Room("room8",8,"19642");
        Room room9 = new Room("room9",9,"19642");
        Room room10 = new Room("room10",10,"19642");
        Room room11 = new Room("room11",11,"19642");
        Room room12 = new Room("room12",12,"19642");

        List<Room> list = new ArrayList<>();
        list.add(room1);
        list.add(room2);
        list.add(room3);
        list.add(room4);
        list.add(room5);
        list.add(room6);
        list.add(room7);
        list.add(room8);
        list.add(room9);
        list.add(room10);
        list.add(room11);
        list.add(room12);

>>>>>>> bef4770ffef50e5560331aa6bf09e52a1ce159cf
        ListView listview = (ListView) findViewById(R.id.rk_listview_ranking);

        RkAdapter rk_adapter = new RkAdapter(this, list);
        listview.setAdapter(rk_adapter);
    }

    //サーバから受信
    static void receiveMessage(String message) {
        //文字列を\\$を区切りとして分ける
        String[] s = message.split("\\$");
        switch (s[0]) {
            //s[0]="rank", s[1]=個数, s[2]="1位のスコア", s[3]="2位のスコア",....
            case "rank":
                Log.i("rk_receiveMessage","rankを受信しました");
                Integer rank_count = Integer.parseInt(s[1]);
                //渡された個数分リストを作成
                for(int i=2;i<rank_count+2;i++){
                    list.add(s[i]);
                }
                break;

            //s[0]="best", s[1]="順位", s[2]="ベストスコア"
            case "best":
                //ピックアップを作る技術はない
                Log.i("rk_receiveMessage","bestを受信しました");
                break;

            //s[0]="num", s[1]="プレイ回数"
            case "num":
                Log.i("rk_receiveMessage","numを受信しました");
                rk_str_count=s[1];
                rk_count.setText(rk_str_count);
                break;
        }
    }

}
