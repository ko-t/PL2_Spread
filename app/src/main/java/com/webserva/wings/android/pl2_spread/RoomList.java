package com.webserva.wings.android.pl2_spread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
import android.widget.SearchView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomList extends AppCompatActivity implements View.OnClickListener {
    private static TextView textview_count;
    private static Room new_room;
    private int[] tagStatus = {0,0,0};
    private int tag;
    private boolean searchFlag = false;
    private static int size;
    static List<Room> list = new ArrayList<>();
    ;
    private static ListView listview;
    private static RlAdapter rl_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomlist);
        findViewById(R.id.rl_button_search).setOnClickListener(this);
        textview_count = (TextView) findViewById(R.id.rl_host_textview_count);

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

                String hostid  = list.get(position).getHostId(),
                        hostname = list.get(position).getHostName();
                int hosttag = list.get(position).getTag();
                Intent intent_list = new Intent(RoomList.this, RoomWait.class);
                intent_list.putExtra("TAG",hosttag);
                intent_list.putExtra("HOSTID",hostid);
                intent_list.putExtra("HOSTNAME",hostname);

                Log.i("RoomList_onItemClick", intent_list.toString());

                startActivity(intent_list);
                Log.i("rl_onCreate", "RoomWait.classが開始されました");

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
        rl_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                Log.i("rl_onQueryTextChange", "ルーム名検索が実行されました");
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
        //Client.sendMessage("roomreq");
        //部屋リストの情報もらう

        String hostId;


        String[] s = message.split("\\$");
        switch (s[0]) {
            case "add4":
                //add4$ルーム名$タグ$ホストID$ホスト名$現在の人数
                Log.i("rl_receive.Message", "追加するルーム情報が渡されました");

                Integer tag = Integer.parseInt(s[2]);
                new_room = new Room(s[1], tag, s[3], s[4]);

                list.add(new_room);
                rl_adapter.notifyDataSetChanged();
                Log.i("rl_onCreate", "ルームが追加されました");

                //申し込むルーム
                Client.sendMessage("apply$" + s[3]);
                break;

            case "del":
                //del$ホストID
                size = list.size();
                int k = 0;
                while (k < size) {
                    //ホストID == HostId<String,Integer>
                    //変更ホストIDをStringにしました（三苫）

                    hostId = (list.get(k)).getHostId();
                    if (hostId.equals(s[1])) {
                        list.remove(list.indexOf(s[1]));
                        break;
                    }
                }
                rl_adapter.notifyDataSetChanged();
                Log.i("rl_onCreate", "ルームが削除されました");
                break;

            case "num":
                //num$ホストのID$新しい人数
                size = list.size();
                int l = 0;
                int item_position = 0;
                hostId = (list.get(l)).getHostId();
                if (hostId.equals(s[1])) {
                    item_position = list.indexOf(s[1]);
                    break;
                }
                rl_adapter = (RlAdapter) listview.getAdapter();
                Room item = rl_adapter.getItem(item_position);
                item.setMemberNum(Integer.valueOf(s[2]));
                rl_adapter.notifyDataSetChanged();
                break;

//            case "add4":
//                //今だけ
//
//                Intent intent_list = new Intent(Client.context, RoomWait.class);
//                intent_list.putExtra("TAG", s[2]);
//                intent_list.putExtra("HOSTID", s[3]);
//                intent_list.putExtra("HOSTNAME", s[4]);
//                Client.sendMessage("apply$" + s[3]);
//
//                Client.startActivity(intent_list);
//                Log.i("rl_onCreate", "RoomWait.classが開始されました");

        }
    }

    //チェックボックスで検索をかけたとき
    @Override
    public void onClick(View v) {

    }
}

