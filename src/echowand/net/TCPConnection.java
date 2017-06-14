package echowand.net;

import echowand.common.EPC;
import echowand.common.ESV;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

    private Socket socket;
    private NodeInfo localNodeInfo;
    private NodeInfo remoteNodeInfo;
    private CommonFrameReceiver receiver;
    private LinkedList<TCPConnectionObserver> observers;
    private boolean inputClosed = false;
    private boolean outputClosed = false;

    /**
     * 新たにTCPコネクションを生成する。
     * @param localNodeInfo ローカルノードの情報
     * @param remoteNodeInfo 接続先となるリモートノードの情報
     * @param portNumber 接続先のポート番号
     * @throws NetworkException 接続に失敗した場合
     */
    public TCPConnection(NodeInfo localNodeInfo, NodeInfo remoteNodeInfo, int portNumber) throws NetworkException {
        this(localNodeInfo, remoteNodeInfo, portNumber, 0);
    }

    /**
     * 新たにTCPコネクションを生成する。
     * 接続の確立にtimeoutミリ秒以上かかった場合には、例外を発生させる。
     * @param localNodeInfo ローカルノードの情報
     * @param remoteNodeInfo 接続先となるリモートノードの情報
     * @param portNumber 接続先のポート番号
     * @param timeout タイムアウト時間の設定
     * @throws NetworkException 接続に失敗した場合
     */
    public TCPConnection(NodeInfo localNodeInfo, NodeInfo remoteNodeInfo, int portNumber, int timeout) throws NetworkException {
        if (!(localNodeInfo instanceof InetNodeInfo)) {
            throw new NetworkException("invalid node: " + localNodeInfo);
        }

        if (!(remoteNodeInfo instanceof InetNodeInfo)) {
            throw new NetworkException("invalid node: " + remoteNodeInfo);
        }

        InetNodeInfo remoteInetNodeInfo = (InetNodeInfo) remoteNodeInfo;
        InetAddress remoteAddress = remoteInetNodeInfo.getAddress();
        
        if (remoteInetNodeInfo.hasPortNumber()) {
            portNumber = remoteInetNodeInfo.getPortNumber();
        }
        
        InetSocketAddress remoteSocketAddress = new InetSocketAddress(remoteAddress, portNumber);

        socket = new Socket();

        try {
            if (timeout > 0) {
                socket.connect(remoteSocketAddress, timeout);
            } else {
                socket.connect(remoteSocketAddress);
            }
        } catch (IOException ex) {
            throw new NetworkException("I/O error", ex);
        }

        this.localNodeInfo = localNodeInfo;
        this.remoteNodeInfo = remoteNodeInfo;
        
        try {
            receiver = new CommonFrameReceiver();
        } catch (IOException ex) {
            throw new NetworkException("I/O error", ex);
        }
        
        observers = new LinkedList<TCPConnectionObserver>();
    }

    /**
     * 接続済みのsocketを利用し、新たにTCPコネクションを生成する。
     * @param socket 利用するSocketの指定
     * @param localNodeInfo ローカルノードの情報
     * @param remoteNodeInfo リモートノードの情報
     * @throws NetworkException Socketからの受信ができない場合
     */
    public TCPConnection(Socket socket, NodeInfo localNodeInfo, NodeInfo remoteNodeInfo) throws NetworkException {
        this.socket = socket;
        this.localNodeInfo = localNodeInfo;
        this.remoteNodeInfo = remoteNodeInfo;
        
        try {
            receiver = new CommonFrameReceiver();
        } catch (IOException ex) {
            throw new NetworkException("I/O error", ex);
        }
        
        observers = new LinkedList<TCPConnectionObserver>();
    }

    private synchronized LinkedList<TCPConnectionObserver> cloneObservers() {
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

    /**
     * オブザーバを追加する。
     * @param observer 追加するオブザーバの指定
     * @return 追加に成功した場合にはtrue、そうでなければfalse
     */
    public synchronized boolean addObserver(TCPConnectionObserver observer) {
        return observers.add(observer);
    }

    /**
     * オブザーバを削除する。
     * @param observer 削除するオブザーバの指定
     * @return 削除に成功した場合にはtrue、そうでなければfalse
     */
    public synchronized boolean removeObserver(TCPConnectionObserver observer) {
        return observers.remove(observer);
    }

    /**
     * この接続が切断されているかどうかを返す。
     *
     * @return 接続が切断されていればtrue、接続中の場合にはfalse
     */
    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    /**
     * この接続を切断する。
     *
     * @throws NetworkException エラーが発生した場合
     */
    @Override
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

    /**
     * これ以上フレームの送信を行わないかどうかを返す。
     * @return これ以上フレームの送信を行わない場合にはtrue、そうでなければfalse
     */
    public synchronized boolean isOutputClosed() {
        return outputClosed;
    }


    /**
     * これ以上フレームの受信を行わないかどうかを返す。
     * @return これ以上フレームの受信を行わない場合にはtrue、そうでなければfalse
     */
    public synchronized boolean isInputClosed() {
        return inputClosed;
    }

    /**
     * これ以上フレームの送信を行わないことを示す。 これ以上、送信も受信も行わない場合にはcloseを呼び出し接続を切断する。
     *
     * @throws NetworkException close呼び出しに失敗した場合
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
     * これ以上フレームの受信をしないことを示す。 これ以上、送信も受信も行わない場合にはcloseを呼び出し接続を切断する。
     *
     * @throws NetworkException close呼び出しに失敗した場合
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
     *
     * @return ローカルノード情報
     */
    @Override
    public NodeInfo getLocalNodeInfo() {
        return localNodeInfo;
    }

    /**
     * この接続のリモートノード情報を表すNodeInfoを返す。
     *
     * @return リモートノード情報
     */
    @Override
    public NodeInfo getRemoteNodeInfo() {
        return remoteNodeInfo;
    }

    /**
     * この接続を利用したフレームの送信を行う。
     *
     * @param commonFrame 送信するフレーム
     * @throws NetworkException 送信に失敗した場合
     */
    @Override
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
            NetworkException exception = new NetworkException("I/O error", ex);
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }

        notifySent(commonFrame);

        LOGGER.exiting(CLASS_NAME, "send");
    }

    /**
     * この接続を利用したフレームの受信を行う。
     *
     * @return 受信したフレーム
     * @throws NetworkException 受信に失敗した場合
     * @throws IOException IOエラーが発生した場合
     */
    @Override
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
        } catch (IOException ex) {
            NetworkException exception = new NetworkException("I/O error", ex);
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        } catch (InvalidDataException ex) {
            NetworkException exception = new NetworkException("invalid frame", ex);
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        } finally {
            if (commonFrame == null) {
                closeInput();
            }
        }

        if (commonFrame != null) {
            notifyReceived(commonFrame);
        }

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

        private CommonFrameReceiver() throws NetworkException, IOException {
            LOGGER.entering(CLASS_NAME, "CommonFrameReceiver");

            resetBuffer();
            
            is = socket.getInputStream();

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

            for (int i = 0; length + i < offset; i++) {
                buffer[i] = buffer[length + i];
            }
            offset -= length;

            LOGGER.exiting(CLASS_NAME, "consumeBuffer");
        }
        
        private boolean hasStandardPayloadHeader(CommonFrame commonFrame) {
            return commonFrame.isEchonetLite() && commonFrame.isStandardPayload();
        }
        
        private boolean hasValidESV(Payload payload) {
            byte[] bytes = payload.toBytes();
            
            if (bytes.length < 7) {
                return false;
            }
            
            return !ESV.fromByte(bytes[6]).isInvalid();
        }
        
        private boolean isSetGet(ESV esv) {
            return esv == ESV.SetGet && esv == ESV.SetGet_Res && esv == ESV.SetGet_SNA;
        }
        
        private int hasValidEPCs(byte[] bytes, int payloadOffset) {
            int opc = 0xff & bytes[payloadOffset++];
            
            for (int i=0; i<opc; i++) {
                EPC epc = EPC.fromByte(bytes[payloadOffset++]);
                if (epc.isInvalid()) {
                    return -1;
                }
                int pdc = 0xff & bytes[payloadOffset++];
                payloadOffset += pdc;
            }
            
            return payloadOffset;
        }
        
        private boolean hasValidEPCsEach(Payload payload) {
            byte[] bytes = payload.toBytes();

            try {
                int payloadOffset = 6;
                ESV esv = ESV.fromByte(bytes[++payloadOffset]);

                payloadOffset = hasValidEPCs(bytes, payloadOffset);
                if (payloadOffset == -1) {
                    return false;
                }

                if (isSetGet(esv)) {
                    payloadOffset = hasValidEPCs(bytes, payloadOffset);
                    if (payloadOffset == -1) {
                        return false;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                LOGGER.logp(Level.FINE, CLASS_NAME, "hasValidEPCsEach", "incompleted yet: " + payload);
            }

            return true;
        }

        private boolean isValidStandardPayload(Payload payload) {
            if (payload == null) {
                return false;
            }

            if (payload instanceof StandardPayload) {
                StandardPayload standardPayload = (StandardPayload) payload;
                StandardPayloadValidator validator = new StandardPayloadValidator();
                return validator.validate(standardPayload);
            } else {
                return false;
            }
        }

        private CommonFrame parseFrame() throws InvalidDataException, NetworkException {
            LOGGER.entering(CLASS_NAME, "parseNextFrame");

            CommonFrame commonFrame;

            try {
                byte[] currentBuffer = Arrays.copyOf(buffer, offset);
                commonFrame = new CommonFrame(currentBuffer);
                
                if (!hasStandardPayloadHeader(commonFrame)) {
                    InvalidDataException exception = new InvalidDataException("invalid header: " + commonFrame);
                    LOGGER.throwing(CLASS_NAME, "consumeBuffer", exception);
                    throw exception;
                }
                
                if (!hasValidESV(commonFrame.getEDATA())) {
                    InvalidDataException exception = new InvalidDataException("invalid esv: " + commonFrame);
                    LOGGER.throwing(CLASS_NAME, "consumeBuffer", exception);
                    throw exception;
                }
                
                if (!hasValidEPCsEach(commonFrame.getEDATA())) {
                    InvalidDataException exception = new InvalidDataException("invalid epcs: " + commonFrame);
                    LOGGER.throwing(CLASS_NAME, "consumeBuffer", exception);
                    throw exception;
                }
                
                if (isValidStandardPayload(commonFrame.getEDATA())) {
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

        private int readBytes() throws IOException {
            LOGGER.entering(CLASS_NAME, "readBytes");

            int count = is.read(buffer, offset, getRemain());

            if (count != -1) {
                offset += count;
            }

            LOGGER.exiting(CLASS_NAME, "readBytes", count);
            return count;
        }

        private void validateBufferSize() throws NetworkException {
            LOGGER.entering(CLASS_NAME, "validateBufferSize");

            if (MAX_BUFFER_SIZE == offset) {
                close();
                NetworkException exception = new NetworkException("buffer overflowed");
                LOGGER.throwing(CLASS_NAME, "receiveCommonFrame", exception);
                throw exception;
            }

            if (getRemain() == 0) {
                expandBuffer();
            }

            LOGGER.exiting(CLASS_NAME, "validateBufferSize");
        }

        public synchronized CommonFrame receiveCommonFrame() throws NetworkException, IOException, InvalidDataException {
            LOGGER.entering(CLASS_NAME, "receiveCommonFrame");

            CommonFrame commonFrame = parseFrame();

            if (commonFrame != null) {
                LOGGER.exiting(CLASS_NAME, "receiveCommonFrame", commonFrame);
                return commonFrame;
            }

            for (;;) {
                int count = readBytes();
                if (count == -1) {
                    LOGGER.exiting(CLASS_NAME, "receiveCommonFrame", null);
                    return null;
                }

                commonFrame = parseFrame();

                if (commonFrame != null) {
                    LOGGER.exiting(CLASS_NAME, "receiveCommonFrame", commonFrame);
                    return commonFrame;
                }

                validateBufferSize();
            }
        }
    }
}
