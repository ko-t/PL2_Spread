package com.webserva.wings.android.pl2_spread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    private String roomName;
    private int tag;
    private Map<String, Integer> hostId;
    private List<Map<String, Integer>> member;

    Room(String roomName, int tag, String hostid){
        this.roomName = roomName;
        this.tag = tag;
        this.hostId = new HashMap<>();
        this.hostId.put(hostid, 0);
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

    public Map<String, Integer> getHostId() {
        return hostId;
    }

    public void setHostId(Map<String, Integer> hostId) {
        this.hostId = hostId;
    }

    public List<Map<String, Integer>> getMember() {
        return member;
    }

    public void setMember(List<Map<String, Integer>> member) {
        this.member = member;
    }
}
