package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomList extends AppCompatActivity implements View.OnClickListener {
    private static int rl_flag=-1;
    private static List<Map<String, Integer>> players;
    private static TextView textview_count;
    private static Room new_room;
    static List<Room> list;
    private int[] tagStatus = {0,0,0};
    private int tag;
    private boolean searchFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomlist);
        findViewById(R.id.rl_button_search).setOnClickListener(this);
        textview_count = (TextView)findViewById(R.id.rl_host_textview_count);

        //listviewについて
        receiveMessage("add4$room1$tag1$id1");
        list = new ArrayList<>();

        ListView listview = (ListView) findViewById(R.id.rl_listview_roominfo);
        RlAdapter rl_adapter = new RlAdapter(this, list);
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
                Log.i("rl_onQueryTextChange","ルーム名検索が実行されました");
                return false;
            }
        });

        RadioGroup rl_radiogroup_s1 = findViewById(R.id.rl_radiogroup_s1);
        RadioGroup rl_radiogroup_s2 = findViewById(R.id.rl_radiogroup_s2);
        RadioGroup rl_radiogroup_s3 = findViewById(R.id.rl_radiogroup_s3);
        RadioButton rl_radiobutton_coop = findViewById(R.id.rl_radiobutton_coop);
        RadioButton rl_radiobutton_on = findViewById(R.id.rl_radiobutton_on);
        RadioButton rl_radioButton_known = findViewById(R.id.rl_radioButton_known);

        Button rl_button_search = findViewById(R.id.rl_button_search);
        rl_button_search.setOnClickListener(v -> {
            searchFlag = true;
            if(rl_radiogroup_s1.getCheckedRadioButtonId() != -1) {
                rl_radiogroup_s1.setBackgroundColor(ContextCompat.getColor(this, R.color.Transparent));
            }else {
                searchFlag = false;
                rl_radiogroup_s1.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red));
            }

            if(rl_radiogroup_s2.getCheckedRadioButtonId() != -1) {
                rl_radiogroup_s2.setBackgroundColor(ContextCompat.getColor(this, R.color.Transparent));
            }else {
                searchFlag = false;
                rl_radiogroup_s2.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red));
            }

            if(rl_radiogroup_s3.getCheckedRadioButtonId() != -1) {
                rl_radiogroup_s3.setBackgroundColor(ContextCompat.getColor(this, R.color.Transparent));
            }else {
                searchFlag = false;
                rl_radiogroup_s3.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red));
            }

            if(searchFlag) {
                if(rl_radiobutton_coop.isChecked()) {
                    tagStatus[0] = 0;
                }else {
                    tagStatus[0] = 1;
                }

                if(rl_radiobutton_on.isChecked()) {
                    tagStatus[1] = 0;
                }else {
                    tagStatus[1] = 1;
                }

                if(rl_radioButton_known.isChecked()) {
                    tagStatus[2] = 0;
                }else {
                    tagStatus[2] = 1;
                }
                tag = tagStatus[0]*4+tagStatus[1]*2+tagStatus[2];

                List<Room> tempList = new ArrayList<Room>();
                for(Room item : list) {
                    if(item.getTag() == tag) {
                        tempList.add(item);
                    }
                }
                rl_adapter.clear();
                rl_adapter.addAll(tempList);
                rl_adapter.notifyDataSetChanged();
            }
        });



    }

    static void receiveMessage(String message) {
        //部屋リストを要求
        Client.sendMessage("roomreq");
        //部屋リストの情報もらう
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "apply":
                //apply$ホストID
                Log.i("rl_receive.Message","追加するルーム情報が渡されました");
                //new_room = new Room(hostid);
                //メンバのデータを取得
                players=new_room.getMember();
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
                break;
            case "num":
                //num$ホストのID$新しい人数
                break;
        }
    }

    //チェックボックスで検索をかけたとき
    @Override
    public void onClick(View v) {

    }
}

