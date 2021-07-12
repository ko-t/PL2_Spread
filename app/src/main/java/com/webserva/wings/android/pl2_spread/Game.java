package com.webserva.wings.android.pl2_spread;

import android.Manifest;
import android.app.AlertDialog;
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

import static java.lang.System.exit;

public class Game extends ComponentActivity {

    ProgressBar progressBar;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    action();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.gm_message)
                            .setMessage("このゲームは位置情報を利用しないとプレイすることができません。")
                            .setPositiveButton("許可する", (dialog, which) -> {
                                Game.this.requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                            })
                            .setNegativeButton("キャンセル", (dialog, which) -> {
                                exit(0);
                            })
                            .show();
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
            Toast.makeText(this, "位置情報を許可してください。", Toast.LENGTH_LONG).show();
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

        CountDownTimer cdt = new CountDownTimer(5000 , 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv.setText(sdf.format(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                progressBar = findViewById(R.id.game_prog_waitingOthers);
                progressBar.setVisibility(android.widget.ProgressBar.VISIBLE);

                if (ActivityCompat.checkSelfPermission(Game.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(Game.this)
                            .setTitle(R.string.gm_message)
                            .setMessage("位置情報が利用できません。")
                            .setPositiveButton("OK", null)
                            .show();
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

                Client.finishActivity();
                //if (Client.myInfo.getTeam() == -1)
                    Client.startActivity(new Intent(getApplication(), ResultMap.class));
                //else Client.finishActivity();startActivity(new Intent(Game.this, TeamResultMap.class));

            }
        };
        Client.fusedLocationClient.getLastLocation()
                .addOnSuccessListener(Game.this, location -> {
                    if (location != null) {
                        Client.start = new LatLng(location.getLatitude(), location.getLongitude());
                        Client.sendMessage("startpos");
                    } else {
                        Toast.makeText(Game.this, "位置情報がありません", Toast.LENGTH_SHORT).show();
                    }
                });
        cdt.start();
    }
}

