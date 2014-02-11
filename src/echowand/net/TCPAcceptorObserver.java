package echowand.net;

/**
 * TCPAcceptorが外部に情報を伝達するためのオブザーバ
 * @author ymakino
 */
public interface TCPAcceptorObserver {
    /**
     * 新たに外部からの接続を受け付けた時に呼び出される。
     * @param connection 新たに受け付けた接続
     */
    public void notifyAccepted(TCPConnection connection);
}
