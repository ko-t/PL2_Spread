package com.webserva.wings.android.pl2_spread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RlAdapter extends ArrayAdapter<Room> {
    private LayoutInflater mInflater;

    public RlAdapter(Context context, List<Room> objects) {
        super(context, 0, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_rl_layout, null);
        }
        final Room item = this.getItem(position);
        if (item != null) {
            TextView list_rl_roomname = (TextView) convertView.findViewById(R.id.rl_host_textview_roomname);
            list_rl_roomname.setText(item.getRoomName());

            //タグの設定
            TextView list_rl_setting = (TextView) convertView.findViewById(R.id.rl_host_textview_settting);
            int tag = item.getTag();
            int[] tag_1 = new int[3];
            tag_1[0]=tag/100;
            tag_1[1]=(tag - (tag_1[0]*100))/10;
            tag_1[2]=tag - (tag_1[0]*100)-(tag_1[1]*10);
            String str_tag="";
            if(tag_1[0]==0){ str_tag=str_tag+"対戦";
            }else{ str_tag=str_tag+"協力"; }
            str_tag=str_tag+"/";
            if(tag_1[1]==0){ str_tag=str_tag+"あり";
            }else{ str_tag=str_tag+"なし"; }
            str_tag=str_tag+"/";
            if(tag_1[2]==0){ str_tag=str_tag+"知ってる人のみ";
            }else{ str_tag=str_tag+"知らない人もOK"; }
            list_rl_setting.setText(str_tag);

            //メンバーの人数

        }
        return convertView;
    }
}
