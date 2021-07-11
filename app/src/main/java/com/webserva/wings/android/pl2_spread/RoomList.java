package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomList extends AppCompatActivity implements View.OnClickListener {
    private static int rl_flag=-1;
    private static String rl_roomname,rl_tag,rl_id;
    List<Map<String, Integer>> players;
    private TextView textview_count;
    static List<Room> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomlist);
        findViewById(R.id.rl_button_search).setOnClickListener(this);



        //listviewについて
        receiveMessage("add4$room1$tag1$id1");
        Integer rl_tag1 = Integer.parseInt(rl_tag);
        //サーバからルーム名、タグ、IDを取得し、ルームのインスタンスを生成
        Room room1 = new Room(rl_roomname, rl_tag1, rl_id);

        //メンバのデータを取得
        players=room1.getMember();
        //メンバの人数(要素数)の取得
        textview_count= (TextView)findViewById(R.id.rl_host_textview_count);
        textview_count.setText(players.size());

        list = new ArrayList<>();
        if(rl_flag==0) {     //追加
            list.add(room1);
            System.out.println("ルームが追加されました");
        }else if(rl_flag==1){    //削除
            list.remove(list.indexOf(room1));
            System.out.println("ルームが削除されました");
        }else if(rl_flag==-1){
            System.out.println("新しいルームのデータはありません");
        }


        ListView listview = (ListView) findViewById(R.id.rl_listview_roominfo);
        RlAdapter rl_adapter = new RlAdapter(this, list);
        listview.setAdapter(rl_adapter);

        // ListView中の要素がタップされたときの処理を記述
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent_list = new Intent(RoomList.this, RoomWait.class);
                startActivity(intent_list);
                System.out.println("RoomWait.classが開始されました");
            }
        });



        //searchviewについての処理
        SearchView rl_search = (SearchView) findViewById(R.id.rl_searchview_name);
        listview.setTextFilterEnabled(true);

        // SearchViewの初期表示状態を設定
        //テキストアイコンのみ表示される
        rl_search.setIconifiedByDefault(true);
        // SearchViewのSubmitボタンを使用不可にする
        rl_search.setSubmitButtonEnabled(true);
        // SearchViewに何も入力していない時のテキストを設定
        rl_search.setQueryHint("検索するルーム名を入力");

        // SearchViewにOnQueryChangeListenerを設定
        rl_search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            // SearchViewにテキストを入力する度に呼ばれるイベント
            @Override
            public boolean onQueryTextSubmit(String query) {
                CharSequence queryText = null;
                if (TextUtils.isEmpty(queryText)) {
                    listview.clearTextFilter();
                } else {
                    listview.setFilterText(queryText.toString());
                }
                return true;
            }

            // SearchViewのSubmitButtonを押下した時に呼ばれるイベント
            //submitボタンは使用不可中
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add4":
                rl_flag=0;
                System.out.println("追加するルーム情報が渡されました");
                break;
            case "del":
                rl_flag=1;
                System.out.println("削除するルーム情報が渡されました");
                break;
        }
        rl_roomname=s[1];
        rl_tag=s[2];
        rl_id=s[3];
    }

    //検索をかけたとき
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,RoomWait.class);
        startActivity(intent);
    }
}

