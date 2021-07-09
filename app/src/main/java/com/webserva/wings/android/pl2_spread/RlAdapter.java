package com.webserva.wings.android.pl2_spread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class RlAdapter extends ArrayAdapter<Room> {
    private LayoutInflater mInflater;
    private TextView list_rl_roomname, list_rl_setting,list_rl_count;

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
            list_rl_roomname = (TextView) convertView.findViewById(R.id.rl_host_textview_roomname);
            list_rl_roomname.setText(item.getRoomName());
            list_rl_setting = (TextView) convertView.findViewById(R.id.rl_host_textview_settting);
            list_rl_setting.setText(String.valueOf(item.getHostId()));

            //メンバーの人数
            list_rl_count = (TextView) convertView.findViewById(R.id.rl_host_textview_count);
            //list_rl_count.setText(String.valueOf());

        }
        return convertView;
    }
}
