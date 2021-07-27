package com.webserva.wings.android.pl2_spread;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

// list_ms.xmlのアダプター
//Roominfo.xmlに導入

public class MsAdapter extends ArrayAdapter<MemberInfo>{
    private final LayoutInflater mInflater;
    private TextView list_ms_playername, list_ms_id;
    private ListItemButtonClickListener mListener;

    public MsAdapter(Context context, List<MemberInfo> objects, ListItemButtonClickListener listener) {
        super(context,0, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener=listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_ms, null);
        }
        final MemberInfo item = this.getItem(position);
        if (item != null) {
            list_ms_playername = (TextView) convertView.findViewById(R.id.list_ms_textview_playername);
            list_ms_playername.setText(item.getName());
            list_ms_id = (TextView) convertView.findViewById(R.id.list_ms_textview_id);
            list_ms_id.setText(item.getId());

            Button button = (Button)convertView.findViewById(R.id.list_ms_button_ok);
            button.setTag(position);
            button.findViewById(R.id.list_ms_button_ok).setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //処理内容
                    //テスト
                    Button b = (Button)v;
                    b.setBackgroundColor(Color.GRAY);
                    b.setEnabled(false);
                    mListener.onItemButtonClick(position,v);
                }
            });
        }
        return convertView;
    }

    interface ListItemButtonClickListener{
        public void onItemButtonClick(int position, View view);
    }

}