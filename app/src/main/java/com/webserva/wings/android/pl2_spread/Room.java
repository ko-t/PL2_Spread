package com.webserva.wings.android.pl2_spread;

import android.util.Log;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Room {
    private String roomName, message;
    private int tag, memberNum = 0;
    private Entry<String, Integer> hostId = new SimpleEntry<>(null, null); //TODO 連戦のときのhostIdの変化
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

    Room(String roomName, int tag, String hostid) {
        this.roomName = roomName;
        this.tag = tag;
        this.hostId = new SimpleEntry<>(hostid, 0);
        Log.i("Room.java", hostId.getKey());
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

    public Entry<String, Integer> getHostId() {
        return hostId;
    }

    public void setHostId(SimpleEntry<String, Integer> hostId) {
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

    public class myEntry<K, V> implements Entry<K, V>, java.io.Serializable {
        private static final long serialVersionUID = -8499721149061103585L;
        private K key;
        private V value;

        public myEntry() {
        }

        @Override
        public K getKey() {
            return null;
        }

        @Override
        public V getValue() {
            return null;
        }

        @Override
        public V setValue(V value) {
            return null;
        }
    }
}
