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
    static ListenerRegistration startListener, resultListener, readyListener, roomMemberListener,
            applicationListener, teamNumListener, gpListener, roomListener;
    static FirebaseFirestore db;
    static Map memberInRoom;

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

    static void init(Context c, String id, boolean isNewRegister) {
        db = FirebaseFirestore.getInstance();
        myInfoRef = db.collection("memberList").document(myInfo.getId());
        final int lv1 = 90000;
        //myInfo.setRoomId("dummyHostId");
        context = c;

        //経験値テーブルの生成
        expTable[0] = 2;
        expTable[1] = lv1;
        for (int i = 2; i < 100; i++) {
            expTable[i] = expTable[i - 1] + (int) ((Math.pow(1.033, (double) i) + 0.1 * (double) i) * lv1);
            if (i < 10) Log.d("Client#init", expTable[i] + "");
        }
        if (isNewRegister) sendMessage("register");
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

    static DocumentReference roomRef,
            myInfoRef;

    static void sendMessage(String message) {
        Log.i(TAG, "sendMessage:" + message);


        String[] s = message.split("\\$");

        WriteBatch batch = db.batch();

        switch (s[0]) {
            case "register":
                myInfoRef.set(myInfo)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                break;
            case "newroom":
                myInfo.setRoomId(myInfo.getId());
                roomRef = db.collection("roomList").document(myInfo.getRoomId());
                db.collection("memberList").document(Client.myInfo.getId()).update(
                        "roomId", myInfo.getId(),
                        "state", "hosting"
                );
                // Roomを作成しリストに追加
                Room newRoom = new Room(s[1], Integer.parseInt(s[2]), myInfo.getId(), myInfo.getName());
                newRoom.setMessage("newRoom");
                newRoom.setMemberNum(1);
                roomRef.set(newRoom);
                roomRef.collection("member").document(myInfo.getId()).set(new SimpleEntry("team", 0));
                // ルームリストを表示しているユーザに通知（openで通知されるはず）
                //申し込みのリスナー
                applicationListener = roomRef.collection("member").addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        // com.google.firebase.database.GenericTypeIndicator
                        String name = dc.getDocument().getId();
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New Member: " + name + "/" + dc.getDocument().getData());
                                db.collection("memberList").whereEqualTo("state", "applying").get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (!document.getId().equals(myInfo.getId())) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                receiveMessage("add9$" + document.getData().get("name") + "$" + document.getId());
                                            }
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
                readyListener = roomRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed in counter.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if (snapshot.get("count", Integer.class).equals(snapshot.get("memberNum", Integer.class))) {
                            receiveMessage("readyall");
                            readyListener.remove();
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });

                break;

            case "roomdel":
                roomRef.delete();
                roomRef.collection("member").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (document.getId().equals(myInfo.getId())) {
                                roomRef.collection("member").document(myInfo.getId()).delete();
                            } else {
                                document.getReference().update(
                                        "value", -4
                                );
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting", task.getException());
                    }
                });
                break;

            case "apply":
                //MemberInfoのRoomIdを設定
                myInfo.setRoomId(s[1]);
                roomRef = db.collection("roomList").document(myInfo.getRoomId());
                db.collection("memberList").document(myInfo.getId()).update(
                        "roomId", s[1],
                        "state", "applying"
                );
                db.collection("roomList").document(s[1])
                        .collection("member").document(myInfo.getId()).set(
                        new SimpleEntry<>("team", -2)
                ).addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written in apply!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document in apply", e));

                //承認非承認をリッスン
                final DocumentReference docRef = db.collection("roomList").document(myInfo.getRoomId()).collection("member").document(myInfo.getId());
                teamNumListener = docRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        switch (Math.toIntExact((Long) snapshot.getData().get("value"))) {
                            case 0: //承認
                                receiveMessage("confirm");
                                roomMemberListener.remove();
                                teamNumListener.remove();
                                break;

                            case -1: //承認
                                db.collection("roomList").document(myInfo.getRoomId()).collection("member")
                                        .whereEqualTo("value", -1).get().addOnCompleteListener(task -> {
                                    Log.i(TAG, "approved");
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            db.collection("memberList").document(document.getId()).get().addOnCompleteListener(task12 -> {
                                                if (task12.isSuccessful()) {
                                                    DocumentSnapshot document1 = task12.getResult();
                                                    if (document1.exists()) {
                                                        Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                                                        receiveMessage("add10$" + document1.get("name", String.class) + "$" + document.getId());
                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                    }
                                                } else {
                                                    Log.d(TAG, "get failed with ", task12.getException());
                                                }
                                            });
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting -1 members ", task.getException());
                                    }
                                });
                                receiveMessage("approved");
                                break;

                            case -3: //非承認
                                receiveMessage("declined");
                                teamNumListener.remove();
                                break;

                            case -4:
                                db.collection("roomList").document(myInfo.getRoomId())
                                        .collection("member").document(myInfo.getId()).delete();
                                receiveMessage("broken");
                                teamNumListener.remove();
                                break;
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
                break;

            case "accept":
                // memberのvalueを-1にする（＝全員に表示させる）TODO
                batch.set(roomRef.collection("member").document(s[1]), new SimpleEntry<>("team", -1));
                batch.update(roomRef, "memberNum", FieldValue.increment(1));
                batch.update(db.collection("memberList").document(s[1]), "state", "approved");
                batch.commit();
                break;

            case "leave":
                //roomListから削除
                db.collection("roomList").document(myInfo.getRoomId())
                        .collection("member").document(myInfo.getId()).delete();
                //myInfoを更新
                myInfo.setRoomId(null);
                //roomのmemberに通知 TODO
                break;

            case "confirm":
                applicationListener.remove();
                batch = db.batch();
                // 非承認だった人をroomlist画面に戻すために-3を設定
                Query noApproval = db.collection("roomList").document(myInfo.getId())
                        .collection("member").whereEqualTo("value", -2);
                noApproval.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            document.getReference().update(
                                    "value", -3
                            );
                        }
                    } else {
                        Log.d(TAG, "Error getting -2 members ", task.getException());
                    }
                });

                // メンバーに通知
                Query approval = roomRef.collection("member").whereEqualTo("value", -1);
                approval.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            document.getReference().update(
                                    "value", 0
                            );
                        }
                    } else {
                        Log.d(TAG, "Error getting -1 members ", task.getException());
                    }
                });
                // roomをcloseする
                // 下のreadyと連動して、ホスト分カウントしておく
                db.collection("roomList").document(myInfo.getId()).update(
                        "open", false,
                        "count", FieldValue.increment(1),
                        "gpCount", 0
                ).addOnFailureListener(e -> Log.w(TAG, "Error updating \"open\"", e));
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
                //myInfoRef.update("matchHistory", FieldValue.increment(1));
                break;

            case "startpos":
                //始点を記録
