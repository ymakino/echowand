package echowand.net;

/**
 * ネットワークに転送を行うフレーム形式
 * @author Yoshiki Makino
 */
public class Frame {
    private Node sender;
    private Node receiver;
    private CommonFrame frame;
    
    /**
     * Frameを生成する。
     * @param sender 送信ノード
     * @param receiver 受信ノード
     * @param frame 共通フレーム
     */
    public Frame(Node sender, Node receiver, CommonFrame frame) {
        this.sender = sender;
        this.receiver = receiver;
        this.frame = frame;
    }
    
    /**
     * このFrameから共通フレームを取得する。
     * @return 共通フレーム
     */
    public CommonFrame getCommonFrame() {
        return frame;
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
        return String.format(format, sender, receiver, frame);
    }
}
