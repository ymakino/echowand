package echowand.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

/**
 *
 * @author Yoshiki Makino
 */
public class Inet6SubnetTest extends Inet4SubnetTest {
    
    public InetSubnet newInetSubnet() throws SubnetException {
        return new Inet6Subnet();
    }
    
    public InetSubnet newInetSubnet(InetAddress addr) throws SubnetException {
        return new Inet6Subnet((Inet6Address)addr);
    }
    
    public InetSubnet newInetSubnet(NetworkInterface nif) throws SubnetException {
        return new Inet6Subnet(nif);
    }
    
    public boolean isValidAddress(InetAddress addr) {
        return addr instanceof Inet6Address;
    }
    
    public InetAddress getLocalAddress() throws UnknownHostException {
        return InetAddress.getByName(localAddress6);
    }
    
    public InetAddress getRemoteAddress() throws UnknownHostException {
        return InetAddress.getByName(remoteAddress6);
    }
    
    public InetAddress getInvalidAddress() throws UnknownHostException {
        return InetAddress.getByName(localAddress4);
    }
}
