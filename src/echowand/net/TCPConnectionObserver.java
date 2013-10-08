package echowand.net;

/**
 * TCPコネクションが外部に情報を伝達するためのオブザーバ
 * @author ymakino
 */
public interface TCPConnectionObserver {
    
    /**
     * アクティブに外部への接続を開始した時の処理を行う。
     * @param connection 新たに受け付けた接続
     */
    public void notifyConnected(TCPConnection connection);
    
    /**
     * パッシブに新たに外部からの接続を受け付けた時の処理を行う。
     * @param connection 新たに受け付けた接続
     */
    public void notifyAccepted(TCPConnection connection);
    
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
