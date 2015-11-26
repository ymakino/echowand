package echowand.service;

import echowand.net.Subnet;

/**
 * Subnetに機能を追加するSubnet拡張インタフェース
 * 実際に処理を行う別のSubnetを内部に持ち、そのSubnetに機能を追加する
 * @author ymakino
 */
public interface ExtendedSubnet extends Subnet {
    
    /**
     * 内部で利用されるSubnetの中で、指定された型を持っているSubnetを返す。
     * 複数存在する場合には、最初に見つかったSubnetを返す。
     * また、指定された型のSubnetを持っていない場合にはnullを返す。
     * @param <S> 取得するSubnetの型
     * @param cls Subnetの型の指定
     * @return 指定された型のSubnet
     */
    <S extends Subnet> S getSubnet(Class<S> cls);
    
    /**
     * 実際の処理で利用するSubnetを返す。
     * @return 処理で利用するSubnet
     */
    Subnet getInternalSubnet();
}
