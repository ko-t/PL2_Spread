package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import java.io.*;
import java.util.Arrays;

public class Title extends AppCompatActivity {

    String filename = "userInfo", TAG = "Title";
    Intent intent;
    Boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        String text = null;
        File file = new File(this.getFilesDir(), "userInfo");
        try {
            //name id exp status
            BufferedReader br = new BufferedReader(new FileReader(file));
            text = br.readLine();
            if (text.isEmpty()) {
                isNew = true;
            } else {
                isNew = false;
                String s[] = text.split("\\$");
                MemberInfo mi = new MemberInfo(s[0], s[1]);
                mi.addExp(Integer.parseInt(s[2]));
                mi.setStatus(Arrays.asList(Integer.parseInt(s[3]), Integer.parseInt(s[4]), Integer.parseInt(s[5]), Integer.parseInt(s[6])));
                Client.myInfo = mi;
                Client.init(s[2], false);
            }
        } catch (IOException e) {
            isNew = true;
            Log.i(TAG, "1");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Client.init2(this);
                if (isNew) {
                    intent = new Intent(getApplication(), TitleDebug.class);
                } else {
                    intent = new Intent(getApplication(), MainMenu.class);
                }
                Client.startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return false;
    }
}