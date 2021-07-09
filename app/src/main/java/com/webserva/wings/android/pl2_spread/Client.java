package com.webserva.wings.android.pl2_spread;

/*
  　receiveMessageで画面遷移するときはこれを使ってください

     static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "start":
                Client.finishActivity();
                Intent i = new Intent(Client.context, {次のクラス}.class);
                Client.startActivity(i);
                break;
        }
    }

       また、receiveMessageをクラスに実装したら、ClientのreceiveMessageの
   コメントアウトを解除してくれると手間が省けます。

 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    String address = "10.0.2.2";
    int port = 38443;

    static MemberInfo myInfo;
    static GoogleMap mMap;
    static FusedLocationProviderClient fusedLocationClient;
    static LatLng goal;
    Sensor pedometer;
    static int[] expTable;
    static PrintWriter out;
    static Context context;

//    static enum screens{
//        Title,
//        MainMenu,
//        RoomMenu,
//        RoomList,
//        RoomWait,
//        Profile,
//        Ranking,
//        TagSet,
//        MemberSelect,
//        RoomInfo,
//        Ready,
//        ResultMap,
//        ResultExp,
//        HReady,
//        Game,
//        GameEnd,
//        TeamSplit,
//        TeamSplitResult,
//        TeamResultMap,
//        MoveLocation,
//        LevelUp
//    }

    void init(){
        new Thread(() -> {
            InetSocketAddress address = new InetSocketAddress( Client.this.address,Client.this.port);
            Socket socket = new Socket();
            try {
                socket.connect(address, 3000);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());
                sendMessage("test");
                String s;
                while (true) {
                    s = br.readLine();
                    if (s != null) {
                        receiveMessage(s);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }) .start();
    }

    static void finishActivity(){
        ((Activity) context).finish();
    }

    static void startActivity(Intent i){
        context.startActivity(i);
    }


    void createInfo(String name, String id){
        myInfo = new MemberInfo(name, id);
    }
    static void sendMessage(String message){
        String[] s=message.split("\\$");
        switch(s[0]) {
            case "startpos":
            case "goalpos":
                //位置情報を送る
                break;

            case "move":
                //wip
                break;
        }
        out.println(message);
    }

    static void receiveMessage(String message){
        String[] s=message.split("\\$");
        switch(s[0]){
            case "status":
                int[] news  = {Integer.parseInt(s[1]), Integer.parseInt(s[2]),
                        Integer.parseInt(s[3]),Integer.parseInt(s[4])};
                myInfo.setStatus(news);
                break;
            case "rank":
            case "best":
            case "num":
//                Ranking.receiveMessage(message);
                break;

            case "add4":
            case "del":
//                RoomList.receiveMessage(message);
                break;

            case "approved":
            case "declined":
//                RoomWait.receiveMessage(message);
                break;

            case "add10":
            case "delete10":
            case "broken":
            case "confirm":
//                RoomInfo.receiveMessage(message);
                break;

            case "add9":
            case "delete9":
//                MemberSelect.receiveMessage(message);
                break;

            case "start":
                Ready.receiveMessage(message);
                break;

            case "readyall":
//                HReady.receiveMessage(message);
                break;

            case "otherpos12":
            case "score12":
                ResultMap.receiveMessage(message);
                break;

            case "score13":
//                ResultExp.receiveMessage(message);
                break;

            case "gps17":
//                TeamSplit.receiveMessage(message);
                break;

            case "gps18":
//                TeamSplitResult.receiveMessage(message);
                break;

            case "otherpos19":
            case "score19":
//                TeamResultMap.receiveMessage(message);
                break;

            case "showresult":
//                Game.receiveMesage(message);
                break;
        }
    }

//    void moveLocation(double x, double y){
//
//    }

//    void playSound(String message){
//
//    }

    //y=204x+9796（仮）
    int calcLevel(int exp){
        return (int)(Math.floor(((double)exp-9796.0))/204.0);
    }

    int calcNextExp(int exp){
        return 204*(calcLevel(exp)+1)+9796-exp;
    }
}
