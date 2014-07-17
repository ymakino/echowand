package echowand.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

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
            
            initialize(localAddress, loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
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
            
            initialize(networkInterface, loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    /**
     * Inet4Subnetを生成し、サービスを開始する。
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
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet4Subnet startSubnet(Inet4Address localAddress) throws SubnetException {
        Inet4Subnet subnet = new Inet4Subnet(localAddress);
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet4Subnetを生成し、サービスを開始する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * @param networkInterface 利用するネットワークインタフェース
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet4Subnet startSubnet(NetworkInterface networkInterface) throws SubnetException {
        Inet4Subnet subnet = new Inet4Subnet(networkInterface);
        subnet.startService();
        return subnet;
    }

    @Override
    public boolean isValidAddress(InetAddress address) {
        return (address instanceof Inet4Address);
    }
}
