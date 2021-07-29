package com.webserva.wings.android.pl2_spread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class Client {
    static int port = 38443, myRank, myScore, rankCounter, maxLevel = 133;
    static long time = 10000;
    private final static String TAG = "Client";

    static MemberInfo myInfo;
    static GoogleMap mMap;
    static FusedLocationProviderClient fusedLocationClient;
    static LatLng start, goal;
    static ListenerRegistration startListener, resultListener, readyListener, roomMemberListener,
            applicationListener, teamNumListener, gpCountListener, roomListener;
    static FirebaseFirestore db;
    static Map memberInRoom;
    static int gCount = -1, pCount = -1;

    static Integer[] expTable = new Integer[maxLevel];
    static PrintWriter out;

    static Context context;

    static void init(String id, boolean isNewRegister) {
        db = FirebaseFirestore.getInstance();
        myInfoRef = db.collection("memberList").document(myInfo.getId());
        final int lv1 = 90000;

        //経験値テーブルの生成
        expTable[0] = 0;
        expTable[1] = 1;
        expTable[2] = lv1;
        for (int i = 3; i < maxLevel; i++) {
            expTable[i] = expTable[i - 1] + (int) ((Math.pow(1.033, (double) i) + 0.1 * (double) i) * lv1);
            if (i < 10) Log.d("Client#init", expTable[i] + "");
        }
        if (isNewRegister) sendMessage("register");
    }

    static void init2(Context c) {
        db = FirebaseFirestore.getInstance();
        context = c;
    }

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

            case "login":
                batch.update(myInfoRef, "state", "offline", "roomId", null, "angle", null, "dist", null, "plusAngle", null, "plusDist", null);
                batch.commit().addOnSuccessListener(Void -> {
                    MainMenu.receiveMessage("success");
                }).addOnFailureListener(e -> {

                });
                break;

            case "newroom":
                myInfo.setRoomId(myInfo.getId());
                roomRef = db.collection("roomList").document(myInfo.getRoomId());
                db.collection("memberList").whereEqualTo("roomId", myInfo.getId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot x : queryDocumentSnapshots.getDocuments()) {
                        x.getReference().update("roomId", null);
                    }
                    db.collection("memberList").document(Client.myInfo.getId()).update(
                            "roomId", myInfo.getId(),
                            "state", "hosting"
                    );
                });
                // Roomを作成しリストに追加
                Room newRoom = new Room(s[1], Integer.parseInt(s[2]), myInfo.getId(), myInfo.getName());
                newRoom.setMessage("newRoom");
                newRoom.setMemberNum(1);
                roomRef.set(newRoom).addOnSuccessListener(Void -> {
                    roomRef.collection("member").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot x : queryDocumentSnapshots.getDocuments()) {
                            String id = x.getId();
                            x.getReference().delete();
                        }
                        WriteBatch wBatch = db.batch();
                        wBatch.set(roomRef.collection("member").document(myInfo.getId()), new SimpleEntry("team", -1));
                        wBatch.commit();
                        applicationListener = roomRef.collection("member").addSnapshotListener((snapshots, e) -> {
                            if (e != null) {
                                Log.w(TAG, "listen:error", e);
                                return;
                            }
                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                // com.google.firebase.database.GenericTypeIndicator
                                String changedId = dc.getDocument().getId();
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d(TAG, "New Member: " + changedId + "/" + dc.getDocument().getData());
                                        db.collection("memberList").document(changedId).get().addOnSuccessListener(documentSnapshot -> {
                                            receiveMessage("add9$" + documentSnapshot.getData().get("name") + "$" + dc.getDocument().getId());
                                        });
                                        break;

                                    case MODIFIED:
                                        if (dc.getDocument().get("value", Integer.class) == -2) {
                                            Log.d(TAG, "Modified Member: " + changedId + "/" + dc.getDocument().getData());
                                            db.collection("memberList").document(changedId).get().addOnSuccessListener(documentSnapshot -> {
                                                receiveMessage("add9$" + documentSnapshot.getData().get("name") + "$" + dc.getDocument().getId());
                                            });
                                            Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        }
                                        break;

                                    case REMOVED:
                                        Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                        receiveMessage("delete9$" + changedId);
                                        break;
                                }
                            }
                        });
                    });
                });
                // ルームリストを表示しているユーザに通知（openで通知されるはず）
                //申し込みのリスナー

                //countの通知をこの時点で追加しておく
                readyListener = roomRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed in counter.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if (snapshot.get("count", Integer.class).equals(snapshot.get("memberNum", Integer.class))) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException interruptedException) {
                            }
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
                                roomMemberListener = db.collection("roomList").document(myInfo.getRoomId()).collection("member")
                                        .whereGreaterThanOrEqualTo("value", -1).addSnapshotListener((value, error) -> {
                                            StringJoiner sj = new StringJoiner("$");
                                            sj.add("add10");
                                            sj.add(value.getDocuments().size() + "");
                                            for (DocumentSnapshot x : value.getDocuments()) {
                                                db.collection("memberList").document(x.getId()).get().addOnSuccessListener(documentSnapshot -> {
                                                    sj.add(documentSnapshot.get("name", String.class));
                                                    sj.add(x.getId());
                                                    Log.i(TAG, (sj.toString().split("\\$").length - 1) / 2 + "");
                                                    if ((sj.toString().split("\\$").length - 1) / 2 == value.getDocuments().size())
                                                        receiveMessage(sj.toString());
                                                });
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
                db.collection("roomList").document(myInfo.getRoomId()).update("memberNum", FieldValue.increment(-1));
                //myInfoを更新
                myInfo.setRoomId(null);
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
                break;

            case "startpos":
                break;

            case "pos":
                LatLng newPos = new LatLng(Double.parseDouble(s[1]), Double.parseDouble(s[2]));
                myInfoRef.update("angle", ResultMap.calcAngle(start, newPos));
                myInfoRef.update("dist", ResultMap.calcDist(start, newPos));
                break;

            case "goalpos":
                //終点を記録、タイマー終了、リスナ追加
                resultListener = roomRef.addSnapshotListener((snapshot, e) -> {
                    //Log.i(TAG, snapshot.toString());
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if (snapshot.get("count", Integer.class)
                                .equals(snapshot.get("memberNum", Integer.class))) {
                            receiveMessage("result");
                            boolean team = myInfo.getTeam() != -1;
                            StringJoiner sj = new StringJoiner("$");
                            if (team) {
                                sj.add("otherpos19");
                                sj.add(String.valueOf(gCount));
                                sj.add(String.valueOf(pCount));
                            } else {
                                sj.add("otherpos12");
                                sj.add(snapshot.get("memberNum").toString());
                            }
                            sj.add(s[1]);
                            db.collection("memberList").whereEqualTo("roomId", Client.myInfo.getRoomId()).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    StringJoiner sj2 = new StringJoiner("$");
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (team) {
                                            if (document.get("team", Integer.class) == 0) {
                                                sj.add(String.valueOf(document.get("angle", Double.class)));
                                                sj.add(String.valueOf(document.get("dist", Double.class)));
                                            } else {
                                                sj2.add(String.valueOf(document.get("angle", Double.class)));
                                                sj2.add(String.valueOf(document.get("dist", Double.class)));
                                            }
                                        } else {
                                            sj.add(String.valueOf(document.get("angle", Double.class)));
                                            sj.add(String.valueOf(document.get("dist", Double.class)));
                                        }
                                    }
                                    Log.i(TAG, sj.toString() + "???" + sj2.toString());
                                    if (team) sj.merge(sj2);
                                    Log.i(TAG, sj.toString());
                                    sj2 = new StringJoiner("$");
                                    if (s[1].equals("1")) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (team) {
                                                if (document.get("team", Integer.class) == 0) {
                                                    sj.add(String.valueOf(document.get("plusAngle", Double.class)));
                                                    sj.add(String.valueOf(document.get("plusDist", Double.class)));
                                                } else {
                                                    sj2.add(String.valueOf(document.get("plusAngle", Double.class)));
                                                    sj2.add(String.valueOf(document.get("plusDist", Double.class)));
                                                }
                                            } else {
                                                sj.add(String.valueOf(document.get("plusAngle", Double.class)));
                                                sj.add(String.valueOf(document.get("plusDist", Double.class)));
                                            }
                                        }
                                        if (team) sj.merge(sj2);
                                    }
                                    receiveMessage(sj.toString());
                                } else {
                                    Log.d(TAG, "Error getting positions: ", task.getException());
                                }
                            });
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
                roomRef.collection("member").document(myInfo.getId()).delete();
                myInfoRef.update("angle", null,
                            "dist", null,
                            "plusAngle", null,
                            "plusDist", null,
                            "roomId", null,
                            "team", null,
                            "status", "offline");
                Client.myInfo.setTeam(-1);
                break;

            case "gp":
                // データを保存
                roomRef.collection("member").document(myInfo.getId())
                        .update("value", Integer.parseInt(s[1]));
                myInfoRef.update("team", Integer.parseInt(s[1]));
                roomRef.update("gpCount", FieldValue.increment(1));
                // もし全員集まったらそれぞれに送る
                gpCountListener = roomRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        int n = snapshot.get("gpCount", Integer.class);
                        if (n >= 1 && n % snapshot.get("memberNum", Integer.class).intValue() == 0) {
                            db.collection("roomList").document(myInfo.getRoomId()).collection("member").get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            StringJoiner sj = new StringJoiner("$");
                                            sj.add("gps17");
                                            sj.add(task.getResult().size() + "");
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                sj.add(document.getId());
                                                sj.add(document.get("value", Integer.class) + "");
                                            }
                                            receiveMessage(sj.toString());
                                            gpCountListener.remove();
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

            case "gpdefined":
                if (myInfo.getId().equals(myInfo.getRoomId())) {
                }
                for (int i = 0; i < Integer.parseInt(s[1]); i++) {
                    if (s[2 * i + 2].equals(myInfo.getId())) {
                        myInfo.setTeam(Integer.parseInt(s[2 * i + 3]));
                    }
                    if (myInfo.getId().equals(myInfo.getRoomId())) {
                        roomRef.collection("member").document(s[2 * i + 2]).update("value", Integer.parseInt(s[2 * i + 3]));
                    }
                }
                break;

            case "move":
                myInfoRef.update("plusAngle", (Integer.parseInt(s[1])) * 90,
                        "plusDist", myInfo.getStatus().get(Integer.parseInt(s[1]) - 1));
                sendMessage("goalpos$1");
                break;

            case "newstatus":
                Integer[] tmp = {Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4])};
                myInfoRef.update("status", Arrays.asList(tmp))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                myInfo.setStatus(Arrays.asList(tmp));

                File file = new File(context.getFilesDir(), "userInfo");
                try (FileWriter writer = new FileWriter(file)) {
                    StringJoiner sj = new StringJoiner("$");
                    //name id exp status
                    sj.add(Client.myInfo.getName());
                    sj.add(Client.myInfo.getId());
                    sj.add(String.valueOf(0));
                    sj.add(String.valueOf(Client.myInfo.getStatus().get(0)));
                    sj.add(String.valueOf(Client.myInfo.getStatus().get(1)));
                    sj.add(String.valueOf(Client.myInfo.getStatus().get(2)));
                    sj.add(String.valueOf(Client.myInfo.getStatus().get(3)));
                    writer.write(sj.toString());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
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
                            sj.add(score.getTeamName());
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
                myInfoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                receiveMessage("numrank$" + document.get("matchHistory", Integer.class));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
                break;

            case "roomreq":
                // 部屋探し中のリストに追加
                myInfo.setState("choosingRoom");
                myInfoRef.update(
                        "state", "choosingRoom"
                );
                Query roomWatcher = db.collection("roomList").whereEqualTo("open", true), //新しいroomの検出
                        roomMemberWatcher = db.collectionGroup("member"); //
                roomListener = roomWatcher.addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Log.i(TAG, "roomReq");
                        Map<String, Object> room = dc.getDocument().getData();
                        String roomName = room.get("roomName").toString(), tag = room.get("tag").toString(),
                                hid = room.get("hostId").toString(), hname = room.get("hostName").toString(),
                                memNum = room.get("memberNum").toString();
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
                                receiveMessage("num$" + hid + "$" + memNum);
                                break;
                            case REMOVED:
                                receiveMessage("del$" + hid);
                                break;
                        }
                    }
                });
                break;

            case "roomdispatch":
                roomListener.remove();
                break;

            case "newscore": //新しいスコアが自分のベストか確認、またホストならランキングに登録、部屋を削除
                if (myInfo.getId().equals(myInfo.getRoomId())) {
                    Score newScore = new Score();
                    int nScore = Integer.parseInt(s[1]);
                    newScore.setScore(nScore);
                    roomRef.get().addOnSuccessListener(documentSnapshot -> {
                        newScore.setTeamName(documentSnapshot.get("roomName", String.class));
                        db.collection("ranking").add(newScore).addOnSuccessListener(documentReference -> {
                            documentReference.update("scoreId", documentReference.getId());
                            roomRef.collection("memberList").get().addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot x : queryDocumentSnapshots) {
                                    db.collection("memberInfo").document(x.getId()).get().addOnSuccessListener(documentSnapshot1 -> {
                                        db.collection("ranking").document(documentSnapshot1.get("recordId", String.class)).get().addOnSuccessListener(documentSnapshot2 -> {
                                            if (documentSnapshot2.get("score", Integer.class) < nScore) {
                                                x.getReference().update("recordId", documentReference.getId());
                                            }
                                        });
                                    });
                                }
                            });
                        });
                        //部屋を破壊
                        roomRef.collection("member").get().addOnSuccessListener(queryDocumentSnapshots -> {
                            WriteBatch wBatch = db.batch();
                            for (QueryDocumentSnapshot x : queryDocumentSnapshots) {
                                wBatch.delete(x.getReference());
                            }
                            wBatch.delete(roomRef);
                            wBatch.commit();
                        });
                    });
                }
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
            case "num":
                RoomList.receiveMessage(message);
                break;

            case "approved":
            case "declined":
                RoomWait.receiveMessage(message);
                break;

            case "add10":
            case "del10":
            case "confirm":
                RoomInfo.receiveMessage(message);
                break;

            case "broken":
                Intent intent = new Intent(context, RoomList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
