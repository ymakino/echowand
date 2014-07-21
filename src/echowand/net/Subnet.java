package echowand.net;

/**
 * ECHONET Liteのサブネット
 * @author Yoshiki Makino
 */
public interface Subnet {
    /**
     * このサブネットにフレームを転送する。
     * @param frame 送信するフレーム
     * @return 送信に成功した場合にはtrue、そうでなければfalse
     * @throws SubnetException 送信に失敗した場合
     */
    public boolean send(Frame frame) throws SubnetException;
    
    /**
     * このサブネットからフレームを受信する。
     * 受信を行うまで待機する。
     * @return 受信したFrame
     * @throws SubnetException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    public Frame receive() throws SubnetException;
    
    /**
     * このサブネットに含まれるローカルノードを表すNodeを返す。
     * @return ローカルノードのNode
     */
    public Node getLocalNode();
    
    /**
     * このサブネットに含まれるリモートノードを表すNodeを返す。
     * @param nodeInfo リモートノードの情報
     * @return リモートノードのNode
     * @throws SubnetException 適切なNodeInfoが指定されなかった場合
     */
    public Node getRemoteNode(NodeInfo nodeInfo) throws SubnetException;
    
    /**
     * このサブネットに含まれるグループを表すNodeを返す。
     * @return グループのNode
     */
    public Node getGroupNode();
}
