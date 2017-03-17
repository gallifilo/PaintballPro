package networking.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

import networking.discoveryNew.IPAddress;

/**
 * Class to announce a server's presence to a LAN
 * 
 * @author Matthew Walters
 */
public class DiscoveryServerAnnouncer extends Thread {

	private int portNo;
	public boolean m_running = true;

	/**
	 * Create a new announcer
	 * @param portNo TCP port that the game is running on
	 */
	public DiscoveryServerAnnouncer(int portNo) {
		this.portNo = portNo;
	}

	/**
	 * Run the announcer
	 */
	@Override
	public void run() {
		try {

			int serverGamePort = portNo;
			String messageToClients = IPAddress.getLAN() + ":" + serverGamePort;
			
			System.out.println("Sending:"+messageToClients);

			InetAddress broadcastAddress = InetAddress.getByName("225.0.0.1");
			System.setProperty("java.net.preferIPv4Stack", "true");
			MulticastSocket socket = new MulticastSocket(25561);
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface iface = networkInterfaces.nextElement();
		        Enumeration<InetAddress> addresses = iface.getInetAddresses(); 
		        //int skip = 0;
		        while(addresses.hasMoreElements()) {
		            InetAddress addr = addresses.nextElement();

		            String ip = addr.getHostAddress();
		            
		            if(Inet4Address.class == addr.getClass() && !ip.contains("192.")) 
		            {
		            	try {

		            		if (!iface.isLoopback())
		            		{
		            			socket.setNetworkInterface(iface);
		            			System.out.println("Socket set to:"+ip);
		            		}
		            	} catch (IOException e) {
		            		//e.printStackTrace();
		            	}
		            }
		        }
			}
			socket.joinGroup(broadcastAddress);

			// Keep broadcasting the server, every 5 seconds.
			while (m_running) {
				DatagramPacket broadcast = new DatagramPacket(messageToClients.getBytes(), messageToClients.length(), broadcastAddress, 25561);
				socket.send(broadcast);
				Thread.sleep(5000);
			}
			socket.close();
		} catch (Exception e) {
			//
		}
	}
}