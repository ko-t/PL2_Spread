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

    MemberInfo myInfo;
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
        //みてい
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
