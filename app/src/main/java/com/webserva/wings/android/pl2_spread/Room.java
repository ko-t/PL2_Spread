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
}
