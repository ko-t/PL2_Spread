package com.webserva.wings.android.pl2_spread;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Game extends Activity {

    ProgressBar pb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        if (ActivityCompat.checkSelfPermission(Game.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Client.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "cannot use location", Toast.LENGTH_SHORT).show();
            System.exit(-1);
        }

        Client.fusedLocationClient.getLastLocation()
                .addOnSuccessListener(Game.this, location -> {
                    if (location != null) {
                        Client.sendMessage("startpos");
                    } else {
                        Toast.makeText(Game.this, "位置情報がありません", Toast.LENGTH_SHORT).show();
                    }
                });
        test();

        TextView tv = findViewById(R.id.game_timer);
        SimpleDateFormat sdf =
                new SimpleDateFormat("mm:ss", Locale.US);

        CountDownTimer cdt = new CountDownTimer(3 * 60000 - 55000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv.setText(sdf.format(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                pb = findViewById(R.id.waitingOthers);
                pb.setVisibility(android.widget.ProgressBar.VISIBLE);

                if (ActivityCompat.checkSelfPermission(Game.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Client.fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(Game.this, location -> {
                            if (location != null) {
                                Client.goal = new LatLng(location.getLatitude(), location.getLongitude());
                                Client.sendMessage("goalpos");
                            } else {
                                Toast.makeText(Game.this, "位置情報がありません", Toast.LENGTH_SHORT).show();
                            }
                        });
                test();

                finish();
                if (Client.myInfo.getTeam() == -1)
                    startActivity(new Intent(Game.this, ResultMap.class));
                else startActivity(new Intent(Game.this, TeamResultMap.class));

            }
        };
        cdt.start();
    }

    private void test() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(android.widget.ProgressBar.INVISIBLE);
            }
        });

    }
}

