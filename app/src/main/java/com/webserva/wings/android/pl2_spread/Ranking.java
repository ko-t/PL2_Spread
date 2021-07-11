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
