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
public class TCPConnectionSample0 {
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
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
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
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
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
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
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
    
    public static void main(String[] args) throws NetworkException {
        
        //LoggerConfig.changeLogLevelAll(TCPConnectionPool.class.getName());
        //LoggerConfig.changeLogLevelAll(TCPConnection.class.getName());
        //LoggerConfig.changeLogLevelAll(TCPNetwork.class.getName());
        //LoggerConfig.changeLogLevelAll(TCPReceiveTask.class.getName());
        
        //3秒後にプログラムが終了するように設定
        // setTimeout(3000);

        try {

            // ECHONET Liteメッセージ送受信に利用するIPのサブネットを作成
            Inet4Subnet subnet = Inet4Subnet.startSubnet();

            //========================= Get =========================
            // メッセージの宛先のNodeを取得
            Node remoteNode = subnet.getRemoteNode(Inet4Address.getByName(peerAddress));
            
            // Connectionの生成
            Connection connection1 = subnet.newTCPConnection(remoteNode);

            // Get共通フレームを作成
            CommonFrame commonFrame1 = createCommonFrameGet();
            
            // 共通フレームを送信
            System.out.println("Sending to " + connection1 + ":  " + commonFrame1);
            connection1.send(commonFrame1);
            
            // 共通フレームを受信
            // 受信するフレームが存在しない場合には、ここでタイムアウトする
            System.out.println("Received from " + connection1 + ": " + connection1.receive());
            System.out.println();
            
            connection1.close();
            
            
            //========================= SetGet =========================
            // メッセージの宛先のNodeを取得
            Connection connection2 = subnet.newTCPConnection(remoteNode);
            
            // SetGet共通フレームを作成
            CommonFrame commonFrame2 = createCommonFrameSetGet();
            
            // 共通フレームを送信
            System.out.println("Sending to " + connection2 + ":  " + commonFrame2);
            connection2.send(commonFrame2);
            
            // 共通フレームを受信
            // 受信するフレームが存在しない場合には、ここでタイムアウトする
            System.out.println("Received from " + connection2 + ": " + connection2.receive());
            System.out.println();
            connection2.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }
}
