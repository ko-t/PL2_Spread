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
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class Client {
    //static String serveraddress = "10.0.2.2";
    static String serveraddress = "192.168.1.4";
    //static String serveraddress = "172.20.10.14";
    static int port = 38443, myRank, myScore, rankCounter;
    static long time = 10000;
    private final static String TAG = "Client";

    static MemberInfo myInfo;
    static GoogleMap mMap;
    static FusedLocationProviderClient fusedLocationClient;
    static LatLng start, goal;
    static ListenerRegistration startListener, resultListener;

    static Integer[] expTable = new Integer[100];
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

    static void init(Context c) {
        final int lv1 = 90000;
        myInfo = new MemberInfo("dummyName", "dummyId");
        myInfo.setRoomId("dummyHostId");
        context = c;
        expTable[0] = lv1;
        for (int i = 1; i < 100; i++) {
            expTable[i] = expTable[i - 1] + (int) ((Math.pow(1.033, (double) i) + 0.1 * (double) i) * lv1);
            if (i < 10) Log.d("Client#init", expTable[i] + "");
        }
        sendMessage("register");
    }

//    static void init_connection() {
//        new Thread(() -> {
//            InetSocketAddress address = new InetSocketAddress(serveraddress, port);
//            Socket socket = new Socket();
//            try {
//                socket.connect(address, 3000);
//                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                out = new PrintWriter(socket.getOutputStream());
//                out.println(Client.myInfo.getId());
//                //初回接続の場合
//                //out.println("$" + Client.myInfo.getId() + "$" + Client.myInfo.getName());
////                while(br.readLine().equals("dup")){
////                    //id重複の処理
////                }
//                out.flush();
//                Log.i("Client_init", "id is sent");
//                String s;
//                while (true) {
//                    s = br.readLine();
//                    if (s != null) {
//                        receiveMessage(s);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

    static void finishActivity() {
        ((Activity) context).finish();
    }

    static void startActivity(Intent i) {
        context.startActivity(i);
    }

    static void sendMessage(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] s = message.split("\\$");

        DocumentReference roomRef = db.collection("roomList").document(myInfo.getRoomId()),
                memberInfoRef = db.collection("memberList").document(myInfo.getId());

        switch (s[0]) {
            case "register":
                memberInfoRef.set(myInfo)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                break;
            case "newroom":
                myInfo.setRoomId(myInfo.getId());
                // Roomを作成しリストに追加
                Room newRoom = new Room(s[1], Integer.parseInt(s[2]), myInfo.getId());
                newRoom.setMemberNum(1);
                roomRef.set(newRoom);
                roomRef.collection("member").document(myInfo.getId()).set(new SimpleEntry("team", 0));
                // ルームリストを表示しているユーザに通知（isOpenで通知されるはず）
                //申し込みのリスナー
                roomRef.collection("member").addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                       // com.google.firebase.database.GenericTypeIndicator
                        String name = dc.getDocument().getId();
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New city: " + dc.getDocument().getData());
                                db.collection("memberList").whereEqualTo("id", name).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            MemberInfo mi = document.toObject(MemberInfo.class);
                                            receiveMessage("add9$" + mi.getName() + "$" + mi.getId());
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                });
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                receiveMessage("delete9$" + name);
                                break;
                        }
                    }
                });

                //countの通知をこの時点で追加しておく
                roomRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed in counter.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if (snapshot.get("count", Integer.class).equals(snapshot.get("memberNum", Integer.class))) {
                            receiveMessage("readyall");
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });

                break;

            case "apply":
                //MemberInfoのRoomIdを設定
                db.collection("member").document(myInfo.getId()).update(
                        "RoomId", s[1],
                        "state", "applying"
                );
                db.collection("roomList").document(s[1])
                        .collection("member").document(myInfo.getId()).set(
                        new SimpleEntry<>("team", -2)
                );

                //承認非承認をリッスン
                final DocumentReference docRef = db.collection("roomList").document(myInfo.getRoomId()).collection("member").document(myInfo.getId());
                docRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        switch (snapshot.get("team", Integer.class)) {
                            case 0: //承認
                                receiveMessage("confirm");
                                break;

                            case -1: //承認
                                receiveMessage("approved");
                                break;

                            case -3: //非承認
                                receiveMessage("declined");
                                break;
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
                break;

            case "accept":
                // memberのvalueを-1にする（＝全員に表示させる）TODO
                db.collection("roomList").document(myInfo.getId())
                        .collection("member").document(myInfo.getId())
                        .set(new SimpleEntry<>("team", -1));

                // 許可された人に通知 TODO
//                sendMessage(s[1], "approved");
//                updateMemberList("add10", s[1]);
                break;

            case "leave":
                //roomListから削除
                db.collection("roomList").document(myInfo.getRoomId()).delete();
                //myInfoを更新
                myInfo.setRoomId(null);
                //roomのmemberに通知 TODO
                //updateMemberList("del", from);
                break;

            case "confirm":
                WriteBatch batch = db.batch();
                // 非承認だった人をroomlist画面に戻すために-3を設定
                Query noApproval = db.collection("roomList").document(myInfo.getId())
                        .collection("member").whereEqualTo("team", -2);
                noApproval.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            document.getReference().update(
                                    "team", -3
                            );
                        }
                    } else {
                        Log.d(TAG, "Error getting -2 members ", task.getException());
                    }
                });

                // メンバーに通知
                Query approval = db.collection("roomList").document(myInfo.getId())
                        .collection("member").whereEqualTo("team", -1);
                approval.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            document.getReference().update(
                                    "team", 0
                            );
                        }
                    } else {
                        Log.d(TAG, "Error getting -1 members ", task.getException());
                    }
                });
                // roomをcloseする
                // 下のreadyと連動して、ホスト分カウントしておく
                db.collection("roomList").document(myInfo.getId()).update(
                        "isOpen", false,
                        "count", FieldValue.increment(1),
                        "gpCount", 0
                ).addOnFailureListener(e -> Log.w(TAG, "Error updating \"isOpen\"", e));
                break;

            case "ready":
                DocumentReference ref = db.collection("roomList").document(myInfo.getRoomId());
                // メンバのready状態を変更
                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    transaction.update(ref, "count", FieldValue.increment(1));
                    return null;
                }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e));
                startListener = roomRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if (snapshot.get("message").equals("start")) {
                            receiveMessage("start");
                            startListener.remove();
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
                break;

            case "start":
                roomRef.update(
                        "message", "start",
                        "count", 0,
                        "gpCount", 0
                );
                memberInfoRef.update("matchHistory", FieldValue.increment(1));
                break;

            case "startpos":
                //始点を記録
                memberInfoRef.update("start", start);
                break;

            case "goalpos":
                //終点を記録
                memberInfoRef.update("goal", goal);
                break;

            case "resume":
                // 部屋をopenにする
                roomRef.update("isOpen", true);
                break;

            case "gp":
                // データを保存
                roomRef.collection("member").document(myInfo.getId())
                        .update("team", Integer.parseInt(s[1]));
                roomRef.update("gpCount", FieldValue.increment(1));
                // もし全員集まったらそれぞれに送る
                roomRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if (snapshot.get("gpCount", Integer.class).equals(snapshot.get("memberNum", Integer.class))) {
                            roomRef.collection("member").get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            StringJoiner sj = new StringJoiner("$");
                                            sj.add("gps17");
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                myEntry member = document.toObject(myEntry.class);
                                                sj.add(String.valueOf(member.getKey()));
                                                sj.add(String.valueOf(member.getValue()));
                                            }
                                            receiveMessage(sj.toString());
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
                break;

            case "move":
                goal = moveLocation(goal, (Integer.parseInt(s[1]) - 1) * 90, myInfo.getStatus().get(Integer.parseInt(s[1])));
                break;

//            case "end": TODO リスナー
//                roomRef.update("count", FieldValue.increment(1));
//                // 全員のendが来たら結果を送る。TODO ゲーム終了の処理、ランキング追加などはここ
//                if (tmpRoom.increaseAndCheckCount()) {
//                    for (String to : tmpRoom.getMember().keySet()) {
//                        sendMessage(to, "showresult");
//                    }
//                }
//                break;

            case "newstatus":
                Integer[] tmp = {Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4])};
                memberInfoRef.update("status", Arrays.asList(tmp))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                myInfo.setStatus(Arrays.asList(tmp));
                break;

            case "rankreq":
                final int reqNum = 10;
                StringJoiner sj = new StringJoiner("$");
                sj.add("rank");
                sj.add(String.valueOf(reqNum));
                db.collection("ranking").orderBy("score", Query.Direction.DESCENDING)
                        .limit(reqNum).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int counter = 1;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            //TODO ランキングにチーム名いらないっけ？
                            Score score = document.toObject(Score.class);
                            sj.add(String.valueOf(score.getScore()));
                            if (score.getScoreId() == myInfo.getRecordId()) {
                                myRank = counter;
                                myScore = score.getScore();
                            }
                            counter++;
                        }
                        receiveMessage(sj.toString());
                        if (myRank == -1) {
                            rankCounter = reqNum * 10;
                            while (rankCounter <= 10000) {
                                db.collection("ranking")
                                        .orderBy("score", Query.Direction.DESCENDING).limit(rankCounter)
                                        .orderBy("score", Query.Direction.ASCENDING).limit(1)
                                        .get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            Score score = document.toObject(Score.class);
                                            if (score.getScore() <= myScore) {
                                                myRank = rankCounter;
                                                myScore = score.getScore();
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting ranking ", task1.getException());
                                    }
                                });
                                if (myRank != -1) break;
                                rankCounter *= 10;
                            }
                        }
                        receiveMessage("best$" + myRank + "$" + myScore);
                    } else {
                        Log.d(TAG, "Error getting ranking ", task.getException());
                    }
                });


                //receiveMessage("num$" + memberList.get(memberList.indexOf(from)).getMatchHistory());
                break;

            case "roomreq":
                // 部屋探し中のリストに追加
                myInfo.setState("choosingRoom");
                Query roomWatcher = db.collection("roomList").whereEqualTo("isOpen", true),
                        roomMemberWatcher = db.collectionGroup("member");
                roomWatcher.addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    Room room;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        room = dc.getDocument().toObject(Room.class);
                        switch (dc.getType()) {
                            case ADDED:
                                List<String> sList = Arrays.asList(room.getRoomName(), String.valueOf(room.getTag()),
                                        room.getHostId().getKey(), String.valueOf(room.getHostId().getValue()), "1");
                                StringJoiner sj2 = new StringJoiner("$");
                                sj2.add("add4");
                                sList.forEach(sj2::add);
                                receiveMessage(sj2.toString());
                                break;
                            case MODIFIED: //
                                Log.d(TAG, "Room Modified Info:" + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                receiveMessage("del$" + room.getHostId());
                                break;
                        }
                    }
                });
                roomMemberWatcher.addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    Map memberInRoom;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        memberInRoom = dc.getDocument().getData();
                        String roomName = dc.getDocument().getReference().getParent().getId();
                        Log.d(TAG, "MemberChange in Room " + roomName);
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "MemberChange Added Info:" + dc.getDocument().getData());
                                break;
                            case MODIFIED:
                                receiveMessage("num$" + roomName + "$" + memberInRoom.size());
                                Log.d(TAG, "MemberChange Modified Info:" + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d(TAG, "MemberChange Removed Info:" + dc.getDocument().getData());
                                break;
                        }
                    }
                });
                break;

            case "newscore": //新しいスコアが自分のベストか確認、またホストならランキングに登録
                if(myInfo.getId().equals(myInfo.getRoomId())){
                    db.collection("ranking").orderBy("scoreid", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                int num = document.toObject(Score.class).getScoreId()+1;
                                Score newScore = new Score();
                                newScore.setScore(Integer.parseInt(s[1]));
                                newScore.setScoreId(num);
                                roomRef.get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document1 = task1.getResult();
                                        if (document1.exists()) {
                                            Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                                            newScore.setTeamName(document1.toObject(Room.class).getRoomName());
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task1.getException());
                                    }
                                });
                                db.collection("ranking").document(String.valueOf(num)).set(newScore);
                            }
                        } else {
                            Log.d(TAG, "Error getting ranking ", task.getException());
                        }
                    });
                }
                db.collection("ranking").document(String.valueOf(myInfo.getRecordId())).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if(document.toObject(Score.class).getScore() < Integer.parseInt(s[1])){

                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
                break;
        }
    }

    static void receiveMessage(String message) {
        Log.i("Client_receiveMessage", message);
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "status":
                Integer[] news = {Integer.parseInt(s[1]), Integer.parseInt(s[2]),
                        Integer.parseInt(s[3]), Integer.parseInt(s[4])};
                myInfo.setStatus(Arrays.asList(news));
                break;
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
//                ResultExp.receiveMessage(message);
                break;

            case "gps17":
                TeamSplit.receiveMessage(message);
                break;

            case "gps18":
//                TeamSplitResult.receiveMessage(message);
                break;

            case "otherpos19":
            case "score19":
                TeamResultMap.receiveMessage(message);
                break;

            case "showresult":
//                Game.receiveMesage(message);
                break;
        }
    }

    static LatLng moveLocation(LatLng target, int degree, int value) {
        /*
         * @param degree N=0, E=90, S=180, W=270
         * @param value is meter
         */

        double R = 6378150.0, dist = (double) value,
                dangle = Math.toRadians((double) degree);
        double deltaLat = dist * Math.cos(dangle) * 360.0 / (2.0 * Math.PI * R);
        double newLat = target.latitude + deltaLat;
        double earth_radius_at_longitude = R * Math.cos(newLat * Math.PI / 180.0),
                earth_circle_at_longitude = 2.0 * Math.PI * earth_radius_at_longitude,
                longitude_per_meter = 360.0 / earth_circle_at_longitude;
        double newLng = target.longitude + dist * Math.sin(dangle) * longitude_per_meter;
        Log.d("Client#moveLocation", "(" + target.latitude + "," + target.longitude
                + ") -> (" + newLat + ", " + newLng + ")");
        return new LatLng(newLat, newLng);
    }

    void playSound(String message) {

    }

    private final static double ratio = 1000000;

    static int calcLevel(int exp) {
        int ret = Arrays.binarySearch(expTable, exp);
        if (ret < 0) ret = ~ret + 1;
        return ret;
    }

    static int calcNextExp(int exp) {
        return expTable[calcLevel(exp) - 1] - exp;
    }

    private class myEntry implements Serializable {
        String s=null;
        Integer i=null;
        @ServerTimestamp
        private Date time;

        myEntry(){}

        myEntry(String s, Integer i){
            this.s = s;
            this.i = i;
        }

        String getKey(){
            return s;
        }

        int getValue(){
            return i;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }
    }
}
