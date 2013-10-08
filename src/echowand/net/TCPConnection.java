package echowand.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCPコネクションの管理
 * @author ymakino
 */
public class TCPConnection implements Connection {
    private static final Logger LOGGER = Logger.getLogger(TCPConnection.class.getName());
    private static final String CLASS_NAME = TCPConnection.class.getName();
    
    public static final int INITIAL_BUFFER_SIZE = 128;
    public static final int MAX_BUFFER_SIZE = 4096;
    public static final int DEFAULT_KEEPTIME = 60;
    
    private Socket socket;
    private NodeInfo localNodeInfo;
    private NodeInfo remoteNodeInfo;
    private CommonFrameReceiver receiver;
    private LinkedList<TCPConnectionObserver> observers;
    private boolean inputClosed = false;
    private boolean outputClosed = false;
    
    
    public TCPConnection(Socket socket, NodeInfo localNodeInfo, NodeInfo remoteNodeInfo) throws NetworkException {
        this.socket = socket;
        this.localNodeInfo = localNodeInfo;
        this.remoteNodeInfo = remoteNodeInfo;
        receiver = new CommonFrameReceiver();
        observers = new LinkedList<TCPConnectionObserver>();
    }
    
    public synchronized LinkedList<TCPConnectionObserver> cloneObservers() {
        return new LinkedList<TCPConnectionObserver>(observers);
    }
    
    private void notifySent(CommonFrame commonFrame) {
        LOGGER.entering(CLASS_NAME, "notifySent", commonFrame);
        
        for (TCPConnectionObserver observer : cloneObservers()) {
            observer.notifySent(this, commonFrame);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifySent");
    }
    
    private void notifyReceived(CommonFrame commonFrame) {
        LOGGER.entering(CLASS_NAME, "notifyReceived", commonFrame);
        
        for (TCPConnectionObserver observer : cloneObservers()) {
            observer.notifyReceived(this, commonFrame);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifyReceived");
    }
    
    private void notifyClosed() {
        LOGGER.entering(CLASS_NAME, "notifyClosed");
        
        for (TCPConnectionObserver observer : cloneObservers()) {
            observer.notifyClosed(this);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifyClosed");
    }
    
    public synchronized boolean addObserver(TCPConnectionObserver observer) {
        return observers.add(observer);
    }
    
    public synchronized boolean removeObserver(TCPConnectionObserver observer) {
        return observers.remove(observer);
    }
    
    /**
     * この接続が切断されているかどうかを返す。
     * @return 接続が切断されていればtrue、接続中の場合にはfalse
     */
    public boolean isClosed() {
        return socket.isClosed();
    }
    
    /**
     * この接続を切断する。
     * @throws NetworkException エラーが発生した場合
     */
    public void close() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "close");
        
        try {
            socket.close();
        } catch (IOException ex) {
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "close", exception);
            throw exception;
        } finally {
            notifyClosed();
        }
        
        LOGGER.exiting(CLASS_NAME, "close");
    }
    
    public synchronized boolean isOutputClosed() {
        return outputClosed;
    }
    
    public synchronized boolean isInputClosed() {
        return inputClosed;
    }
    
