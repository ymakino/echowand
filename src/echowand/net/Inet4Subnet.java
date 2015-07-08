package echowand.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * IPv4ネットワークのサブネット
 * @author ymakino
 */
public class Inet4Subnet extends InetSubnet {
    
    /**
     * IPv4ループバックアドレス
     */
    public static final String LOOPBACK_ADDRESS = "127.0.0.1";
    
    /**
     * ECHONET Liteが利用するIPv4マルチキャストアドレス
     */
    public static final String MULTICAST_ADDRESS = "224.0.23.0";
    
    
    /**
     * Inet4Subnetを生成する。
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet4Subnet() throws SubnetException {
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }

    /**
     * Inet4Subnetを生成する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet4Subnet(Inet4Address localAddress) throws SubnetException {
        if (localAddress == null) {
            throw new SubnetException("invalid address: " + localAddress);
        }
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(localAddress, new LinkedList<NetworkInterface>(), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }

    /**
     * Inet4Subnetを生成する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * また、必要があれば受信用のネットワークインタフェースの指定も行う。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @param receiverInterfaces 受信用ネットワークインタフェース
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet4Subnet(Inet4Address localAddress, NetworkInterface... receiverInterfaces) throws SubnetException {
        if (localAddress == null) {
            throw new SubnetException("invalid address: " + localAddress);
        }
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(localAddress, Arrays.asList(receiverInterfaces), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    /**
     * Inet4Subnetを生成する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * @param networkInterface 利用するネットワークインタフェース
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet4Subnet(NetworkInterface networkInterface) throws SubnetException {
        if (networkInterface == null) {
            throw new SubnetException("invalid network interface: " + networkInterface);
        }
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(networkInterface, new LinkedList<NetworkInterface>(), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    /**
     * Inet4Subnetを生成する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * また、必要があれば受信用のネットワークインタフェースの指定も行う。
     * @param networkInterface 利用するネットワークインタフェース
     * @param receiverInterfaces 受信用ネットワークインタフェース
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet4Subnet(NetworkInterface networkInterface, NetworkInterface... receiverInterfaces) throws SubnetException {
        if (networkInterface == null) {
            throw new SubnetException("invalid network interface: " + networkInterface);
        }
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(networkInterface, Arrays.asList(receiverInterfaces), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    /**
     * Inet4Subnetを生成し、サービスを開始する。
     * @return 生成したInet4Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet4Subnet startSubnet() throws SubnetException {
        Inet4Subnet subnet = new Inet4Subnet();
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet4Subnetを生成し、サービスを開始する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @return 生成したInet4Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet4Subnet startSubnet(Inet4Address localAddress) throws SubnetException {
        Inet4Subnet subnet = new Inet4Subnet(localAddress);
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet4Subnetを生成し、サービスを開始する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * また、必要があれば受信用のネットワークインタフェースも指定する。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @param receiverInterfaces 受信用ネットワークインタフェース
     * @return 生成したInet4Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet4Subnet startSubnet(Inet4Address localAddress, NetworkInterface... receiverInterfaces) throws SubnetException {
        Inet4Subnet subnet = new Inet4Subnet(localAddress, receiverInterfaces);
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet4Subnetを生成し、サービスを開始する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * @param networkInterface 利用するネットワークインタフェース
     * @return 生成したInet4Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet4Subnet startSubnet(NetworkInterface networkInterface) throws SubnetException {
        Inet4Subnet subnet = new Inet4Subnet(networkInterface);
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet4Subnetを生成し、サービスを開始する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * また、必要があれば受信用のネットワークインタフェースも指定する。
     * @param networkInterface 利用するネットワークインタフェース
     * @param receiverInterfaces 受信用ネットワークインタフェース
     * @return 生成したInet4Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet4Subnet startSubnet(NetworkInterface networkInterface, NetworkInterface... receiverInterfaces) throws SubnetException {
        Inet4Subnet subnet = new Inet4Subnet(networkInterface, receiverInterfaces);
        subnet.startService();
        return subnet;
    }

    @Override
    public boolean isValidAddress(InetAddress address) {
        return (address instanceof Inet4Address);
    }
}
