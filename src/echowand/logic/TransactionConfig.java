package echowand.logic;

import echowand.common.EOJ;
import echowand.common.ESV;
import echowand.net.Node;
import echowand.net.StandardPayload;

/**
 * トランザクションの詳細設定を行なう抽象クラス
 * @author Yoshiki Makino
 */
public abstract class TransactionConfig {
    private Node senderNode;
    private Node receiverNode;
    private EOJ sourceEOJ;
    private EOJ destinationEOJ;
    
    /**
     * TransactionConfigを生成する。
     */
    public TransactionConfig() {}
    
    /**
     * トランザクションのリクエストフレームのESVを返す。
     * @return リクエストのESV
     */
    public abstract ESV getESV();
    
    /**
     * トランザクションのリクエストで送信を行なうフレーム数を返す。
     * @return リクエストのフレーム数
     */
    public abstract int getCountPayloads();
    
    /**
     * 指定したStandardPayloadにプロパティを追加して、リクエストのためのStandardPayloadを生成する。
     * @param index フレームの番号
     * @param payload プロパティを追加するStandardPayload
     */
    public abstract void addPayloadProperties(int index, StandardPayload payload);
    
    /**
     * 送信ノードを設定する。
     * @param senderNode 送信ノード
     */
    public void setSenderNode(Node senderNode) {
        this.senderNode = senderNode;
    }
    
    /**
     * 送信ノードを取得する。
     * @return 送信ノード
     */
    public Node getSenderNode() {
        return senderNode;
    }
    /**
     * 受信ノードを設定する。
     * @param receiverNode 受信ノード
     */
    public void setReceiverNode(Node receiverNode) {
        this.receiverNode = receiverNode;
    }
    
    /**
     * 受信ノードを取得する。
     * @return 受信ノード
     */
    public Node getReceiverNode() {
        return receiverNode;
    }
    
    /**
     * 送信元EOJを設定する。
     * @param sourceEOJ 送信元EOJ
     */
    public void setSourceEOJ(EOJ sourceEOJ) {
        this.sourceEOJ = sourceEOJ;
    }
    
    /**
     * 送信元EOJを取得する。
     * @return 送信元EOJ
     */
    public EOJ getSourceEOJ() {
        return sourceEOJ;
    }
    
    /**
     * 宛先EOJを設定する
     * @param destinationEOJ 宛先EOJ
     */
    public void setDestinationEOJ(EOJ destinationEOJ) {
        this.destinationEOJ = destinationEOJ;
    }
    
    /**
     * 宛先EOJを取得する。
     * @return 宛先EOJ
     */
    public EOJ getDestinationEOJ() {
        return destinationEOJ;
    }
}
