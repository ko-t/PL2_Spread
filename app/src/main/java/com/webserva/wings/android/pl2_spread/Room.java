package com.webserva.wings.android.pl2_spread;

import android.util.Log;

import androidx.annotation.Keep;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

public class Room {
    private String roomName, message, hostId, hostName;
    private int tag, memberNum = 0;
    //teamの値
    // -3:非承認
    // -2:未承認（承認待ち）
    // -1:承認済み
    //  0:メンバー確定

    //  0:グー
    //  1:パー
    private boolean isOpen;
    private int count = 0, gpCount = 0, endCount = 0;

    public Room() {
    }

    Room(String roomName, int tag, String hostid, String hostName) {
        this.roomName = roomName;
        this.tag = tag;
        this.hostId = hostid;
        this.hostName = hostName;
        isOpen = true;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getCount() {
        return count;
    }

    public int getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(int memberNum) {
        this.memberNum = memberNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