    /**
     * これ以上フレームの送信を行わないことを示す。
     * これ以上、送信も受信も行わない場合にはcloseを呼び出し接続を切断する。
     * @throws NetworkException 
     */
    public synchronized void closeOutput() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "closeOutput");
        
        outputClosed = true;
        
        if (inputClosed && outputClosed && !isClosed()) {
            close();
        }
        
        LOGGER.exiting(CLASS_NAME, "closeOutput");
    }
    
    
    /**
     * これ以上フレームの受信をしないことを示す。
     * これ以上、送信も受信も行わない場合にはcloseを呼び出し接続を切断する。
     * @throws NetworkException 
     */
    public synchronized void closeInput() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "closeInput");
        
        inputClosed = true;
        
        if (inputClosed && outputClosed && !isClosed()) {
            close();
        }
        
        LOGGER.exiting(CLASS_NAME, "closeInput");
    }

    /**
     * この接続のローカルノード情報を表すNodeInfoを返す。
     * @return ローカルノード情報
     */
    public NodeInfo getLocalNodeInfo() {
        return localNodeInfo;
    }

    /**
     * この接続のリモートノード情報を表すNodeInfoを返す。
     * @return リモートノード情報
     */
    public NodeInfo getRemoteNodeInfo() {
        return remoteNodeInfo;
    }
    
    /**
     * この接続を利用したフレームの送信を行う。
     * @param commonFrame 送信するフレーム
     * @throws NetworkException 送信に失敗した場合
     */
    public void send(CommonFrame commonFrame) throws NetworkException {
        LOGGER.entering(CLASS_NAME, "send", commonFrame);
        
        if (isOutputClosed()) {
            NetworkException exception = new NetworkException("closed output: " + socket);
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
        
        try {
            OutputStream os = socket.getOutputStream();
            os.write(commonFrame.toBytes());
            os.flush();
        } catch (IOException ex) {
            close();
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
            
        notifySent(commonFrame);
        
        LOGGER.exiting(CLASS_NAME, "send");
    }
    
    /**
     * この接続を利用したフレームの受信を行う。
     * @return 受信したフレーム
     * @throws NetworkException 受信に失敗した場合
     */
    public CommonFrame receive() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "receive");
        
        if (isInputClosed()) {
            NetworkException exception = new NetworkException("closed input: " + socket);
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        }
        
        CommonFrame commonFrame = null;
        try {
            commonFrame = receiver.receiveCommonFrame();
        } finally {
            if (commonFrame == null) {
                close();
            }
        }
        
        notifyReceived(commonFrame);
        
        LOGGER.exiting(CLASS_NAME, "receive", commonFrame);
        return commonFrame;
    }
    
    @Override
    public String toString() {
        return String.format("local: %s remote: %s", localNodeInfo, remoteNodeInfo);
    }

    private class CommonFrameReceiver {
        private byte[] buffer;
        private int offset;
        private InputStream is;

        private CommonFrameReceiver() throws NetworkException {
            LOGGER.entering(CLASS_NAME, "CommonFrameReceiver");
        
            resetBuffer();
            
            try {
                is = socket.getInputStream();
            } catch (IOException ex) {
                NetworkException exception = new NetworkException("catched exception", ex);
                LOGGER.throwing(CLASS_NAME, "CommonFrameReceiver", exception);
                throw exception;
            }
            
            LOGGER.exiting(CLASS_NAME, "CommonFrameReceiver");
        }
        
        private int getRemain() {
            return buffer.length - offset;
        }
        
        private void resetBuffer() {
            LOGGER.entering(CLASS_NAME, "resetBuffer");
        
            buffer = new byte[INITIAL_BUFFER_SIZE];
            offset = 0;
            
            LOGGER.exiting(CLASS_NAME, "resetBuffer");
        }
        
        private void expandBuffer() {
            LOGGER.entering(CLASS_NAME, "expandBuffer");
            
            int newSize = buffer.length * 2;
            
            if (newSize > MAX_BUFFER_SIZE) {
                newSize = MAX_BUFFER_SIZE;
            }
            
            if (newSize != buffer.length) {
                byte[] newBuffer = new byte[newSize];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = newBuffer;
            }
            
            LOGGER.exiting(CLASS_NAME, "expandBuffer");
        }
        
        private void consumeBuffer(int length) throws NetworkException {
            LOGGER.entering(CLASS_NAME, "consumeBuffer", length);
            
            if (offset < length) {
                NetworkException exception = new NetworkException("buffer underflowed");
                LOGGER.throwing(CLASS_NAME, "consumeBuffer", exception);
                throw exception;
            }
            
            for (int i=0; length + i < offset; i++) {
                buffer[i] = buffer[length + i];
            }
            offset -= length;
            
            LOGGER.exiting(CLASS_NAME, "consumeBuffer");
        }
        
        private boolean isCorrectStandardPayload(Payload payload) {
            if (payload == null) {
                return false;
            }
            
            if (payload instanceof StandardPayload) {
                StandardPayload standardPayload = (StandardPayload)payload;
                StandardPayloadValidator validator = new StandardPayloadValidator();
                return validator.validate(standardPayload);
            } else {
                return false;
            }
        }

        private CommonFrame parseFrame() throws NetworkException {
            LOGGER.entering(CLASS_NAME, "parseNextFrame");
            CommonFrame commonFrame;
            
            try {
                byte[] currentBuffer = Arrays.copyOf(buffer, offset);
                commonFrame = new CommonFrame(currentBuffer);
                
                if (isCorrectStandardPayload(commonFrame.getEDATA())) {
                    consumeBuffer(commonFrame.toBytes().length);
                } else {
                    commonFrame = null;
                }
            } catch (InvalidDataException ex) {
                commonFrame = null;
            }
            
            LOGGER.entering(CLASS_NAME, "parseNextFrame", commonFrame);
            return commonFrame;
        }
        
        private boolean isValidCommonFrame(CommonFrame commonFrame) {
            return true;
        }

        public synchronized CommonFrame receiveCommonFrame() throws NetworkException {
            LOGGER.entering(CLASS_NAME, "receiveCommonFrame");
            
            CommonFrame commonFrame = null;
            
            if (offset > 0) {
                commonFrame = parseFrame();
            }

            if (commonFrame != null) {
                if (!isValidCommonFrame(commonFrame)) {
                    NetworkException exception = new NetworkException("invalid common frame: " + commonFrame);
                    LOGGER.throwing(CLASS_NAME, "receiveCommonFrame", exception);
                    throw exception;
                }
                
                LOGGER.exiting(CLASS_NAME, "receiveCommonFrame", commonFrame);
                return commonFrame;
            }

            for (;;) {
                int count;

                try {
                    count = is.read(buffer, offset, getRemain());
                } catch (IOException ex) {
                    NetworkException exception = new NetworkException("catched exception", ex);
                    LOGGER.throwing(CLASS_NAME, "receiveCommonFrame", exception);
                    throw exception;
                }

                if (count == -1) {
                    NetworkException exception = new NetworkException("socket closed");
                    LOGGER.throwing(CLASS_NAME, "receiveCommonFrame", exception);
                    throw exception;
                }

                offset += count;

                commonFrame = parseFrame();

                if (commonFrame != null) {
                    if (!isValidCommonFrame(commonFrame)) {
                        NetworkException exception = new NetworkException("invalid common frame: " + commonFrame);
                        LOGGER.throwing(CLASS_NAME, "receiveCommonFrame", exception);
                        throw exception;
                    }

                    LOGGER.exiting(CLASS_NAME, "receiveCommonFrame", commonFrame);
                    return commonFrame;
                }

                if (MAX_BUFFER_SIZE == offset) {
                    close();
                    NetworkException exception = new NetworkException("buffer overflowed");
                    LOGGER.throwing(CLASS_NAME, "receiveCommonFrame", exception);
                    throw exception;
                }

                if (getRemain() == 0) {
                    expandBuffer();
                }
            }
        }
    }
}
