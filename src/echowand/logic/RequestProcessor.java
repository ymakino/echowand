package echowand.logic;

import echowand.net.Frame;
import echowand.net.Subnet;

/**
 * リクエストフレームの処理を実行。
 * このクラスを継承したクラスを作成して、そのオブジェクトをRequestDispatcherに登録する。
 * @author Yoshiki Makino
 */
public class RequestProcessor {
    
    /**
     * RequestProcessorを生成する。
     */
    public RequestProcessor() {}
    /**
     * ESVがSetIであるフレームを受信した場合に呼び出される。
     * 常にfalseを返すダミーメソッドであり、SetIを処理するサブクラスではオーバーライドされる。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 常にfalse
     */
    public boolean processSetI(Subnet subnet, Frame frame, boolean processed){ return false; }
    
    /**
     * ESVがSetCであるフレームを受信した場合に呼び出される。
     * 常にfalseを返すダミーメソッドであり、SetCを処理するサブクラスではオーバーライドされる。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 常にfalse
     */
    public boolean processSetC(Subnet subnet, Frame frame, boolean processed){ return false; }
    
    /**
     * ESVがGetであるフレームを受信した場合に呼び出される。
     * 常にfalseを返すダミーメソッドであり、Getを処理するサブクラスではオーバーライドされる。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 常にfalse
     */
    public boolean processGet(Subnet subnet, Frame frame, boolean processed){ return false; }
    
    /**
     * ESVがSetGetであるフレームを受信した場合に呼び出される。
     * 常にfalseを返すダミーメソッドであり、SetGetを処理するサブクラスではオーバーライドされる。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 常にfalse
     */
    public boolean processSetGet(Subnet subnet, Frame frame, boolean processed){ return false; }
    
    /**
     * ESVがINF_REQであるフレームを受信した場合に呼び出される。
     * 常にfalseを返すダミーメソッドであり、INF_REQを処理するサブクラスではオーバーライドされる。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 常にfalse
     */
    public boolean processINF_REQ(Subnet subnet, Frame frame, boolean processed){ return false; }
    
    /**
     * ESVがINFであるフレームを受信した場合に呼び出される。
     * 常にfalseを返すダミーメソッドであり、INFを処理するサブクラスではオーバーライドされる。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 常にfalse
     */
    public boolean processINF(Subnet subnet, Frame frame, boolean processed){ return false; }
    
    /**
     * ESVがINFCであるフレームを受信した場合に呼び出される。
     * 常にfalseを返すダミーメソッドであり、INCを処理するサブクラスではオーバーライドされる。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 常にfalse
     */
    public boolean processINFC(Subnet subnet, Frame frame, boolean processed){ return false; }
}
