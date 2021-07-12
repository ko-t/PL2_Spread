package com.webserva.wings.android.pl2_spread;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultMap extends FragmentActivity implements OnMapReadyCallback {
    static Object lock = new Object();
    static int flag = 0;
    static List<LatLng> others_pos = new ArrayList<>(), others_original = new ArrayList<>();
    Button button;
    TextView textView;
    Intent intent_to_re;
    double area = 0, tmp;

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        int num = Integer.parseInt(s[1]);
        switch (s[0]) {
            case "otherpos12":
                for (int i = 0; i < num; i++) {
                    //others_original.add(new LatLng(Double.parseDouble(s[4 * i + 2]), Double.parseDouble(s[4 * i + 3])));
                    //others_original.add(new LatLng(Double.parseDouble(s[4 * i + 4]), Double.parseDouble(s[4 * i + 5])));
                    others_pos.add(moveWithVector(Client.start,
                            new LatLng(Double.parseDouble(s[4 * i + 2]), Double.parseDouble(s[4 * i + 3])),
                            new LatLng(Double.parseDouble(s[4 * i + 4]), Double.parseDouble(s[4 * i + 5]))));
                }
                synchronized (lock) {
                    lock.notifyAll();
                }
                break;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultmap);

        button = findViewById(R.id.rm_button);
        textView = findViewById(R.id.rm_textView_value);

        button.setOnClickListener(v -> {
            Client.finishActivity();
            intent_to_re = new Intent(getApplication(), ResultExp.class);
            intent_to_re.putExtra("SCORE", (int)area);
            Client.startActivity(intent_to_re);
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.rm_map);
        Log.i("rm_onCreate", mapFragment.toString());
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Client.mMap = googleMap;
        Client.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Client.goal, 13));

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;

//        if (flag <= 1) {
//            synchronized (lock) {
//                try {
//                    lock.wait();
//                } catch (Exception e) {
//
//                }
//            }
//        }


        //直線
        for (int i = 0; i < others_pos.size(); i++) {
            Client.mMap.addMarker(new MarkerOptions().position(others_pos.get(i)));
                Polyline polyline = Client.mMap.addPolyline(new PolylineOptions()
                        .add(Client.start, others_pos.get(i))
                        .width(15)
                        .color(Color.BLUE)
                        .geodesic(true));
        }

        //三角形と面積
        List<LatLng> triangle;


        int[] index = new int[3];
        for (int i = 0; i < others_pos.size(); i++) {
            for (int j = i + 1; j < others_pos.size(); j++) {
                for (int k = j + 1; k < others_pos.size(); k++) {
                    triangle = new ArrayList<>(Arrays.asList(others_pos.get(i), others_pos.get(j), others_pos.get(k)));
                    tmp = SphericalUtil.computeArea(triangle);
                    if (area < tmp) {
                        area = tmp;
                        index[0] = i;
                        index[1] = j;
                        index[2] = k;
                    }
                }
            }
        }

        textView.setText(Long.toString(Math.round(area)));

        triangle = new ArrayList<>(Arrays.asList(others_pos.get(index[0]),
                others_pos.get(index[1]), others_pos.get(index[2])));

        Client.mMap.addPolygon(new PolygonOptions()
                .addAll(triangle)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(64, 128, 128, 255)));
    }

    static private double calcAngle(LatLng ll1, LatLng ll2) {
        double x1 = ll1.latitude, x2 = ll2.longitude, y1 = ll1.longitude, y2 = ll2.longitude, r = 6371.0 * 1000.0;
        double angle = 90.0 - Math.atan2(Math.sin(x2 - x1), Math.cos(y1) * Math.tan(y2) - Math.sin(y1) * Math.cos(x2 - x1));
        return angle;
    }

    static private double calcDist(LatLng ll1, LatLng ll2) {
        double x1 = ll1.latitude, x2 = ll2.longitude, y1 = ll1.longitude, y2 = ll2.longitude, r = 6371.0 * 1000.0;
        double d = r * Math.acos(
                Math.sin(y1) * Math.sin(y2) + Math.cos(y1) * Math.cos(y2) * Math.cos(x2 - x1));
        return d;
    }

    static private LatLng moveWithVector(LatLng target, LatLng vecStart, LatLng vecGoal) {
        double a = 6378137.06, f = 1.0 / 298.257223563, b = 6356752.314245,
                fi1 = target.latitude, fi2, U1 = Math.atan((1 - f) * Math.tan(fi1)), U2,
                L, lm1, lm2, al1 = calcAngle(vecStart, vecGoal), al2, al, dsig, sigm = 0.0,
                s = calcDist(vecStart, vecGoal), sig, sig1;
        sig1 = Math.atan(Math.tan(U1) / Math.cos(al1));
        double sinal = Math.cos(U1) * Math.sin(al1);
        double cos2al = 1.0 - sinal * sinal;
        double u2 = cos2al * (a * a - b * b / b * b);
        double A = 1.0 + u2 / 16384.0 * (4096.0 + u2 * (-768.0 + u2 * (320.0 - 175.0 + u2)));
        double B = u2 / 1024.0 * (256.0 + u2 * (-128.0 + u2 * (74.0 - 47.0 * u2)));
        sig = s / b / A;

        for (int i = 0; i < 10; i++) {
            Log.i("rm_vincentyDirect", i + ":" + sig);
            sigm = 2.0 * sig1 + sig; //sigmは2σ_mのこととする
            dsig = B * Math.sin(sig) * (Math.cos(sigm) + 1.0 / 4.0 * B * (Math.cos(sig)
                    * (-1.0 + 2.0 * Math.cos(sigm) * Math.cos(sigm)) - 1.0 / 6.0 * B * Math.cos(sigm)
                    * (-3.0 + 4.0 * Math.sin(sig) * Math.sin(sig))
                    * (-3.0 + 4.0 * Math.cos(sigm) * Math.cos(sigm))));
            sig = s / b / A + dsig;
        }
        fi2 = Math.atan((Math.sin(U1) * Math.cos(sig) + Math.cos(U1) * Math.sin(sig) * Math.cos(al1))
                / ((1.0 - f) * Math.sqrt(Math.pow(sinal, 2.0) + Math.pow(Math.sin(U1) * Math.sin(sig)
                - Math.cos(U1) * Math.cos(sig) * Math.cos(al1), 2.0))));
        double lm = Math.atan(Math.sin(sig) * Math.sin(al1) / (Math.cos(U1) * Math.cos(sig)
                - Math.sin(U1) * Math.sin(sig) * Math.cos(al1)));
        double C = f / 16.0 * cos2al * (4.0 + f * (4.0 - 3.0 * cos2al));
        L = lm - (1.0 - C) * f * sinal * (sig + C * Math.sin(sig) * (Math.cos(sigm)
                + C * Math.cos(sig) * (-1.0 + 2.0 * Math.cos(sigm) * Math.cos(sigm))));
        double L2 = L + target.longitude;
        return new LatLng(fi2, L2);
    }
}
