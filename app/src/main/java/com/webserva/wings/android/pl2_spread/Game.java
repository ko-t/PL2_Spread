package com.webserva.wings.android.pl2_spread;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Game extends ComponentActivity {

    ProgressBar pb;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    action();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        if (ActivityCompat.checkSelfPermission(
                Game.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) { action();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            Toast.makeText(this, "needs location permission", Toast.LENGTH_LONG).show();
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void action(){
        Client.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView tv = findViewById(R.id.game_timer);
        SimpleDateFormat sdf =
                new SimpleDateFormat("mm:ss:SS", Locale.US);

        CountDownTimer cdt = new CountDownTimer(3 * 60000 , 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv.setText(sdf.format(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                pb = findViewById(R.id.game_prog_waitingOthers);
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
                //else startActivity(new Intent(Game.this, TeamResultMap.class));

            }
        };
        cdt.start();

        Client.fusedLocationClient.getLastLocation()
                .addOnSuccessListener(Game.this, location -> {
                    if (location != null) {
                        Client.sendMessage("startpos");
                    } else {
                        Toast.makeText(Game.this, "位置情報がありません", Toast.LENGTH_SHORT).show();
                    }
                });
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

