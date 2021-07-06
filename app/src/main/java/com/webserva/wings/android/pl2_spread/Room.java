package com.webserva.wings.android.pl2_spread;


import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Room {
    private String roomName;
    private int tag;
    private Entry<String, Integer> hostId;
    private Map<String, Integer> member;
    private boolean isOpen;
    private int count = 0;

    Room(String roomName, int tag, String hostid) {
        this.roomName = roomName;
        this.tag = tag;
        this.hostId = new SimpleEntry(hostId, 0);
        member = new TreeMap<>();
        member.put(hostId.getKey(), hostId.getValue());
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

    public void setHostId(Entry<String, Integer> hostId) {
        this.hostId = hostId;
    }

    public Map<String, Integer> getMember() {
        return member;
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

    public boolean increaseAndCheckCount() {
        count++;
        if(count == member.size()){
            count = 0;
            return true;
        } else return false;
    }
}
