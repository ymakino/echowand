package echowand.logic;

import echowand.net.Frame;
import echowand.net.Subnet;

/**
 * 受信したフレームを処理するオブジェクトのためのインタフェース
 * @author Yoshiki Makino
 */
public interface Listener {
    /**
     * 受信したフレームの処理を行なう。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean process(Subnet subnet, Frame frame, boolean processed);
}
