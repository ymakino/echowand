package echowand.net;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import java.io.IOException;
import java.net.BindException;
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
    /*
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
                c1 = new TCPConnection(socket);
            } catch (TCPException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
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
                c2 = new TCPConnection(socket);
            } catch (UnknownHostException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TCPConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TCPException ex) {
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
    public void testIsConnected() {
        assertTrue(c1.isConnected());
        assertTrue(c2.isConnected());
    }

    @Test
    public void testIsValid() {
        assertTrue(c1.isValid());
        assertTrue(c2.isValid());
    }

    @Test
    public void testInvalidate() {
        c1.invalidate();
        assertFalse(c1.isValid());
        assertTrue(c2.isValid());
        c2.invalidate();
        assertFalse(c1.isValid());
        assertFalse(c2.isValid());
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

        CommonFrame commonFrame2 = c2.recv(1000);

        assertTrue(Arrays.equals(commonFrame1.toBytes(), commonFrame2.toBytes()));
    }

    @Test
    public void testSendAsync() {
        CommonFrame commonFrame1 = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get);
        StandardPayload p = (StandardPayload) commonFrame1.getEDATA();
        p.addFirstProperty(new Property(EPC.x80));
        p.addFirstProperty(new Property(EPC.x81));
        p.addFirstProperty(new Property(EPC.x82));
        p.addFirstProperty(new Property(EPC.x83));
        p.addFirstProperty(new Property(EPC.x84));
        c1.sendAsync(commonFrame1);

        CommonFrame commonFrame2 = c2.recv(0);

        assertTrue(Arrays.equals(commonFrame1.toBytes(), commonFrame2.toBytes()));
    }

    @Test
    public void testRecv() throws IOException, TCPException {
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

        CommonFrame commonFrame3 = c2.recv(1000);
        CommonFrame commonFrame4 = c2.recv(1000);

        assertTrue(Arrays.equals(commonFrame1.toBytes(), commonFrame3.toBytes()));
        assertTrue(Arrays.equals(commonFrame2.toBytes(), commonFrame4.toBytes()));
    }
    
    @Test
    public void testRecvTimeout() {
        CommonFrame commonFrame = c2.recv(500);
        assertNull(commonFrame);
    }

    @Test
    public void testRecvAsync() throws IOException, TCPException {
        CommonFrame commonFrame0 = c2.recvAsync();
        assertNull(commonFrame0);
        
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

        for (;;) {
            CommonFrame commonFrame3 = c2.recvAsync();
            if (commonFrame3 != null) {
                assertTrue(Arrays.equals(commonFrame1.toBytes(), commonFrame3.toBytes()));
                break;
            }
        }
        
        for (;;) {
            CommonFrame commonFrame4 = c2.recvAsync();
            if (commonFrame4 != null) {
                assertTrue(Arrays.equals(commonFrame2.toBytes(), commonFrame4.toBytes()));
                break;
            }
        }
        
        CommonFrame commonFrame5 = c2.recvAsync();
        assertNull(commonFrame5);
    }
    */
}