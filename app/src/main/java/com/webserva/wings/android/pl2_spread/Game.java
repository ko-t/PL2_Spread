package com.webserva.wings.android.pl2_spread;

import android.Manifest;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
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
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Game extends ComponentActivity implements SensorEventListener {
    ProgressBar progressBar;
    long time = 3 * 60 * 1000;
    //long time = 15000;

    private SensorManager sensorManager;
    private Sensor sensor;
    TextView speed, step;
    boolean speedy = false;
    Button button_full, button_sleep;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.i("Game_Location", locationResult.getLastLocation().toString());
                if (speedy) {
                    Client.sendMessage("startpos");
                    Log.i("Game_Start", locationResult.getLastLocation().toString());
                    speedy = false;
                }
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
        button_full = findViewById(R.id.gm_button_fullscreen);
        button_sleep = findViewById(R.id.gm_button_sleep);

        Window w = getWindow();

        button_full.setOnClickListener(v -> {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (Build.VERSION.SDK_INT >= 30) {
                Log.i("Game", "SDK_30~/" + getWindow().getInsetsController());
                getWindow().getInsetsController().hide(
                        WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                getWindow().getInsetsController().setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                Log.i("Game", getWindow().getInsetsController().getSystemBarsBehavior() + "");
            } else {
                Log.i("Game", "NOT_SDK_30~");
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        });
        action();
    }

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
                            .setTitle(R.string.general_message)
                            .setMessage(R.string.gm_no_pos)
                            .setPositiveButton("OK", null)
                            .show();
                }

                Client.fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(Game.this, location -> {
                            if (location != null) {
                                Client.goal = new LatLng(location.getLatitude(), location.getLongitude());
                                Log.i("Game_Goal", location.toString());
                                Client.sendMessage("goalpos");
                            } else {
                                Toast.makeText(Game.this, R.string.gm_no_pos, Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        };
        Client.fusedLocationClient.getLastLocation()
                .addOnSuccessListener(Game.this, location -> {
                    if (location != null) {
                        Client.start = new LatLng(location.getLatitude(), location.getLongitude());
                        Client.sendMessage("startpos");
                        Log.i("gm_action", "test");
                    } else {
                        Toast.makeText(Game.this, R.string.gm_no_getpos, Toast.LENGTH_SHORT).show();
                        speedy = true;
                        locationRequest.setInterval(1000);
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
        //Toast.makeText(this, "acc changed", Toast.LENGTH_SHORT).show();
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
                    .setTitle(R.string.general_message)
                    .setMessage(R.string.gm_no_pos)
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

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "result":
                Client.finishActivity();
                if (Client.myInfo.getTeam() == -1)
                    Client.startActivity(new Intent(Client.context, ResultMap.class));
                else {
                    Client.startActivity(new Intent(Client.context, TeamResultMap.class));
                }
                break;
        }
    }
}

