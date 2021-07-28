package com.webserva.wings.android.pl2_spread;

import junit.framework.TestCase;

import static java.lang.String.valueOf;

public class ClientTest extends TestCase {

    Clienttester test = new Clienttester();



    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testInit() {
        test.init();
        //経験値テーブルのテスト
        int exp=90000;
        int lv1 =90000;
        assertEquals("0",test.expTable[0].toString());
        assertEquals("1",test.expTable[1].toString());
        assertEquals(valueOf(exp),test.expTable[2].toString());
        for (int i = 3; i < 100; i++) {
            exp=exp+ (int) ((Math.pow(1.033, (double) i) + 0.1 * (double) i) * lv1);
            assertEquals(valueOf(exp),test.expTable[i].toString());
        }
    }
    /*
    テストのために、以下のC言語プログラムを作成
    #include <stdio.h>
#include <math.h>
int main(void){

    int exp=90000;
    int lv1 =90000;
    int playerExp=118360000;
    int level[110];
    int i=0;
    level[0]=0;
    level[1]=1;
    level[2]=lv1;

    for (int i = 0; i < 110; i++) {
            level[i]=level[i-1]+ (int) ((pow(1.033, (double) i) + 0.1 * (double) i) * lv1);
             if(playerExp<level[i]){
                printf("プレイヤレベルは %d です\n 次のレベルまで　%d",i+1,level[i]-playerExp);
                break;
            }

    }
}
     */
    public void testCalcLevel() {
        //test.calcLevel(844870);
        test.init();
        assertEquals(4,test.calcLevel(118360));
        assertEquals(10,test.calcLevel(1183600));
        assertEquals(37,test.calcLevel(11836000));
        assertEquals(103,test.calcLevel(118360000));
    }

    public void testCalcNextExp() {
        test.init();
        assertEquals(97847,test.calcNextExp(118360));
        assertEquals(51507,test.calcNextExp(1183600));
        assertEquals(281220,test.calcNextExp(11836000));
        assertEquals(3252705,test.calcNextExp(118360000));
    }
    public void testInit_connection() {

    }

    public void testFinishActivity() {
    }

    public void testStartActivity() {
    }


    public void testSendMessage() {
        //ルームリスト送信リクエスト
        test.init();


        String [] sMes = test.sendMessage("register");
        assertEquals("register",sMes[0]);


        test.sendMessage("newroom$TestRoom$4");
        //ホストのIDと部屋のIDの一致を確認
        assertEquals(test.myInfo.getRoomId(),test.myInfo.getId());
        //メッセージやり取りの確認
        assertEquals("newRoom",test.newRoom.getMessage());
        //メンバーの人数の初期値を入れる
        assertEquals(1,test.newRoom.getMemberNum());

        test.sendMessage("apply$TestRoom");{
            assertEquals("TestRoom",test.myInfo.getRoomId());
        }

        test.sendMessage("leave");{
            assertEquals(null,test.myInfo.getRoomId());
        }

/*
        sMes = test.sendMessage("roomreq");
        assertEquals("roomreq",sMes[0]);

        //メンバーの承認
        sMes = test.receiveMessage("accept$myID");
        assertEquals("accept",sMes[0]);
        assertEquals("myID",sMes[1]);

        //スコアの送信要求
        sMes = test.sendMessage("rankreq");
        assertEquals("rankreq",sMes[0]);

        //ルーム作成申請(チーム名とタグ)
        sMes = test.receiveMessage("newroom$myRoom$4");
        assertEquals("newroom",sMes[0]);
        assertEquals("myRoom",sMes[1]);
        assertEquals("4",sMes[2]);

        //ルームへの参加申請
        sMes = test.receiveMessage("apply$my1D");
        assertEquals("apply",sMes[0]);
        assertEquals("my1D",sMes[1]);

        //退出情報
        sMes = test.sendMessage("leave");
        assertEquals("leave",sMes[0]);

        //メンバーの確定
        sMes = test.receiveMessage("confirm$4$id1$1$id2$1$id3$0$id4$1");
        assertEquals("confirm",sMes[0]);
        assertEquals("4",sMes[1]);
        assertEquals("id1",sMes[2]);
        assertEquals("1",sMes[3]);
        assertEquals("id2",sMes[4]);
        assertEquals("1",sMes[5]);
        assertEquals("id3",sMes[6]);
        assertEquals("0",sMes[7]);
        assertEquals("id4",sMes[8]);
        assertEquals("1",sMes[9]);

        //継続確認
        sMes = test.sendMessage("resume$0");
        assertEquals("resume",sMes[0]);
        assertEquals("0",sMes[1]);

        //グーパー
        sMes = test.receiveMessage("gp$1");
        assertEquals("gp",sMes[0]);
        assertEquals("1",sMes[1]);


        //メンバーの一人を承認
        sMes = test.sendMessage("accept$myID");
        assertEquals("accept",sMes[0]);
        assertEquals("myID",sMes[1]);
*/
    }

