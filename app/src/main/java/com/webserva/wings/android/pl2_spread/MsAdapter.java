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

public class RlAdapter extends ArrayAdapter<Room> {
    private LayoutInflater mInflater;
    private TextView list_rl_playername, list_rl_id;
    private Button mButton;

    public RlAdapter(Context context, List<Room> objects) {
        super(context, 0, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_rl, null);
        }
        final Room item = this.getItem(position);
        if (item != null) {
            list_rl_playername = (TextView) convertView.findViewById(R.id.list_rl_textview_playername);
            list_rl_playername.setText(item.getRoomName());
            list_rl_id = (TextView) convertView.findViewById(R.id.list_rl_textview_id);
            list_rl_id.setText(String.valueOf(item.getHostId()));
            mButton = (Button) convertView.findViewById(R.id.list_rl_button_ok);
            mButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //承認ボタンをクリックしたときの動作
                }
            });
        }
        return convertView;
    }
}