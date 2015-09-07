package echowand.service;

import echowand.net.Frame;

/**
 * CaptureSubnetでキャプチャされたフレームを処理
 * @author ymakino
 */
public interface CaptureSubnetObserver {
    /**
     * 送信フレームを処理する。
     * @param frame 送信したフレーム
     * @param success 送信の成否
     */
    public void notifySent(Frame frame, boolean success);
    
    /**
     * 受信フレームを処理する。
     * @param frame 受信したフレーム
     */
    public void notifyReceived(Frame frame);
}
