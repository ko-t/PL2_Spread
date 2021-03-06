package com.webserva.wings.android.pl2_spread;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.TreeMap;

public class Ranking extends AppCompatActivity {
    private static String rk_str_count="初期値";
    private static List<SimpleEntry<Integer,String>> rank = new ArrayList<>();
    private static TextView rk_count, rk_myRank, rk_myScore;
    private static RkAdapter rk_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        rk_count = findViewById((R.id.rk_textview_count));
        rk_myScore = findViewById((R.id.rk_textview_b_score));
        rk_myRank = findViewById((R.id.rk_textview_b_ranking));
        rk_adapter = new RkAdapter(this, rank);

        //receiveMessage("rank$3$820$280$208");
        receiveMessage("numrank$" + Client.myInfo.getMatchHistory());
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

                rank.clear();
                //渡されたランキングデータを Map に格納
                for(int i=0;i<rank_count;i++){
                    rank.add(new SimpleEntry<Integer, String>(i+1,s[2 * i+2] + "$" + s[2 * i+3]));   //  rank_data の i-2 を リストへ
                }
                break;

            //s[0]="best", s[1]="順位", s[2]="ベストスコア"
            case "best":
                Log.i("rk_receiveMessage","bestを受信しました");
                rk_myScore.setText(s[2]);
                rk_myRank.setText(s[1]);
                break;

            //s[0]="num", s[1]="プレイ回数"
            case "numrank":
                Log.i("rk_receiveMessage","numを受信しました");
                rk_str_count=s[1];
                rk_count.setText(rk_str_count);
                break;
        }
        rk_adapter.notifyDataSetChanged();
    }
}
