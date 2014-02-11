package echowand.net;

/**
 * TCPコネクションが外部に情報を伝達するためのオブザーバ
 * @author ymakino
 */
public interface TCPConnectionObserver {
    
    /**
     * 新たに外部からフレームを受信した時の処理を行う。
     * @param connection フレームを受信したコネクション
     * @param commonFrame 受信したフレーム
     */
    public void notifyReceived(TCPConnection connection, CommonFrame commonFrame);
    
    /**
     * 外部にフレームを送信した時の処理を行う。
     * @param connection フレームを送信したコネクション
     * @param commonfram 送信したフレーム
     */
    public void notifySent(TCPConnection connection, CommonFrame commonfram);
    
    /**
     * コネクションの接続をクローズした時の処理を行う。
     * @param connection クロースしたコネクション
     */
    public void notifyClosed(TCPConnection connection);
}
