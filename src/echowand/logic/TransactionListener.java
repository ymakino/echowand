package echowand.logic;

import echowand.net.Frame;
import echowand.net.Subnet;

/**
 * トランザクションのレスポンス処理のためのインタフェース
 * @author Yoshiki Makino
 */
public interface TransactionListener {
    /**
     * トランザクションが開始された時の処理を行う。
     * @param t 開始したTransaction
     */
    public void begin(Transaction t);
    
    /**
     * 送信したリクエストフレームの処理を行う。
     * @param t リクエストを送信したTransaction
     * @param subnet リクエストを送信したサブネット
     * @param frame リクエストフレーム
     * @param success 送信に成功した場合にはtrue、失敗した場合にはfalse
     */
    public void send(Transaction t, Subnet subnet, Frame frame, boolean success);
    
    /**
     * 受信したレスポンスフレームの処理を行う。
     * @param t レスポンスを受信したTransaction
     * @param subnet レスポンスを受信したサブネット
     * @param frame レスポンスフレーム
     */
    public void receive(Transaction t, Subnet subnet, Frame frame);
    
    /**
     * トランザクション終了時の処理を行う。
     * @param t 終了したTransaction
     */
    public void finish(Transaction t);
}
