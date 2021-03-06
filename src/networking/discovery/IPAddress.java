package networking.discovery;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Class to get the LAN IP for a user
 *
 * @author Matthew Walters
 */
public class IPAddress {

	/**
	 * Get the LAN IP for the current machine
	 *
	 * @return LAN IP
	 */
	public static String getLAN() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					String ip = addr.getHostAddress();

					// System.out.println("name is:"+iface.getDisplayName());
					if (Inet4Address.class == addr.getClass() && !ip.contains("192.168.56")
							&& !iface.getDisplayName().toLowerCase().contains("virtualbox"))
						return ip;
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return "";
	}
}