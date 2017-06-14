package echowand.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IPv4ネットワークのサブネット
 * @author ymakino
 */
public class Inet4Subnet extends InetSubnet {
    private static final Logger LOGGER = Logger.getLogger(Inet4Subnet.class.getName());
    private static final String CLASS_NAME = Inet4Subnet.class.getName();
    
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
        LOGGER.entering(CLASS_NAME, "Inet4Subnet");
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet4Subnet");
    }

    /**
     * Inet4Subnetを生成する。
     * localAddressにより利用するネットワークインタフェースの指定を行う。
     * @param localAddress 利用するネットワークインタフェースにつけられたアドレス
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet4Subnet(Inet4Address localAddress) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Inet4Subnet", localAddress);
        
        if (localAddress == null) {
            SubnetException exception = new SubnetException("invalid address: " + localAddress);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(localAddress, new LinkedList<NetworkInterface>(), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet4Subnet");
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
        LOGGER.entering(CLASS_NAME, "Inet4Subnet", new Object[]{localAddress, receiverInterfaces});
        
        if (localAddress == null) {
            SubnetException exception = new SubnetException("invalid address: " + localAddress);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(localAddress, Arrays.asList(receiverInterfaces), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet4Subnet");
    }
    
    /**
     * Inet4Subnetを生成する。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * @param networkInterface 利用するネットワークインタフェース
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet4Subnet(NetworkInterface networkInterface) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Inet4Subnet", networkInterface);
        
        if (networkInterface == null) {
            SubnetException exception = new SubnetException("invalid network interface: " + networkInterface);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(networkInterface, new LinkedList<NetworkInterface>(), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet4Subnet");
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
        LOGGER.entering(CLASS_NAME, "Inet4Subnet", new Object[]{networkInterface, receiverInterfaces});
        
        if (networkInterface == null) {
            SubnetException exception = new SubnetException("invalid network interface: " + networkInterface);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        try {
            Inet4Address loopbackAddress = (Inet4Address)Inet4Address.getByName(LOOPBACK_ADDRESS);
            Inet4Address multicastAddress = (Inet4Address)Inet4Address.getByName(MULTICAST_ADDRESS);
            
            initialize(networkInterface, Arrays.asList(receiverInterfaces), loopbackAddress, multicastAddress, DEFAULT_PORT_NUMBER);
        } catch (UnknownHostException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "Inet4Subnet", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "Inet4Subnet");
    }
    
    /**
     * Inet4Subnetを生成し、サービスを開始する。
     * @return 生成したInet4Subnet
     * @throws SubnetException 生成やサービス開始に失敗した場合
     */
    public static Inet4Subnet startSubnet() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "startSubnet");
        
        Inet4Subnet subnet = new Inet4Subnet();
        subnet.startService();
        
        LOGGER.exiting(CLASS_NAME, "startSubnet", subnet);
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
        LOGGER.entering(CLASS_NAME, "startSubnet", localAddress);
        
        Inet4Subnet subnet = new Inet4Subnet(localAddress);
        subnet.startService();
        
        LOGGER.exiting(CLASS_NAME, "startSubnet", subnet);
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
        LOGGER.entering(CLASS_NAME, "startSubnet", new Object[]{localAddress, receiverInterfaces});
        
        Inet4Subnet subnet = new Inet4Subnet(localAddress, receiverInterfaces);
        subnet.startService();
        
        LOGGER.exiting(CLASS_NAME, "startSubnet", subnet);
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
        LOGGER.entering(CLASS_NAME, "startSubnet", networkInterface);
        
        Inet4Subnet subnet = new Inet4Subnet(networkInterface);
        subnet.startService();
        
        LOGGER.exiting(CLASS_NAME, "startSubnet", subnet);
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
        LOGGER.entering(CLASS_NAME, "startSubnet", new Object[]{networkInterface, receiverInterfaces});
        
        Inet4Subnet subnet = new Inet4Subnet(networkInterface, receiverInterfaces);
        subnet.startService();
        
        LOGGER.exiting(CLASS_NAME, "startSubnet", subnet);
        return subnet;
    }

    @Override
    public boolean isValidAddress(InetAddress address) {
        LOGGER.entering(CLASS_NAME, "isValidAddress", address);
        
        boolean result = (address instanceof Inet4Address);
        
        LOGGER.exiting(CLASS_NAME, "isValidAddress", result);
        return result;
    }
}
