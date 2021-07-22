package com.webserva.wings.android.pl2_spread;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.lang.System.exit;

public class Game extends ComponentActivity implements SensorEventListener {
    ProgressBar progressBar;
    //long time = 3 * 60 * 1000;
    long time = 5000;

    private SensorManager sensorManager;
    private Sensor sensor;
    TextView speed, step;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    speed.setText(String.format(Locale.US, "%.3f m/s", location.getSpeed()));
                }
            }
        };

        speed = findViewById(R.id.gm_tv_speedvalue);
        step = findViewById(R.id.gm_tv_stepvalue);

        if (ActivityCompat.checkSelfPermission(
                Game.this, Manifest.permission.ACTIVITY_RECOGNITION) ==
                PackageManager.PERMISSION_GRANTED) {
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            if (Build.VERSION.SDK_INT >= 29) {
                new AlertDialog.Builder(Client.context)
                        .setTitle("メッセージ")
                        .setMessage("このゲームを遊ぶには、歩行を検知するために身体活動の権限を許可する必要があります。")
                        .setPositiveButton("OK", null)
                        .show();
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            if (Build.VERSION.SDK_INT >= 29) {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        }

        if (ActivityCompat.checkSelfPermission(
                Game.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            action();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(Client.context)
                    .setTitle("メッセージ")
                    .setMessage("このゲームを遊ぶには、位置情報の権限を許可する必要があります。")
                    .setPositiveButton("OK", null)
                    .show();
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    action();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.gm_message)
                            .setMessage("このゲームは位置情報と身体活動の取得を利用しないとプレイすることができません。")
                            .setPositiveButton("許可する", (dialog, which) -> {
                                Game.this.requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                            })
                            .setNegativeButton("キャンセル", (dialog, which) -> {
                                exit(0);
                            })
                            .show();
                }
            });

    private void action() {
        Client.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView tv = findViewById(R.id.gm_timer);
        SimpleDateFormat sdf =
                new SimpleDateFormat("mm:ss:SS", Locale.US);

        CountDownTimer cdt = new CountDownTimer(time, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv.setText(sdf.format(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                progressBar = findViewById(R.id.gm_prog_waitingOthers);
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
                if (Client.myInfo.getTeam() == -1)
                    Client.startActivity(new Intent(getApplication(), ResultMap.class));
                else {
                    Client.startActivity(new Intent(getApplication(), TeamResultMap.class));
                }

            }
        };
        Client.fusedLocationClient.getLastLocation()
                .addOnSuccessListener(Game.this, location -> {
                    if (location != null) {
                        Client.start = new LatLng(location.getLatitude(), location.getLongitude());
                        Client.sendMessage("startpos");
                        Log.i("gm_action", "test");
                    } else {
                        Toast.makeText(Game.this, "位置情報がありません", Toast.LENGTH_SHORT).show();
                    }
                });
        cdt.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        step.setText(String.valueOf(event.values[0]));

        StringBuffer sb = new StringBuffer();
        for (float x : event.values) {
            sb.append(x);
            sb.append(", ");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Toast.makeText(this, "acc changed", Toast.LENGTH_SHORT).show();
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        startLocationUpdates();
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Client.fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Game.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(Game.this)
                    .setTitle(R.string.gm_message)
                    .setMessage("位置情報が利用できません。")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            Client.fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    private LocationCallback locationCallback;
    private LocationRequest locationRequest = LocationRequest.create()
            .setInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
}

