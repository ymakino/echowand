package echowand.net;

/**
 * ネットワークに転送を行うフレーム形式
 * @author Yoshiki Makino
 */
public class Frame {
    private Node sender;
    private Node receiver;
    private CommonFrame commonFrame;
    private Connection connection;
    
    /**
     * Frameを生成する。
     * @param sender 送信ノード
     * @param receiver 受信ノード
     * @param commonFrame 共通フレーム
     */
    public Frame(Node sender, Node receiver, CommonFrame commonFrame) {
        this.sender = sender;
        this.receiver = receiver;
        this.commonFrame = commonFrame;
        this.connection = null;
    }
    
    /**
     * Frameを生成する。
     * @param sender 送信ノード
     * @param receiver 受信ノード
     * @param commonFrame 共通フレーム
     * @param connection コネクション情報
     */
    public Frame(Node sender, Node receiver, CommonFrame commonFrame, Connection connection) {
        this.sender = sender;
        this.receiver = receiver;
        this.commonFrame = commonFrame;
        this.connection = connection;
    }
    
    /**
     * 利用するコネクション情報を登録し、以前登録されていたコネクション情報を返す。
     * @param connection 登録するコネクション情報
     * @return 以前のコネクション情報
     */
    public Connection setConnection(Connection connection) {
        Connection lastConnection = this.connection;
        this.connection = connection;
        return lastConnection;
    }
    
    /**
     * 登録されているコネクション情報を返す。
     * @return コネクション情報
     */
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * このFrameから共通フレームを取得する。
     * @return 共通フレーム
     */
    public CommonFrame getCommonFrame() {
        return commonFrame;
    }
    
    /**
     * このフレームの受信ノードを返す。
     * @return 受信ノード
     */
    public Node getReceiver() {
        return receiver;
    }
    
    /**
     * このフレームの送信ノードを返す。
     * @return 送信ノード
     */
    public Node getSender() {
        return sender;
    }
    
    /**
     * このフレームを文字列で表現する
     * @return フレームの文字列表現
     */
    @Override
    public String toString() {
        String format = "[Sender=%s Receiver=%s %s]";
        return String.format(format, sender, receiver, commonFrame);
    }
}
