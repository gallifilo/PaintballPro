import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
// helper from http://stackoverflow.com/questions/8083479/java-getting-my-ip-address
public class testIP {

	public static void main(String[] args) {
		System.out.println(getIP());
	}
	
	public static String getIP()
	{
		try {
		    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		    while (interfaces.hasMoreElements()) {
		        NetworkInterface iface = interfaces.nextElement();
		        if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
		            continue;

		        Enumeration<InetAddress> addresses = iface.getInetAddresses();
		        //int skip = 0;
		        while(addresses.hasMoreElements()) {
		            InetAddress addr = addresses.nextElement();

		            String ip = addr.getHostAddress();
		            
		            if(Inet4Address.class == addr.getClass()) 
		            	return ip;
		        }
		    }
		} catch (SocketException e) {
		    throw new RuntimeException(e);
		}
		return "";
	}

}