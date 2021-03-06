package com.webserva.wings.android.pl2_spread;



public class MemberInfotester {
    private String name;
    private String id;
    private String roomId = null;
    private int team = -1;
    private int exp = 1;
    private int[] status = {0, 0, 0, 0};
    private int matchHistory = 0;
    private int recordId = -1;

    MemberInfotester(String name, String id) {
        this.id = "id";
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int[] getStatus() {
        return status;
    }

    public void setStatus(int[] status) {
        this.status = status;
    }

    public int getMatchHistory() {
        return matchHistory;
    }

    public void setMatchHistory(int matchHistory) {
        this.matchHistory = matchHistory;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}