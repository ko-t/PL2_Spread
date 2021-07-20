package com.webserva.wings.android.pl2_spread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoomList extends AppCompatActivity implements View.OnClickListener {
    private static int rl_flag=-1;
    private static List<Map<String, Integer>> players, room;
    private static TextView textview_count;
    private static Room new_room;
    private static int size;
    static List<Room> list;
    private static ListView listview;
    private static RlAdapter rl_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomlist);
        findViewById(R.id.rl_button_search).setOnClickListener(this);
        textview_count = (TextView)findViewById(R.id.rl_host_textview_count);

        Client.sendMessage("roomreq");

        //listviewについて
        list = new ArrayList<>();

        listview = (ListView) findViewById(R.id.rl_listview_roominfo);
        rl_adapter = new RlAdapter(this, list);
        listview.setAdapter(rl_adapter);

        // ListView中の要素がタップされたときの処理を記述
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                int hostid;     //listviewから持ってくる
                String hosttag;    //同様
                String hostname;   //同様
                Intent intent_list = new Intent(RoomList.this, RoomWait.class);
                //intent_list.putExtra("TAG",hosttag);
                //intent_list.putExtra("HOSTID",hostid);
                //intent_list.putExtra("HOSTNAME",hostname);

                startActivity(intent_list);
                Log.i("rl_onCreate","RoomWait.classが開始されました");

            }
        });


        //searchviewについての処理
        SearchView rl_search = findViewById(R.id.rl_searchview_name);
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
                Log.i("rl_onQueryTextChange","ルーム名検索が実行されました");
                return false;
            }
        });



    }

    static void receiveMessage(String message) {
        //部屋リストを要求
        //Client.sendMessage("roomreq");
        //部屋リストの情報もらう
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "apply":
                //apply$ホストID
                Log.i("rl_receive.Message","追加するルーム情報が渡されました");
                //new_room = new Room(hostid);
                //メンバのデータを取得
                //players=new_room.getMember();
                //メンバの人数(要素数)の取得
                textview_count.setText(players.size());
                list.add(new_room);

                Log.i("rl_onCreate","ルームが追加されました");
                break;
            case "del":
                //del$ホストID
                //new_room = new Room(hostid);
                list.remove(list.indexOf(new_room));
                Log.i("rl_onCreate","ルームが削除されました");


                size= list.size();
                int k=0;
                while(k<size){
                    //ホストID == HostId<String,Integer>
//                    for(Map.Entry<String, Integer> entry : (list.get(k)).getHostId().entrySet()){
//                        if(entry.getKey()==s[1]){
//                            list.remove(list.indexOf(new_room));
//                            break;
//                        }
//                    }
                }
                Log.i("ms_onCreate","メンバリストのメンバが退出しました");

                break;

            case "num":
                //num$ホストのID$新しい人数
                break;

            case "add4":
                //今だけ

                            Intent intent_list = new Intent(Client.context, RoomWait.class);
                            intent_list.putExtra("TAG",s[2]);
                            intent_list.putExtra("HOSTID",s[3]);
                            intent_list.putExtra("HOSTNAME",s[4]);
                            Client.sendMessage("apply$" + s[4]);

                            Client.startActivity(intent_list);
                            Log.i("rl_onCreate","RoomWait.classが開始されました");

        }
    }

    //チェックボックスで検索をかけたとき
    @Override
    public void onClick(View v) {

    }
}

