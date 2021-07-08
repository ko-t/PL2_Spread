package com.webserva.wings.android.pl2_spread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

// list_ms.xmlのアダプター
//Roominfo.xmlに導入

public class RkAdapter extends ArrayAdapter<Room> {
    private LayoutInflater mInflater;
    private TextView list_rk_rank, list_rk_score;

    public RkAdapter(Context context, List<Room> objects) {
        super(context, 0, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_rk, null);
        }
        final Room item = this.getItem(position);
        if (item != null) {
            list_rk_rank = (TextView) convertView.findViewById(R.id.list_rk_textview_rank);
            list_rk_rank.setText(item.getRoomName());
            list_rk_score = (TextView) convertView.findViewById(R.id.list_rk_textview_score);
            list_rk_score.setText(String.valueOf(item.getHostId()));
        }
        return convertView;
    }
}