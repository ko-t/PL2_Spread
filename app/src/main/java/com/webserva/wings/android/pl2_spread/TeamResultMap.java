package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamResultMap extends FragmentActivity implements OnMapReadyCallback {
    static Object lock = new Object();
    static int flag = 0;
    static List<LatLng> others_pos1 = new ArrayList<>(), others_pos2 = new ArrayList<>(),
            others_original1 = new ArrayList<>(), others_original2 = new ArrayList<>();
    ImageButton trm_imageButton_next;
    TextView textViewLeft, textViewRight, textViewResult;
    Intent intent_to_re;
    double area = 0, tmp;
    long leftScore, rightScore;
    static boolean plus;

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        int num1 = Integer.parseInt(s[1]), num2 = Integer.parseInt(s[2]);
        switch (s[0]) {
            case "otherpos19":
                others_pos1.clear();
                others_original1.clear();
                others_pos2.clear();
                others_original2.clear();
                plus = Integer.parseInt(s[3]) == 1;
                if (plus) {
                    for (int i = 0; i < num1; i++) { //グーチーム
                        others_original1.add(moveWithVector(Client.start,
                                Double.parseDouble(s[2 * i + 4]),
                                Double.parseDouble(s[2 * i + 5])));
                        others_pos1.add(moveWithVector(others_original1.get(i),
                                Math.toRadians(Double.parseDouble(s[2 * i + 2 * (num1 + num2) + 4])),
                                Double.parseDouble(s[2 * i + 2 * (num1 + num2) + 5])));
                    }
                    for (int i = num1; i < num1 + num2; i++) {
                        others_original2.add(moveWithVector(Client.start,
                                Double.parseDouble(s[2 * i + 4]),
                                Double.parseDouble(s[2 * i + 5])));
                        others_pos2.add(moveWithVector(others_original2.get(i-num1),
                                Math.toRadians(Double.parseDouble(s[2 * i + 2 * (num1 + num2) + 4])),
                                Double.parseDouble(s[2 * i + 2 * (num1 + num2) + 5])));
                    }
                } else {
                    for (int i = 0; i < num1; i++) {
                        others_pos1.add(moveWithVector(Client.start, Double.parseDouble(s[2 * i + 4]), Double.parseDouble(s[2 * i + 5])));
                    }
                    for (int i = num1; i < num1 + num2; i++) {
                        others_pos2.add(moveWithVector(Client.start, Double.parseDouble(s[2 * i + 4]), Double.parseDouble(s[2 * i + 5])));
                    }
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
                Log.i("trm_receiveMessage", others_original1.toString() + "???" + others_pos1.toString());
                break;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teamresultmap);

        trm_imageButton_next = findViewById(R.id.trm_imageButton_next);
        textViewLeft = findViewById(R.id.trm_textView_valueLeft);
        textViewRight = findViewById(R.id.trm_textView_valueRight);
        textViewResult = findViewById(R.id.trm_textView_winlose);

        trm_imageButton_next.setOnClickListener(v -> {
            Client.finishActivity();
            intent_to_re = new Intent(getApplication(), ResultExp.class);
            intent_to_re.putExtra("SCORE", Client.myInfo.getTeam() == 0 ? (int) leftScore : (int) rightScore);
            Client.startActivity(intent_to_re);
            Log.i("mm_setOnClickListener","Team,ResultExp画面に遷移");
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.trm_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Client.mMap = googleMap;
        Client.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Client.goal, 13));
        Client.mMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (flag <= 1) {
//            synchronized (lock) {
//                try {
//                    lock.wait();
//                } catch (Exception e) {
//
//                }
//            }
//        }

        String winlose = null;
        if(plus) {
            leftScore = draw(Color.BLUE, textViewLeft, others_pos1, others_original1);
            rightScore = draw(Color.MAGENTA, textViewRight, others_pos2, others_original2);
        } else {
            leftScore = draw(Color.BLUE, textViewLeft, others_pos1);
            rightScore = draw(Color.MAGENTA, textViewRight, others_pos2);
        }

        if (leftScore == rightScore) winlose = "引き分け";
        else
            winlose = leftScore < rightScore ^ Client.myInfo.getTeam() == 0 ? "勝ち" : "負け";
        textViewResult.setText(winlose);
    }

    private long draw(int color, TextView textView, List<LatLng>... lists) {
        //直線
        List<LatLng> others_pos = lists[0];

        area = 0;
        for (int i = 0; i < others_pos.size(); i++) {
            Client.mMap.addMarker(new MarkerOptions().position(others_pos.get(i)));
            if (lists.length == 2) {
                Polyline polyline = Client.mMap.addPolyline(new PolylineOptions()
                        .add(Client.start, others_pos.get(i))
                        .width(15)
                        .color(color)
                        .geodesic(true));

                Polyline polyline2 = Client.mMap.addPolyline(new PolylineOptions()
                        .add(others_pos.get(i), lists[1].get(i))
                        .width(15)
                        .color(Color.GREEN)
                        .geodesic(true));
            } else {
                Polyline polyline = Client.mMap.addPolyline(new PolylineOptions()
                        .add(Client.start, others_pos.get(i))
                        .width(15)
                        .color(color)
                        .geodesic(true));
            }
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
                .strokeColor(color)
                .fillColor(Color.argb(64, Color.red(color), Color.green(color), Color.blue(color))));

        return Math.round(area);
    }

    static private double calcAngle(LatLng ll1, LatLng ll2) {
        double y1 = Math.toRadians(ll1.latitude), y2 = Math.toRadians(ll2.latitude),
                x1 = Math.toRadians(ll1.longitude), x2 = Math.toRadians(ll2.longitude);
        double angle = Math.toDegrees(Math.atan2(Math.sin(x2 - x1), Math.cos(y1) * Math.tan(y2) - Math.sin(y1) * Math.cos(x2 - x1)));
        if (angle < 0) angle = angle + 360;
        Log.i("rm_calcAngle", String.valueOf(angle));
        return Math.toRadians(angle);
    }


    static private double calcDist(LatLng ll1, LatLng ll2) {
        double y1 = Math.toRadians(ll1.latitude), y2 = Math.toRadians(ll2.latitude),
                x1 = Math.toRadians(ll1.longitude), x2 = Math.toRadians(ll2.longitude), r = 6378.137 * 1000;
        double d = r * Math.acos(
                Math.sin(y1) * Math.sin(y2) + Math.cos(y1) * Math.cos(y2) * Math.cos(x2 - x1));
        Log.i("rm_calcDist", String.valueOf(d));
        return d;
    }

    static LatLng moveWithVector(LatLng target, double angle, double dist) {
//        double a = 6378137.06, f = 1.0 / 298.257223563, b = 6356752.314245,
//                fi1 = target.latitude, fi2, U1 = Math.atan((1.0 - f) * Math.tan(Math.toRadians(fi1))),
//                L, al1 = calcAngle(vecStart, vecGoal), dsig, sigm = 0.0,
//                s = calcDist(vecStart, vecGoal), sig, sig1;
//        //al1, U1はラジアン
//        sig1 = Math.atan(Math.tan(U1) / Math.cos(al1));
//        double sinal = Math.cos(U1) * Math.sin(al1);
//        double cos2al = 1.0 - sinal * sinal;
//        double u2 = cos2al * ((a * a - b * b) / (b * b));
//        double A = 1.0 + u2 / 16384.0 * (4096.0 + u2 * (-768.0 + u2 * (320.0 - 175.0 + u2)));
//        double B = u2 / 1024.0 * (256.0 + u2 * (-128.0 + u2 * (74.0 - 47.0 * u2)));
//        sig = Math.toRadians(s / b / A);
//
//        for (int i = 0; i < 10; i++) {
//            //Log.i("rm_vincentyDirect", i + ":" + sig);
//            sigm = 2.0 * sig1 + sig; //sigmは2σ_mのこととする, rad
//            dsig = B * Math.sin(sig) * (Math.cos(sigm) + 1.0 / 4.0 * B * (Math.cos(sig)
//                    * (-1.0 + 2.0 * Math.cos(sigm) * Math.cos(sigm)) - 1.0 / 6.0 * B * Math.cos(sigm)
//                    * (-3.0 + 4.0 * Math.sin(sig) * Math.sin(sig))
//                    * (-3.0 + 4.0 * Math.cos(sigm) * Math.cos(sigm))));
//            sig = Math.toRadians(s / b / A) + dsig;
//        }
//        fi2 = Math.toDegrees(Math.atan((Math.sin(U1) * Math.cos(sig) + Math.cos(U1) * Math.sin(sig) * Math.cos(al1))
//                / ((1.0 - f) * Math.sqrt(Math.pow(sinal, 2.0) + Math.pow(Math.sin(U1) * Math.sin(sig)
//                - Math.cos(U1) * Math.cos(sig) * Math.cos(al1), 2.0)))));
//        double lm = Math.atan(Math.sin(sig) * Math.sin(al1) / (Math.cos(U1) * Math.cos(sig)
//                - Math.sin(U1) * Math.sin(sig) * Math.cos(al1)));
//        double C = f / 16.0 * cos2al * (4.0 + f * (4.0 - 3.0 * cos2al));
//        L = lm - (1.0 - C) * f * sinal * (sig + C * Math.sin(sig) * (Math.cos(sigm)
//                + C * Math.cos(sig) * (-1.0 + 2.0 * Math.cos(sigm) * Math.cos(sigm))));
//        double L2 = L + target.longitude;
//        return new LatLng(fi2, L2);
        double R = 6378150.0;
        double deltaLat = dist * Math.cos(angle) * 360.0 / (2.0 * Math.PI * R);
        double newLat = target.latitude + deltaLat;
        Log.i("rm_moveWithVector", String.valueOf(deltaLat));

        double earth_radius_at_longitude = R * Math.cos(newLat * Math.PI / 180.0),
                earth_circle_at_longitude = 2.0 * Math.PI * earth_radius_at_longitude,
                longitude_per_meter = 360.0 / earth_circle_at_longitude;

        double newLng = target.longitude + dist * Math.sin(angle) * longitude_per_meter;

        return new LatLng(newLat, newLng);
    }
}