package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TeamSplitResult extends AppCompatActivity implements View.OnClickListener {
    private ImageButton tsr_imageButton_next;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        i = getIntent();
        int num = i.getIntExtra("MEMBER_NUM", 0); //人数
        String name = i.getStringExtra("MEMBER_NAME");
        String id = i.getStringExtra("MEMBER_ID");
        String gp = i.getStringExtra("MEMBER_GP");

        String[] s_name = name.split("\\$");   //n1$n2$n3...
        String[] s_id = id.split("\\$");       //i1$i2$i3...
        String[] s_gp = gp.split("\\$");       //g$p$g...

        Log.i("TeamSplit", num + "/" + name + "/" + id + "/" + gp);

        List<MemberInfo> list_rock = new ArrayList<>();
        List<MemberInfo> list_paper = new ArrayList<>();
        for (int j = 0; j < num; j++) {
            //member情報を生成
            MemberInfo member = new MemberInfo(s_name[j], s_id[j]);
            //rock側のリストに add
            if (s_gp[j].equals("g")) {
                list_rock.add(member);
            } //paper側のリストに add
            else if (s_gp[j].equals("p")) {
                list_paper.add(member);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.teamsplitresult);
        tsr_imageButton_next = findViewById(R.id.tsr_imageButton_next);
        tsr_imageButton_next.setOnClickListener(this);

        ListView listview_rock = findViewById(R.id.tsr_listview_rock);
        ListView listview_paper = findViewById(R.id.tsr_listview_paper);
        Rw_Ri_Tsr_Adapter adapter_rock = new Rw_Ri_Tsr_Adapter(this, list_rock);
        listview_rock.setAdapter(adapter_rock);
        Rw_Ri_Tsr_Adapter adapter_paper = new Rw_Ri_Tsr_Adapter(this, list_paper);
        listview_paper.setAdapter(adapter_paper);
    }

    //画面遷移
    @Override
    public void onClick(View v) {
        if (v == tsr_imageButton_next) {   //画面15(Game)に遷移
            Intent intent;
            if (Client.myInfo.getId().equals(Client.myInfo.getRoomId())) {
                intent = new Intent(this, HReady.class);
            } else {
                intent = new Intent(this, Ready.class);
            }
            intent.putExtra("STATUS_TAG", i.getIntExtra("STATUS_TAG", 0));
            Client.finishActivity();
            Client.startActivity(intent);
        }
    }
}
