package com.webserva.wings.android.pl2_spread;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Ranking extends AppCompatActivity {
    private static String rk_str_count="初期値";
    private static List<Map<Integer,String>> rank = new ArrayList<>();
    private static Map<Integer,String> rank_data = new TreeMap<>();
    private static TextView rk_count;
    private static RkAdapter rk_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        rk_count = findViewById((R.id.rk_textview_count));
        rk_adapter = new RkAdapter(this, rank);

        //receiveMessage("rank$3$820$280$208");
        receiveMessage("num$" + Client.myInfo.getMatchHistory());
        Client.sendMessage("rankreq");
        //rk_textview_countの内容をrk_countで設定

        rk_count.setText(rk_str_count);

        ListView listview = findViewById(R.id.rk_listview_ranking);


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
                int rank_count = Integer.parseInt(s[1]);

                //渡されたランキングデータを Map に格納
                for(int i=2;i<rank_count+2;i++){
                    rank_data.put(i-2,s[i]);         // key:i-2, 値:スコア
                    rank.add(i-2,rank_data);   //  rank_data の i-2 を リストへ
                }
                break;

            //s[0]="best", s[1]="順位", s[2]="ベストスコア"
            case "best":
                Log.i("rk_receiveMessage","bestを受信しました");
                int best_rank = Integer.parseInt(s[1]);
                rank_data.put(best_rank,s[2]);
                rank.add(rank_data);    //ランクは10位まで、ベストスコアは11位の位置
                break;

            //s[0]="num", s[1]="プレイ回数"
            case "num":
                Log.i("rk_receiveMessage","numを受信しました");
                rk_str_count=s[1];
                rk_count.setText(rk_str_count);
                break;
        }
        rk_adapter.notifyDataSetChanged();
    }
}
