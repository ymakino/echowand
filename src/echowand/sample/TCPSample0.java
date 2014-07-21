package echowand.sample;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.*;
import echowand.util.LoggerConfig;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * フレームの送受信を行うサンプルプログラム
 * @author Yoshiki Makino
 */
public class TCPSample0 {
    /*
     * リモートノードのIPアドレス
     */
    public static final String peerAddress = "192.168.1.1";
    
    /*
     * ローカルのエアコンオブジェクトからリモートのノードプロファイルオブジェクトへ宛てた
     * Getメッセージの共通フレームを作成する。
     */
    public static CommonFrame createCommonFrameGet() {
        CommonFrame commonFrame = new CommonFrame(new EOJ("013001"), new EOJ("0ef001"), ESV.Get);
        commonFrame.setTID((short)1);
        StandardPayload payload = (StandardPayload) commonFrame.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80));
        payload.addFirstProperty(new Property(EPC.x88));
        payload.addFirstProperty(new Property(EPC.x9F));
        payload.addFirstProperty(new Property(EPC.x9E));
        payload.addFirstProperty(new Property(EPC.x9D));
        payload.addFirstProperty(new Property(EPC.xD5));
        payload.addFirstProperty(new Property(EPC.xD6));
        payload.addFirstProperty(new Property(EPC.xD7));
        return commonFrame;
    }
    
    /*
     * ローカルのエアコンオブジェクトからリモートのエアコンオブジェクトへ宛てた
     * SetGetメッセージの共通フレームを作成する。
     */
    public static CommonFrame createCommonFrameSetGet() {
        CommonFrame commonFrame = new CommonFrame(new EOJ("013001"), new EOJ("013001"), ESV.SetGet);
        commonFrame.setTID((short)2);
        StandardPayload payload = (StandardPayload) commonFrame.getEDATA();
        payload.addFirstProperty(new Property(EPC.xB0, new Data((byte)0x12)));
        payload.addSecondProperty(new Property(EPC.x80));
        payload.addSecondProperty(new Property(EPC.x9F));
        payload.addSecondProperty(new Property(EPC.x9E));
        payload.addSecondProperty(new Property(EPC.x9D));
        return commonFrame;
    }

    /*
     * ローカルのエアコンオブジェクトからリモートのノードプロファイルオブジェクトへ宛てた
     * 応答有りプロパティ通知の共通フレームを作成する。
     */
    public static CommonFrame createCommonFrameINFC() {
        CommonFrame commonFrame = new CommonFrame(new EOJ("013001"), new EOJ("0ef001"), ESV.INFC);
        commonFrame.setTID((short)3);
        StandardPayload payload = (StandardPayload) commonFrame.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x31)));
        return commonFrame;
    }

    /*
     * timeoutミリ秒後にプログラムが終了するようにスレッドを作成し実行する。
     */
    public static void setTimeout(final int timeout) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.exit(1);
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }
    
    public static void main(String[] args) {
        LoggerConfig.changeLogLevelAll(TCPConnection.class.getName());
        
        //3秒後にプログラムが終了するように設定
        // setTimeout(3000);

        try {

            // ECHONET Liteメッセージ送受信に利用するIPのサブネットを作成
            Inet4Subnet subnet = Inet4Subnet.startSubnet();

            //========================= Get =========================
            // メッセージの宛先のNodeを取得
            Node remoteNode1 = subnet.getRemoteNode(Inet4Address.getByName(peerAddress));

            // Getフレームを作成
            TCPConnection connection1 = subnet.newTCPConnection(remoteNode1);
            Frame frame1 = new Frame(subnet.getLocalNode(), remoteNode1, createCommonFrameGet(), connection1);
            subnet.registerTCPConnection(connection1);
            
            // フレームを送信
            System.out.println("Sending:  " + frame1);
            subnet.send(frame1);
            
            // フレームを受信
            // 受信するフレームが存在しない場合には、ここでタイムアウトする
            System.out.println("Received: " + subnet.receive());
            System.out.println();
            subnet.unregisterTCPConnection(connection1);
            
            
            //========================= SetGet =========================
            // メッセージの宛先のNodeを取得
            Node remoteNode2 = subnet.getRemoteNode(Inet4Address.getByName(peerAddress));
            
            // SetGetフレームを作成
            TCPConnection connection2 = subnet.newTCPConnection(remoteNode2);
            Frame frame2 = new Frame(subnet.getLocalNode(), remoteNode2, createCommonFrameSetGet(), connection2);
            subnet.registerTCPConnection(connection2);
            
            // フレームを送信
            System.out.println("Sending:  " + frame2);
            subnet.send(frame2);
            
            // フレームを受信
            // 受信するフレームが存在しない場合には、ここでタイムアウトする
            Frame rframe2 = subnet.receive();
            System.out.println("Received: " + rframe2);
            System.out.println();
            subnet.unregisterTCPConnection(connection2);
            
            
            //========================= INFC =========================
            // INFCフレームを作成、宛先はグループにする。
            Frame frame3 = new Frame(subnet.getLocalNode(), subnet.getGroupNode(), createCommonFrameINFC());
            
            // フレームを送信
            System.out.println("Sending:  " + frame3);
            subnet.send(frame3);
            
            for (;;) {
                // フレームを受信
                System.out.println("Received: " + subnet.receive());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }
}
