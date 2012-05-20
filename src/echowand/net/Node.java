package echowand.net;

/**
 * サブネットのノード
 * @author Yoshiki Makino
 */
public interface Node {
    /**
     * 指定されたサブネットにこのNodeが含まれるかどうかを返す。
     * @param subnet サブネットの指定
     * @return 指定されたサブネットに含まれる場合にはtrue、そうでなければfalse
     */
    public boolean isMemberOf(Subnet subnet);
}
