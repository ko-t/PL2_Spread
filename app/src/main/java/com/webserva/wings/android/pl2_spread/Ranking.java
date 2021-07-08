package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Ranking extends AppCompatActivity implements View.OnClickListener {
    //private Button button;
    private TextView rk_count;
    private static String rk_str_count="初期値";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);

        //rk_textview_countの内容をrk_countで設定
        rk_count = (TextView) findViewById((R.id.rk_textview_count));
        //サーバからmessage("num\\$5")を受信
        receiveMessage("num$5");
        System.out.print("rkはメッセージを受信しました");
        rk_count.setText(rk_str_count);

        //ランキングの作成
        //ランキング情報はサーバから？
        Room room1 = new Room("room1",1,"19641");
        Room room2 = new Room("room2",2,"19642");
        List<Room> list = new ArrayList<>();
        list.add(room1);
        list.add(room2);
        ListView listview = (ListView) findViewById(R.id.rk_listview_ranking);
        RkAdapter rk_adapter = new RkAdapter(this, list);
        listview.setAdapter(rk_adapter);
    }

    //サーバから受信
    static void receiveMessage(String message) {
        //文字列を\\$を区切りとして分ける
        String[] s = message.split("\\$");
        switch (s[0]) {
            //s[0]="num", s[1]="5"
            case "num":
                rk_str_count=s[1];
                break;
        }
    }

    //画面遷移
    @Override
    public void onClick(View v) {
//        if(v==ms_button){
//            Intent intent = new Intent(this,RoomInfo.class);
//            startActivity(intent);
//        }
    }
}

//何で遷移するのか　→画面タッチ？戻るボタン？
