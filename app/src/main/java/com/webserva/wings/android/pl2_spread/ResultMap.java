package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultMap extends FragmentActivity implements OnMapReadyCallback {
    static int score;
    static Object lock;
    static int flag = 0;
    static List<LatLng> others_pos = new ArrayList<>();
    Button button = findViewById(R.id.rm_button);
    TextView textView = findViewById(R.id.rm_textView_value);

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        int num = Integer.parseInt(s[1]);
        switch (s[0]) {
            case "otherpos12":
                for (int i = 0; i < num; i++) {
                    others_pos.add(new LatLng(Double.parseDouble(s[2 * i + 2]), Double.parseDouble(s[2 * i + 3])));
                }
                flag++;
                break;
            case "score12":
                score = num;
                flag++;
                break;
        }
        if (flag == 2) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultmap);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(ResultMap.this, ResultExp.class);
                i.putExtra("SCORE", score);
                startActivity(i);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rm_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Client.mMap = googleMap;
        Client.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Client.goal, 13));

        if (flag <= 1) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (Exception e) {

                }
            }
        }

        //直線
        LatLng even, odd = others_pos.get(0);
        for (int i = 0; i < others_pos.size(); i++) {
            Client.mMap.addMarker(new MarkerOptions().position(others_pos.get(i)));
            if(i%2 == 1){
                even = others_pos.get(i);
                Polyline polyline = Client.mMap.addPolyline(new PolylineOptions()
                        .add(odd, even)
                        .width(25)
                        .color(Color.BLUE)
                        .geodesic(true));
            } else {
                odd = others_pos.get(i);
            }
        }

        //三角形と面積
        List<LatLng> triangle;

        double area = 0, tmp;
        int[] index = new int[3];
        for(int i=0; i<others_pos.size(); i++){
            for(int j=i+1; j<others_pos.size(); j++){
                for(int k=j+1; k<others_pos.size(); k++){
                    triangle = new ArrayList<>(Arrays.asList(others_pos.get(i), others_pos.get(j), others_pos.get(k)));
                    tmp = SphericalUtil.computeArea(triangle);
                    if(area < tmp) {
                        area = tmp;
                        index[0] = i; index[1] = j; index[2] = k;
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
}
