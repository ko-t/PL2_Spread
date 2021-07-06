package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;


public class Profile extends AppCompatActivity implements TextWatcher{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView pr_textView_level = findViewById(R.id.pr_textView_level);
        pr_textView_level.setText(Client.myInfo.getLevel());
        TextView pr_textView_id = findViewById(R.id.pr_textView_id);
        pr_textView_id.setText(Client.myInfo.getId());
        TextView pr_textView_east = findViewById(R.id.pr_textView_east);
        pr_textView_east.setText(Client.myInfo.getStatus()[0]);
        TextView pr_textView_south = findViewById(R.id.pr_textView_south);
        pr_textView_south.setText(Client.myInfo.getStatus()[1]);
        TextView pr_textView_west = findViewById(R.id.pr_textView_west);
        pr_textView_west.setText(Client.myInfo.getStatus()[2]);
        TextView pr_textView_north = findViewById(R.id.pr_textView_north);
        pr_textView_north.setText(Client.myInfo.getStatus()[3]);

        EditText pr_plainText_name= findViewById(R.id.pr_plainText_name);
        pr_plainText_name.setText(Client.myInfo.getName());
        pr_plainText_name.setFocusable(false);

        Button pr_button_editName = findViewById(R.id.pr_button_editName);
        pr_button_editName.setOnClickListener(v -> {
            pr_plainText_name.setFocusable(true);
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        
    }
}