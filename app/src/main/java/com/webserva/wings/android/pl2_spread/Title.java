package com.webserva.wings.android.pl2_spread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class Title extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Intent intent_to_mm = new Intent(getApplication(), MainMenu.class);
                Client.startActivity(intent_to_mm);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return false;
    }
}