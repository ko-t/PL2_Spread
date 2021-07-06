package com.webserva.wings.android.pl2_spread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
public class Rw_Ri_Tsr_Adapter extends ArrayAdapter<Room> {
    private LayoutInflater mInflater;
    private TextView list_rw_ri_tsr_playername, list_rw_ri_tsr_id;

    public Rw_Ri_Tsr_Adapter(Context context, List<Room> objects) {
        super(context, 0, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_rw_ri_tsr_layout, null);
        }
        final Room item = this.getItem(position);
        if (item != null) {
            list_rw_ri_tsr_playername = (TextView) convertView.findViewById(R.id.list_rw_ri_tsr_textview_playername);
            list_rw_ri_tsr_playername.setText(item.getRoomName());
            list_rw_ri_tsr_id = (TextView) convertView.findViewById(R.id.list_rw_ri_tsr_textview_id);
            list_rw_ri_tsr_id.setText(String.valueOf(item.getHostId()));
        }
        return convertView;
    }
}