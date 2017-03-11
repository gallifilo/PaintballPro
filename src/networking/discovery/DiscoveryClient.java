package networking.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Class to listen out for servers in the LAN
 * 
 * @author Matthew Walters
 */
public class DiscoveryClient extends Thread{
	
	public String retVal = "";
	
	/**
	 * Get the IP address and port of the first server found
	 * 
	 * @return IP address and port, separated by a colon
	 */
	public String findServer() {
		
		try {
			InetAddress broadcastAddress = InetAddress.getByName("225.0.0.1");
			System.setProperty("java.net.preferIPv4Stack", "true");
			MulticastSocket socket = new MulticastSocket(25566);
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	        while (networkInterfaces.hasMoreElements()) {
	            NetworkInterface iface = networkInterfaces.nextElement();
	            try {
	            	if (!iface.isLoopback())
	            	{
						socket.setNetworkInterface(iface);
	            	}
	            } catch (IOException e) {
	            	//e.printStackTrace();
	            }
	        }
			socket.joinGroup(broadcastAddress);
			
			byte[] buf = new byte[1023];
			DatagramPacket packetFromServer = new DatagramPacket(buf, buf.length);
//			if(!socket.isConnected())
//			{
//				socket.close();
//				return "";
//			}
			
			socket.receive(packetFromServer);
			String data = new String(packetFromServer.getData(), packetFromServer.getOffset(),
					packetFromServer.getLength());
			socket.leaveGroup(broadcastAddress);
			socket.close();
			return data;
		} catch (Exception e) {
			System.err.println("Socket Client Exception!" + e);
		}
		return "";
	}
	
	/**
	 * Run the main method of this thread.
	 */
	public void run()
	{
		retVal = this.findServer();
	}
}