package echowand.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * IPv6ネットワークのサブネット
 * @author Yoshiki Makino
 */
public class Inet6Subnet extends InetSubnet {
    private static final Logger LOGGER = Logger.getLogger(Inet6Subnet.class.getName());
    private static final String CLASS_NAME = Inet6Subnet.class.getName();
    
    /**
     * IPv6ループバックアドレス
     */
    public static final String LOOPBACK_ADDRESS = "::1";
    
    /**
     * ECHONET Liteが利用するIPv6マルチキャストアドレス
     */
    public static final String MULTICAST_ADDRESS = "ff02::1";
    
    
    /**
     * Inet6Subnetを生成する。
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet6Subnet() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Inet6Subnet");
        
        try {
            Inet6Address loopbackAddress = (Inet6Address)Inet6Address.getByName(LOOPBACK_ADDRESS);
            Inet6Address multicastAddress = (Inet6Address)Inet6Address.getByName(MULTICAST_ADDRESS);
            
            initialize(loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet6Subnet");
    }

    /**
     * Inet6Subnetを生成する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet6Subnet(Inet6Address localAddress) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Inet6Subnet", localAddress);
        
        if (localAddress == null) {
            SubnetException exception = new SubnetException("invalid address: " + localAddress);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        try {
            Inet6Address loopbackAddress = (Inet6Address)Inet6Address.getByName(LOOPBACK_ADDRESS);
            Inet6Address multicastAddress = (Inet6Address)Inet6Address.getByName(MULTICAST_ADDRESS);
            
            initialize(localAddress, new LinkedList<NetworkInterface>(), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet6Subnet");
    }

    /**
     * Inet6Subnetを生成する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * また、必要があれば受信用のネットワークインタフェースも指定する。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @param receiverInterfaces 受信用ネットワークインタフェース
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet6Subnet(Inet6Address localAddress, NetworkInterface... receiverInterfaces) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Inet6Subnet", new Object[]{localAddress, receiverInterfaces});
        
        if (localAddress == null) {
            SubnetException exception = new SubnetException("invalid address: " + localAddress);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        try {
            Inet6Address loopbackAddress = (Inet6Address)Inet6Address.getByName(LOOPBACK_ADDRESS);
            Inet6Address multicastAddress = (Inet6Address)Inet6Address.getByName(MULTICAST_ADDRESS);
            
            initialize(localAddress, Arrays.asList(receiverInterfaces), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet6Subnet");
    }
    
    /**
     * Inet6Subnetを生成する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * @param networkInterface 利用するネットワークインタフェース
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet6Subnet(NetworkInterface networkInterface) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Inet6Subnet", networkInterface);
        
        if (networkInterface == null) {
            SubnetException exception = new SubnetException("invalid network interface: " + networkInterface);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        try {
            Inet6Address loopbackAddress = (Inet6Address)Inet6Address.getByName(LOOPBACK_ADDRESS);
            Inet6Address multicastAddress = (Inet6Address)Inet6Address.getByName(MULTICAST_ADDRESS);
            
            initialize(networkInterface, new LinkedList<NetworkInterface>(), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet6Subnet");
    }
    
    /**
     * Inet6Subnetを生成する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * また、必要があれば受信用のネットワークインタフェースも指定する。
     * @param networkInterface 利用するネットワークインタフェース
     * @param receiverInterfaces 受信用ネットワークインタフェース
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet6Subnet(NetworkInterface networkInterface, NetworkInterface... receiverInterfaces) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Inet6Subnet", new Object[]{networkInterface, receiverInterfaces});
        
        if (networkInterface == null) {
            SubnetException exception = new SubnetException("invalid network interface: " + networkInterface);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        try {
            Inet6Address loopbackAddress = (Inet6Address)Inet6Address.getByName(LOOPBACK_ADDRESS);
            Inet6Address multicastAddress = (Inet6Address)Inet6Address.getByName(MULTICAST_ADDRESS);
            
            initialize(networkInterface, Arrays.asList(receiverInterfaces), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet6Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet6Subnet");
    }
    
    /**
     * Inet6Subnetを生成し、サービスを開始する。
     * @return 生成したInet6Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet6Subnet startSubnet() throws SubnetException {
        Inet6Subnet subnet = new Inet6Subnet();
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet6Subnetを生成し、サービスを開始する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @return 生成したInet6Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet6Subnet startSubnet(Inet6Address localAddress) throws SubnetException {
        Inet6Subnet subnet = new Inet6Subnet(localAddress);
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet6Subnetを生成し、サービスを開始する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * また、必要があれば受信用のネットワークインタフェースも指定する。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @param receiverInterfaces 受信用ネットワークインタフェース
     * @return 生成したInet6Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet6Subnet startSubnet(Inet6Address localAddress, NetworkInterface... receiverInterfaces) throws SubnetException {
        Inet6Subnet subnet = new Inet6Subnet(localAddress, receiverInterfaces);
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet6Subnetを生成し、サービスを開始する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * @param networkInterface 利用するネットワークインタフェース
     * @return 生成したInet6Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet6Subnet startSubnet(NetworkInterface networkInterface) throws SubnetException {
        Inet6Subnet subnet = new Inet6Subnet(networkInterface);
        subnet.startService();
        return subnet;
    }
    
    /**
     * Inet6Subnetを生成し、サービスを開始する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * また、必要があれば受信用のネットワークインタフェースも指定する。
     * @param networkInterface 利用するネットワークインタフェース
     * @param receiverInterfaces 受信用ネットワークインタフェース
     * @return 生成したInet6Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet6Subnet startSubnet(NetworkInterface networkInterface, NetworkInterface... receiverInterfaces) throws SubnetException {
        Inet6Subnet subnet = new Inet6Subnet(networkInterface, receiverInterfaces);
        subnet.startService();
        return subnet;
    }

    @Override
    public boolean isValidAddress(InetAddress address) {
        return (address instanceof Inet6Address);
    }
}
