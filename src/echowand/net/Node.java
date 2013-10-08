package echowand.net;

/**
 * サブネット内に存在するNodeの表現
 * @author Yoshiki Makino
 */
public interface Node {
    
    /**
     * このNodeの情報を返す。
     * @return このNodeの情報
     */
    public NodeInfo getNodeInfo();
    
    /**
     * 指定されたサブネットにこのNodeが含まれるかどうかを返す。
     * @param subnet サブネットの指定
     * @return 指定されたサブネットに含まれる場合にはtrue、そうでなければfalse
     */
    public boolean isMemberOf(Subnet subnet);
}