    public void testReceiveMessage() {
        //部屋の人数変化に関するメッセージ受信
        String [] rMes = test.receiveMessage("num$myID$3");
        assertEquals("num",rMes[0]);
        assertEquals("myID",rMes[1]);
        assertEquals("3",rMes[2]);



        //ルームリスト追加
        rMes = test.receiveMessage("add$myRoom$4$my1D$myName$3");
        assertEquals("add",rMes[0]);
        assertEquals("myRoom",rMes[1]);
        assertEquals("4",rMes[2]);
        assertEquals("my1D",rMes[3]);
        assertEquals("myName",rMes[4]);
        assertEquals("3",rMes[5]);

        //ランキング取得
        rMes = test.receiveMessage("rank$3$18500$12000$4300");
        assertEquals("rank",rMes[0]);
        assertEquals("3",rMes[1]);
        assertEquals("18500",rMes[2]);
        assertEquals("12000",rMes[3]);
        assertEquals("4300",rMes[4]);

        //マイベストランキング取得
        rMes = test.receiveMessage("best$125$12000");
        assertEquals("best",rMes[0]);
        assertEquals("125",rMes[1]);
        assertEquals("12000",rMes[2]);

        //プレイ回数取得
        rMes = test.receiveMessage("num$15");
        assertEquals("num",rMes[0]);
        assertEquals("15",rMes[1]);

        //ルームの削除
        rMes = test.receiveMessage("del$my1D");
        assertEquals("del",rMes[0]);
        assertEquals("my1D",rMes[1]);

        //承認
        rMes = test.receiveMessage("approved");
        assertEquals("approved",rMes[0]);

        //否認
        rMes = test.receiveMessage("declined");
        assertEquals("declined",rMes[0]);

        //メンバーの追加
        rMes = test.receiveMessage("add$Taro$ta6ou");
        assertEquals("add",rMes[0]);
        assertEquals("Taro",rMes[1]);
        assertEquals("ta6ou",rMes[2]);

        //メンバーの削除
        rMes = test.receiveMessage("delete$ta6ou");
        assertEquals("delete",rMes[0]);
        assertEquals("ta6ou",rMes[1]);

        //メンバーの追加10
        rMes = test.receiveMessage("add10$Taro$ta6ou");
        assertEquals("add10",rMes[0]);
        assertEquals("Taro",rMes[1]);
        assertEquals("ta6ou",rMes[2]);

        //メンバーの削除10
        rMes = test.receiveMessage("delete10$ta6ou");
        assertEquals("delete10",rMes[0]);
        assertEquals("ta6ou",rMes[1]);

        //メンバーの追加9
        rMes = test.receiveMessage("add9$Taro$ta6ou");
        assertEquals("add9",rMes[0]);
        assertEquals("Taro",rMes[1]);
        assertEquals("ta6ou",rMes[2]);

        //メンバーの削除
        rMes = test.receiveMessage("delete9$ta6ou");
        assertEquals("delete9",rMes[0]);
        assertEquals("ta6ou",rMes[1]);



        //ルームの消失
        rMes = test.receiveMessage("broken");
        assertEquals("broken",rMes[0]);

        //メンバー確定
        rMes = test.receiveMessage("confirm");
        assertEquals("confirm",rMes[0]);

        //ゲーム開始
        //rMes = test.receiveMessage("start");
        //assertEquals("start",rMes[0]);

        //全員準備完了
        rMes = test.receiveMessage("readyall");
        assertEquals("readyall",rMes[0]);

       //他のプレイヤの位置
        rMes = test.receiveMessage("otherpos$3$137.22$37.43$137.24$37.41");
        assertEquals("otherpos",rMes[0]);
        assertEquals("3",rMes[1]);
        assertEquals("137.22",rMes[2]);
        assertEquals("37.43",rMes[3]);
        assertEquals("137.24",rMes[4]);
        assertEquals("37.41",rMes[5]);

        //スコア
        rMes = test.receiveMessage("score$140000");
        assertEquals("score",rMes[0]);
        assertEquals("140000",rMes[1]);


        //他のユーザーの出し手
        rMes = test.receiveMessage("gps$4$user1$0$user2$1$user3$1$user4$0");
        assertEquals("gps",rMes[0]);
        assertEquals("4",rMes[1]);
        assertEquals("user1",rMes[2]);
        assertEquals("0",rMes[3]);
        assertEquals("user2",rMes[4]);
        assertEquals("1",rMes[5]);
        assertEquals("user3",rMes[6]);
        assertEquals("1",rMes[7]);
        assertEquals("user4",rMes[8]);
        assertEquals("0",rMes[9]);

        //対戦スコア
        rMes = test.receiveMessage("score$140000$130000");
        assertEquals("score",rMes[0]);
        assertEquals("140000",rMes[1]);
        assertEquals("130000",rMes[2]);

    }


}