//                myInfoRef.update("angle", ResultMap.calcAngle(start, ));
//                myInfoRef.update("dist", start.longitude);
                break;

            case "pos":
                LatLng newPos = new LatLng(Double.parseDouble(s[1]), Double.parseDouble(s[2]));
                myInfoRef.update("angle", ResultMap.calcAngle(start, newPos));
                myInfoRef.update("dist", ResultMap.calcDist(start, newPos));
                break;

            case "goalpos":
                //終点を記録、タイマー終了、リスナ追加
                resultListener = roomRef.addSnapshotListener((snapshot, e) -> {
                    Log.i(TAG, snapshot.toString());
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if (snapshot.get("count", Integer.class)
                                .equals(snapshot.get("memberNum", Integer.class))) {
                            receiveMessage("result");
                            //チーム戦ではない場合
                            StringJoiner sj = new StringJoiner("$");
                            sj.add("otherpos12");
                            sj.add(snapshot.get("memberNum").toString());
                            db.collection("memberList").whereEqualTo("roomId", Client.myInfo.getRoomId()).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        sj.add(String.valueOf(document.get("angle", Double.class)));
                                        sj.add(String.valueOf(document.get("dist", Double.class)));
                                    }
                                    receiveMessage(sj.toString());
                                } else {
                                    Log.d(TAG, "Error getting positions: ", task.getException());
                                }
                            });

