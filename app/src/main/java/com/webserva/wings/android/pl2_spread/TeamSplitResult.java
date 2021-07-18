package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TeamSplitResult extends AppCompatActivity implements View.OnClickListener {
    private Button tsr_button_next;
    private static MemberInfo member;
    private static Room room1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent i= getIntent();
        int num = i.getIntExtra("MEMBER_NUM",0); //人数
        String name = i.getStringExtra("MEMBER_NAME");
        String id = i.getStringExtra("MEMBER_ID");
        String gp = i.getStringExtra("MEMBER_GP");
        String[] s_name = name.split("\\$");   //n1$n2$n3...
        String[] s_id = id.split("\\$");       //i1$i2$i3...
        String[] s_gp = gp.split("\\$");       //g$p$g...

        List<MemberInfo> list_rock = new ArrayList<>();
        List<MemberInfo> list_paper = new ArrayList<>();
        for(int j=0;j<num;j++){
            //member情報を生成
            member = new MemberInfo(s_name[j],s_id[j]);
            //rock側のリストに add
            if(s_gp[j]=="g"){
                list_rock.add(member);
            } //paper側のリストに add
            else if(s_gp[j]=="p"){
                list_paper.add(member);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.teamsplitresult);
        tsr_button_next=(Button)findViewById(R.id.tsr_button_next);
        tsr_button_next.setOnClickListener(this);

        ListView listview_rock = (ListView) findViewById(R.id.tsr_listview_rock);
        ListView listview_paper = (ListView) findViewById(R.id.tsr_listview_paper);
        Rw_Ri_Tsr_Adapter adapter_rock = new Rw_Ri_Tsr_Adapter(this, list_rock);
        listview_rock.setAdapter(adapter_rock);
        Rw_Ri_Tsr_Adapter adapter_paper = new Rw_Ri_Tsr_Adapter(this, list_paper);
        listview_paper.setAdapter(adapter_paper);
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            //グー(rock)：0
            //パー(paper)：1

        }
    }

    //画面遷移
    @Override
    public void onClick(View v) {
        if(v==tsr_button_next){   //画面15(Game)に遷移
            Intent intent = new Intent(this,RoomInfo.class);
            Client.finishActivity();
            Client.startActivity(intent);
        }
    }
}
