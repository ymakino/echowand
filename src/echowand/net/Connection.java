package echowand.net;

/**
 * コネクションのインタフェース
 * @author ymakino
 */
public interface Connection {
    
    /**
     * この接続のローカルノード情報を表すNodeInfoを返す。
     * @return ローカルノード情報
     */
    public NodeInfo getLocalNodeInfo();
    
    /**
     * この接続のリモートノード情報を表すNodeInfoを返す。
     * @return リモートノード情報
     */
    public NodeInfo getRemoteNodeInfo();
    
    /**
     * この接続が切断されているかどうかを返す。
     * @return 接続が切断されていればtrue、接続中の場合にはfalse
     */
    public boolean isClosed();
    
    /**
     * この接続を切断する。
     * @throws NetworkException エラーが発生した場合
     */
    public void close() throws NetworkException;
    
    /**
     * この接続を利用したフレームの送信を行う。
     * @param commonFrame 送信するフレーム
     * @throws NetworkException 送信に失敗した場合
     */
    public void send(CommonFrame commonFrame) throws NetworkException;
    
    /**
     * この接続を利用したフレームの受信を行う。
     * @return 受信したフレーム
     * @throws NetworkException 受信に失敗した場合
     */
    public CommonFrame receive() throws NetworkException;
}
