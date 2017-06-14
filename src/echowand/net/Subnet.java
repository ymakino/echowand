package echowand.net;

/**
 * ECHONET Liteのサブネット
 * @author Yoshiki Makino
 */
public interface Subnet {
    /**
     * このサブネットにフレームを転送する。
     * @param frame 送信するフレーム
     * @throws SubnetException 送信に失敗した場合
     */
    void send(Frame frame) throws SubnetException;
    
    /**
     * このサブネットからフレームを受信する。
     * 受信を行うまで待機する。
     * @return 受信したフレーム
     * @throws SubnetException 受信に失敗した場合
     */
    Frame receive() throws SubnetException;
    
    /**
     * このサブネットに含まれるローカルノードを表すNodeを返す。
     * @return ローカルノードのNode
     */
    Node getLocalNode();
    
    /**
     * このサブネットに含まれるリモートノードを表すNodeを返す。
     * @param name リモートノードの名前
     * @return リモートノードのNode
     * @throws SubnetException 適切な名前が指定されなかった場合
     */
    Node getRemoteNode(String name) throws SubnetException;
    
    /**
     * このサブネットに含まれるリモートノードを表すNodeを返す。
     * @param nodeInfo リモートノードの情報
     * @return リモートノードのNode
     * @throws SubnetException 適切なNodeInfoが指定されなかった場合
     */
    Node getRemoteNode(NodeInfo nodeInfo) throws SubnetException;
    
    /**
     * このサブネットに含まれるグループを表すNodeを返す。
     * @return グループのNode
     */
    Node getGroupNode();
    
    /**
     * このSubnetの処理を開始する。
     * @return 処理の開始に成功した場合はtrue、すでに処理が開始していた場合にはfalse
     * @throws SubnetException 処理の開始に失敗した場合
     */
    boolean startService() throws SubnetException;
    
    /**
     * このSubnetの処理を停止する。
     * @return 処理の停止に成功した場合はtrue、すでに処理が停止していた場合にはfalse
     * @throws SubnetException 処理の停止に失敗した場合
     */
    boolean stopService() throws SubnetException;
    
    /**
     * このSubnetが処理中であるかを返す。
     * @return 処理中であればtrue、そうでなければfalse
     */
    boolean isInService();
}
