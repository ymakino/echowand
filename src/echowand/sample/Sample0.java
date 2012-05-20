package echowand.sample;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Yoshiki Makino
 */
public class Sample0 {
    public static String peerAddress = "192.168.1.1";
    
    public static CommonFrame createCommonFrame1() {
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
    
    public static CommonFrame createCommonFrame2() {
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
    
    public static CommonFrame createCommonFrame3() {
        CommonFrame commonFrame = new CommonFrame(new EOJ("013001"), new EOJ("0ef001"), ESV.INFC);
        commonFrame.setTID((short)3);
        StandardPayload payload = (StandardPayload) commonFrame.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x31)));
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x41)));
        // payload.addFirstProperty(new Property(EPC.x88));
        return commonFrame;
    }
    
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
        setTimeout(3000);
        
        InetSubnet subnet = new InetSubnet();
        
        try {
            Node remoteNode1 = subnet.getRemoteNode(InetAddress.getByName(peerAddress), 3610);
            Frame frame1 = new Frame(subnet.getLocalNode(), remoteNode1, createCommonFrame1());
            System.out.println("Sending:  " + frame1);
            subnet.send(frame1);
            System.out.println("Received: " + subnet.recv());
            System.out.println();
            
            Node remoteNode2 = subnet.getRemoteNode(InetAddress.getByName(peerAddress), 3610);
            Frame frame2 = new Frame(subnet.getLocalNode(), remoteNode2, createCommonFrame2());
            System.out.println("Sending:  " + frame2);
            subnet.send(frame2);
            System.out.println("Received: " + subnet.recv());
            System.out.println();
            
            Node remoteNode3 = subnet.getRemoteNode(InetAddress.getByName(peerAddress), 3610);
            Frame frame3 = new Frame(subnet.getLocalNode(), remoteNode3, createCommonFrame3());
            System.out.println("Sending:  " + frame3);
            subnet.send(frame3);
            System.out.println("Received: " + subnet.recv());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }
}