//                            roomRef.collection("member").get().addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        sj.add(String.valueOf(document.get("start", LatLng.class).latitude));
//                                        sj.add(String.valueOf(document.get("start", LatLng.class).longitude));
//                                        sj.add(String.valueOf(document.get("goal", LatLng.class).latitude));
//                                        sj.add(String.valueOf(document.get("goal", LatLng.class).longitude));
//                                    }
//                                    receiveMessage(sj.toString());
//                                } else {
//                                    Log.d(TAG, "Error getting positions: ", task.getException());
//                                }
//                            });
                            resultListener.remove();
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
                batch.update(roomRef, "count", FieldValue.increment(1));
                batch.update(myInfoRef, "matchHistory", FieldValue.increment(1));
                batch.commit();
                myInfo.setMatchHistory(myInfo.getMatchHistory() + 1);
                break;

            case "resume":
                // 部屋をopenにする
                roomRef.update("open", true);
                break;

            case "gp":
                // データを保存
                roomRef.collection("member").document(myInfo.getId())
                        .update("value", Integer.parseInt(s[1]));
                roomRef.update("gpCount", FieldValue.increment(1));
                // もし全員集まったらそれぞれに送る
                gpListener = roomRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if (snapshot.get("gpCount", Integer.class).equals(snapshot.get("memberNum", Integer.class))) {
                            db.collection("roomList").document(myInfo.getRoomId()).collection("member").get()
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
                goal = moveLocation(goal, (Integer.parseInt(s[1]) - 1) * 90, myInfo.getStatus().get(Integer.parseInt(s[1]) - 1));
                sendMessage("goalpos");
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
                myInfoRef.update("status", Arrays.asList(tmp))
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
                receiveMessage("num$" + Client.myInfo.getMatchHistory());
                break;

            case "roomreq":
                // 部屋探し中のリストに追加
                myInfo.setState("choosingRoom");
                myInfoRef.update(
                        "state", "choosingRoom"
                );
                Query roomWatcher = db.collection("roomList").whereEqualTo("open", true),
                        roomMemberWatcher = db.collectionGroup("member");
                roomListener = roomWatcher.addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Log.i(TAG, "roomReq");
                        Map<String, Object> room = dc.getDocument().getData();
                        String roomName = room.get("roomName").toString(), tag = room.get("tag").toString(),
                                hid = room.get("hostId").toString(), hname = room.get("hostName").toString();
                        StringJoiner sj2;
                        List<String> sList;
                        switch (dc.getType()) {
                            case ADDED:
                                sList = Arrays.asList(roomName, tag, hid, hname, "1");
                                sj2 = new StringJoiner("$");
                                sj2.add("add4");
                                sList.forEach(sj2::add);
                                receiveMessage(sj2.toString());
                                break;
                            case MODIFIED: //
                                sList = Arrays.asList(roomName, tag, hid, hname, "1");
                                sj2 = new StringJoiner("$");
                                sj2.add("add4");
                                sList.forEach(sj2::add);
                                receiveMessage("del$" + hid);
                                receiveMessage(sj2.toString());
                                break;
                            case REMOVED:
                                receiveMessage("del$" + hid);
                                break;
                        }
                    }
                });

                roomMemberListener = roomMemberWatcher.addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        memberInRoom = dc.getDocument().getData();
                        // String roomName = dc.getDocument().getReference().getParent().getId(); //memberが返ってきた
                        String changedUserName = dc.getDocument().getReference().getId();
                        db.collection("memberList").document(changedUserName).addSnapshotListener((snapshots1, e1) -> {
                            Log.d(TAG, "MemberChange in Room " + changedUserName);
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "MemberChange Added Info:" + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    receiveMessage("num$" + snapshots1.get("roomId", String.class) + "$" + memberInRoom.size());
                                    Log.d(TAG, "MemberChange Modified Info:" + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "MemberChange Removed Info:" + dc.getDocument().getData());
                                    break;
                            }
                        });
                    }
                });
                break;

            case "roomdispatch":
                roomListener.remove();
                break;

            case "newscore": //新しいスコアが自分のベストか確認、またホストならランキングに登録
                if (myInfo.getId().equals(myInfo.getRoomId())) {
                    db.collection("ranking").orderBy("scoreid", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                int num = document.toObject(Score.class).getScoreId() + 1;
                                Score newScore = new Score();
                                newScore.setScore(Integer.parseInt(s[1]));
                                newScore.setScoreId(num);
                                db.collection("roomList").document(myInfo.getRoomId()).get().addOnCompleteListener(task1 -> {
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
                            if (document.toObject(Score.class).getScore() < Integer.parseInt(s[1])) {

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
            case "numrank":
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
            case "num":
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
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                HReady.receiveMessage(message);
                break;

            case "otherpos12":
            case "score12":
                ResultMap.receiveMessage(message);
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

            case "result":
                Game.receiveMessage(message);
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
        if (expTable[expTable.length - 1] <= exp) return expTable.length;
        int ret = Arrays.binarySearch(expTable, exp);
        if (ret < 0) ret = ~ret + 1;
        return ret;
    }

    static int calcNextExp(int exp) {
        if (expTable[expTable.length - 1] <= exp) return 0;
        return expTable[calcLevel(exp) - 1] - exp;
    }

    private static class myEntry implements Serializable {
        String s = null;
        Integer i = null;
        @ServerTimestamp
        private Date time;

        myEntry() {
        }

        myEntry(String s, Integer i) {
            this.s = s;
            this.i = i;
        }

        String getKey() {
            return s;
        }

        int getValue() {
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
