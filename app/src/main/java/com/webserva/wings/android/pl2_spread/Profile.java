package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
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
        pr_textView_level.setText(String.valueOf(Client.calcLevel(Client.myInfo.getExp())));
        TextView pr_textView_id = findViewById(R.id.pr_textView_id);
        pr_textView_id.setText(Client.myInfo.getId());
        TextView pr_textView_east = findViewById(R.id.pr_textView_east);
        pr_textView_east.setText(String.valueOf(Client.myInfo.getStatus().get(0)));
        TextView pr_textView_south = findViewById(R.id.pr_textView_south);
        pr_textView_south.setText(String.valueOf(Client.myInfo.getStatus().get(1)));
        TextView pr_textView_west = findViewById(R.id.pr_textView_west);
        pr_textView_west.setText(String.valueOf(Client.myInfo.getStatus().get(2)));
        TextView pr_textView_north = findViewById(R.id.pr_textView_north);
        pr_textView_north.setText(String.valueOf(Client.myInfo.getStatus().get(3)));

        EditText pr_plainText_name= findViewById(R.id.pr_plainText_name);
        pr_plainText_name.setText(Client.myInfo.getName());
        pr_plainText_name.setFocusable(false);

        Button pr_button_editName = findViewById(R.id.pr_button_editName);
        pr_button_editName.setOnClickListener(v -> {
            if(pr_button_editName.getText().equals(getString(R.string.pro_edi))){
                pr_button_editName.setText(R.string.pro_fin_edi);
                pr_plainText_name.setFocusable(true);
                pr_plainText_name.setFocusableInTouchMode(true);
                pr_plainText_name.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.showSoftInput(pr_plainText_name, InputMethodManager.SHOW_IMPLICIT);
            } else {
                Client.sendMessage("new");
                Client.myInfo.setName(pr_plainText_name.getText().toString());
                pr_button_editName.setText(R.string.pro_edi);
                pr_plainText_name.setFocusable(false);
                pr_plainText_name.setFocusableInTouchMode(false);
            }
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