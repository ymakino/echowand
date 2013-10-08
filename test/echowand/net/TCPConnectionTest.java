package echowand.net;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author ymakino
 */
public class TCPConnectionTest {
    
    private short port;
    private TCPConnection c1;
    private TCPConnection c2;
    
    public TCPConnectionTest() {
    }
    
    class Server extends Thread {
        private ServerSocket ss;
        private Socket socket;
        
        public Server() throws SocketException, IOException {
            for (port=2000; port< Short.MAX_VALUE; port++) {
                try {
                    ss = new ServerSocket(port);
                    break;
                } catch (BindException ex) {
                }
            }
            ss.setReuseAddress(true);
        }
        
        public void close() {
            try {
                ss.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        @Override
        public void run() {
            try {
                socket = ss.accept();
                socket.setReuseAddress(true);
                
                InetNodeInfo localNodeInfo = new InetNodeInfo(InetAddress.getByName("127.0.0.1"), port);
                InetNodeInfo remoteNodeInfo = new InetNodeInfo(InetAddress.getByName("127.0.0.1"), 3610);
                
                c1 = new TCPConnection(socket, localNodeInfo, remoteNodeInfo);
            } catch (IOException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NetworkException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    class Client extends Thread {
        private Socket socket;
        
        public void close() {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void run() {
            try {
                socket = new Socket("localhost", port);
                socket.setReuseAddress(true);
                
                InetNodeInfo localNodeInfo = new InetNodeInfo(InetAddress.getByName("127.0.0.1"), 3610);
                InetNodeInfo remoteNodeInfo = new InetNodeInfo(InetAddress.getByName("127.0.0.1"), port);
                
                c2 = new TCPConnection(socket, localNodeInfo, remoteNodeInfo);
            } catch (UnknownHostException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NetworkException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    Server server;
    Client client;

    @Before
    public void setUp() throws IOException {
        server = new Server();
        client = new Client();

        server.start();
        client.start();

        try {
            server.join();
            client.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        server.close();
        client.close();
        
        server = null;
        client = null;
    }

    @Test
    public void testIsClosed() {
        assertFalse(c1.isClosed());
        assertFalse(c2.isClosed());
    }
    
    @Test
    public void testCloseInput() throws NetworkException {
        assertFalse(c1.isClosed());
        assertFalse(c1.isInputClosed());
        
        c1.closeInput();
        
        assertFalse(c1.isClosed());
        assertTrue(c1.isInputClosed());
    }
    
    @Test
    public void testCloseOutput() throws NetworkException {
        assertFalse(c1.isClosed());
        assertFalse(c1.isOutputClosed());
        
        c1.closeOutput();
        
        assertFalse(c1.isClosed());
        assertTrue(c1.isOutputClosed());
    }
    
    @Test
    public void testCloseInputAndOutput() throws NetworkException {
        assertFalse(c1.isClosed());
        assertFalse(c1.isOutputClosed());
        assertFalse(c1.isInputClosed());
        
        c1.closeOutput();
        
        assertFalse(c1.isClosed());
        assertTrue(c1.isOutputClosed());
        assertFalse(c1.isInputClosed());
        
        c1.closeInput();
        
        assertTrue(c1.isClosed());
        assertTrue(c1.isOutputClosed());
        assertTrue(c1.isInputClosed());
        
        assertFalse(c2.isClosed());
        assertFalse(c2.isOutputClosed());
        assertFalse(c2.isInputClosed());
        
        c2.closeInput();
        
        assertFalse(c2.isClosed());
        assertFalse(c2.isOutputClosed());
        assertTrue(c2.isInputClosed());
        
        c2.closeOutput();
        
        assertTrue(c2.isClosed());
        assertTrue(c2.isOutputClosed());
        assertTrue(c2.isInputClosed());
    }

    @Test
    public void testSend() throws Exception {
        CommonFrame commonFrame1 = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get);
        StandardPayload p = (StandardPayload) commonFrame1.getEDATA();
        p.addFirstProperty(new Property(EPC.x80));
        p.addFirstProperty(new Property(EPC.x81));
        p.addFirstProperty(new Property(EPC.x82));
        p.addFirstProperty(new Property(EPC.x83));
        p.addFirstProperty(new Property(EPC.x84));
        c1.send(commonFrame1);

        CommonFrame commonFrame2 = c2.receive();

        assertTrue(Arrays.equals(commonFrame1.toBytes(), commonFrame2.toBytes()));
    }

    @Test
    public void testReceive() throws IOException, NetworkException {
        CommonFrame commonFrame1 = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get);
        StandardPayload p1 = (StandardPayload) commonFrame1.getEDATA();
        p1.addFirstProperty(new Property(EPC.x80));
        p1.addFirstProperty(new Property(EPC.x81));
        p1.addFirstProperty(new Property(EPC.x82));
        p1.addFirstProperty(new Property(EPC.x83));
        p1.addFirstProperty(new Property(EPC.x84));
        commonFrame1.setTID((short)1);
        c1.send(commonFrame1);
        
        CommonFrame commonFrame2 = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.SetC);
        StandardPayload p2 = (StandardPayload) commonFrame2.getEDATA();
        p2.addFirstProperty(new Property(EPC.x80, new Data((byte)0x00)));
        p2.addFirstProperty(new Property(EPC.x81, new Data((byte)0x00, (byte)0x00)));
        p2.addFirstProperty(new Property(EPC.x82, new Data((byte)0x12, (byte)0x34)));
        commonFrame2.setTID((short)2);
        c1.send(commonFrame2);

        CommonFrame commonFrame3 = c2.receive();
        CommonFrame commonFrame4 = c2.receive();

        assertTrue(Arrays.equals(commonFrame1.toBytes(), commonFrame3.toBytes()));
        assertTrue(Arrays.equals(commonFrame2.toBytes(), commonFrame4.toBytes()));
    }
}