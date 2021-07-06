package com.webserva.wings.android.pl2_spread;

import android.content.Intent;
import android.hardware.Sensor;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;

public class Client {
    static enum screens{
        Title,
        MainMenu,
        RoomMenu,
        RoomList,
        RoomWait,
        Profile,
        Ranking,
        TagSet,
        MemberSelect,
        RoomInfo,
        Ready,
        ResultMap,
        ResultExp,
        HReady,
        Game,
        GameEnd,
        TeamSplit,
        TeamSplitResult,
        TeamResultMap,
        MoveLocation,
        LevelUp
    }

    static MemberInfo myInfo;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationClient;
    Sensor pedometer;
    static int[] expTable;

    void createInfo(String name, String id){
        myInfo = new MemberInfo(name, id);
    }
//    void display(screens s){
//        finish();
//        startActivity(new Intent(Game.this, Result.class));
//    }

//    void setTimer(long msec){
//
//    }

    static void sendMessage(String message){
        //moveのとき別処理
    }

    static void receiveMessage(String message){
        String[] s=message.split("\\$");
        switch(s[0]){ //最初のパート
            case "rank":
            case "best":
            case "num":
                Ranking.receiveMessage(message);
                break;

            case "add4":
            case "del":
                RoomList.receiveMessage(message);
                break;

            case "approved":
            case "declined":
                RoomWait.receiveMessage(message);
                break;

            case "add10":
            case "delete10":
            case "broken":
            case "confirm":
                RoomInfo.receiveMessage(message);
                break;

            case "add9":
            case "delete9":
                MemberSelect.receiveMessage(message);
                break;

            case "start":
                Ready.receiveMessage(message);
                break;

            case "readyall":
                HReady.receiveMessage(message);
                break;

            case "otherpos12":
            case "score12":
                ResultMap.receiveMessage(message);
                break;

            case "score13":
                ResultExp.receiveMessage(message);
                break;

            case "gps17":
                TeamSplit.receiveMessage(message);
                break;

            case "gps18":
                TeamSplitResult.receiveMessage(message);
                break;

            case "otherpos19":
            case "score19":
                TeamResultMap.receiveMessage(message);
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
